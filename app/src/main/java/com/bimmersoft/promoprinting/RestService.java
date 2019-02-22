package com.bimmersoft.promoprinting;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bimmersoft.promoprinting.print.GPrinterCommand;
import com.bimmersoft.promoprinting.print.PrintPic;
import com.bimmersoft.promoprinting.print.PrintQueue;
import com.bimmersoft.promoprinting.print.PrintUtil;
import com.bimmersoft.promoprinting.printutil.PrintOrderDataMaker;
import com.bimmersoft.promoprinting.printutil.PrinterWriter;
import com.bimmersoft.promoprinting.printutil.PrinterWriter58mm;
import com.bimmersoft.promoprinting.restserver.AsyncServer;
import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.http.NameValuePair;
import com.bimmersoft.promoprinting.restserver.http.body.JSONObjectBody;
import com.bimmersoft.promoprinting.restserver.http.body.MultipartFormDataBody;
import com.bimmersoft.promoprinting.restserver.http.body.StringBody;
import com.bimmersoft.promoprinting.restserver.http.body.UrlEncodedFormBody;
import com.bimmersoft.promoprinting.restserver.http.server.AsyncHttpServer;
import com.bimmersoft.promoprinting.restserver.http.server.AsyncHttpServerRequest;
import com.bimmersoft.promoprinting.restserver.http.server.AsyncHttpServerResponse;
import com.bimmersoft.promoprinting.restserver.http.server.HttpServerRequestCallback;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by liuguirong on 8/1/17.
 * <p/>
 * print ticket service
 */
public class RestService extends IntentService {

    public static final String ACTION_START = "start_svc";
    public static final String ACTION_STOP = "stop_svc";
    public static final String ACTION_FSTOP= "force_stop_svc";

    public RestService() {
        super("BtService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RestService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(RestService.ACTION_START)) {
            //printTest();
            runRestFul();
        } else if (intent.getAction().equals(RestService.ACTION_STOP)) {
            //printTesttwo(3);
        }else if (intent.getAction().equals(RestService.ACTION_FSTOP)) {
            //printBitmapTest();
        }

    }
    AsyncHttpServer httpServer;
    String FilePath;

    public void runRestFul() {
        Log.e("HTTP-Server", "try to Start Service");
        httpServer = new AsyncHttpServer();
        httpServer.setErrorCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                Log.e("HTTP-Server", ex.getMessage());
            }
        });
        httpServer.listen(AsyncServer.getDefault(), 5000);
        httpServer.get("/popup_print", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //assertNotNull(request.getHeaders().get("Host"));
                //assert(request.getHeaders().get("Host"));
                String str =  request.getQuery().getString("FILE");
                String icon =  request.getQuery().getString("ICON");
                PicPrintEx pc = new PicPrintEx();
                pc.printBitmapTest(getApplicationContext(),str);
                //pc.printBitmapZPl(getApplicationContext(),str);

                //response.send("<HTML><HEAD></HEAD> <BODY><IMG SRC='http://localhost:8080/"+str+"'></IMG></BODY></HTML>");
                Log.e("Debug","http://127.0.0.1:5000/printing?FILE=" + str + "&ICON=" + icon);
                response.redirect("http://127.0.0.1:5000/printing?FILE=" + str + "&ICON=" + icon);
            }
        });

        httpServer.get("/hello", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //assertNotNull(request.getHeaders().get("Host"));
                //assert(request.getHeaders().get("Host"));
                String str =  request.getQuery().getString("FILE");

                PicPrintEx pc = new PicPrintEx();
                pc.printBitmapTest(getApplicationContext(),str);
                //pc.printBitmapZPl(getApplicationContext(),str);
                response.send("<HTML><HEAD>TEST</HEAD> <BODY><H1>hello man.......<IMG SRC='"+str+"'></IMG></H1><H1>"+ FilePath +"</H1><INPUT ID='TTT' TYPE='BUTTON' VALUE='TTTETETET'> </INPUT></BODY></HTML>");
            }
        });

        httpServer.get("/printing", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //assertNotNull(request.getHeaders().get("Host"));
                //assert(request.getHeaders().get("Host"));
                //print("\r\n! U1 setvar \"device.languages\" \"zpl\"\r\n".getBytes());
                //print("\r\n! U1 setvar \"zpl.label_length\" \"300\"\r\n".getBytes());
                //senzpl();
                String str =  request.getQuery().getString("FILE");
                String str_icon =  request.getQuery().getString("ICON");

                //PicPrintEx pc = new PicPrintEx();
                //pc.printBitmapTest(getApplicationContext(),str);
                String html_text = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "<A HREF=\"http://127.0.0.1:5000/popup_print?FILE="+ str +"&ICON="+str_icon+"\" >\n" +
                        "<img src=\"http://127.0.0.1:8080/"+str_icon+"\" ></img>\n" +
                        "<!-- <button onclick=\"myFunction()\">Print this page</button> -->\n" +
                        "</A>\n" +
                        "<script>\n" +
                        "\n" +
                        "function myFunction() {\n" +
                        "  window.print();\n" +
                        "}\n" +
                        "\n" +
                        "</script>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>";
                Log.e("", "Printing....... ");
                Log.e("Debug","http://127.0.0.1:5000/popup_print?FILE=" + str + "&ICON=" + str_icon);
                //response.redirect("http://127.0.0.1:8080/print.html");
                response.send(html_text);
            }
        });

        httpServer.post("/echo", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                try {
                    //assertNotNull(request.getHeaders().get("Host"));
                    JSONObject json = new JSONObject();
                    if (request.getBody() instanceof UrlEncodedFormBody) {
                        UrlEncodedFormBody body = request.getBody();
                        for (NameValuePair pair : body.get()) {
                            json.put(pair.getName(), pair.getValue());
                        }
                    } else if (request.getBody() instanceof JSONObjectBody) {
                        json = ((JSONObjectBody) request.getBody()).get();
                    } else if (request.getBody() instanceof StringBody) {
                        json.put("foo", ((StringBody) request.getBody()).get());
                    } else if (request.getBody() instanceof MultipartFormDataBody) {
                        MultipartFormDataBody body = request.getBody();
                        for (NameValuePair pair : body.get()) {
                            json.put(pair.getName(), pair.getValue());
                        }
                    }

                    response.send(json);
                } catch (Exception e) {
                }
            }
        });
    }

}
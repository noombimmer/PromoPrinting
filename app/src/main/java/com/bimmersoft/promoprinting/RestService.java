package com.bimmersoft.promoprinting;

import android.Manifest;
import android.Manifest.permission;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by liuguirong on 8/1/17.
 * <p/>
 * print ticket service
 */
public class RestService extends IntentService {

    public static final String ACTION_START = "start_svc";
    public static final String ACTION_STOP = "stop_svc";
    public static final String ACTION_FSTOP= "force_stop_svc";
    int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    int READ_STORAGE_PERMISSION_REQUEST_CODE = 0x3;

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
                Log.e("Debug","/printing?FILE=" + str + "&ICON=" + icon);
                response.redirect("/printing?FILE=" + str + "&ICON=" + icon);
            }
        });

        httpServer.get("/file", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String str =  request.getQuery().getString("FILE");
                Log.e("Debug","/file?FILE=" + str );

                File fs = new File("/storage/emulated/0/" + str);
                String mimeType = getMimeType("/storage/emulated/0/" + str);
                Log.e("Debug", (("File size:" + fs.length())));
                FileInputStream fileStream = null;
                try {
                    fileStream = new FileInputStream(fs);
                    byte[] bs = new byte[(int) fs.length()];
                    int read = fileStream.read(bs, 0, bs.length);
                    response.send(mimeType,bs);
                    Log.e("Debug", (("Send File completed")));
                } catch (FileNotFoundException e) {
                    Log.e("Debug FileNotFoundException", e.getMessage());
                }catch (IOException e) {
                    Log.e("Debug IOException", e.getMessage());
                }catch (Exception e) {
                    Log.e("Debug Exception", e.getMessage());
                }


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
                        "<A HREF=\"/popup_print?FILE="+ str +"&ICON="+str_icon+"\" >\n" +
                        "<img src=\"/file?FILE="+str_icon+"\" ></img>\n" +
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
                Log.e("Debug","/popup_print?FILE=" + str + "&ICON=" + str_icon);
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
    private String getMimeType(String filePath) {
        String mimeType = AsyncHttpServer.getContentType(filePath);
        Log.e("MimeType:",mimeType);
        if ("text/plain".equals(mimeType)) {
            if (filePath.endsWith(".mp3")) {
                mimeType = "audio/mp3";
            } else if (filePath.contains(".mp4?")) {
                mimeType = "video/mp4";
            }
        }

        return mimeType;
    }

}
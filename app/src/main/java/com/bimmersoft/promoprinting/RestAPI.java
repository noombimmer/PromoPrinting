package com.bimmersoft.promoprinting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RestAPI extends Service {
    public int PRINT_MODE;
    public RestAPI() {

        PRINT_MODE = PRNT_ZPL_MODE;
    }
    final static public int PRNT_ZPL_MODE =0x01;
    final static public int PRNT_ESC_MODE = 0x02;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        runRestFul();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        httpServer.stop();
        super.onDestroy();
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }
    public static void stop_listen(){
        if(httpServer != null){
            httpServer.stop();

        }

    }
    public static AsyncHttpServer httpServer;

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
                if (PRINT_MODE == PRNT_ESC_MODE) {
                    /*for ESC Command */
                    pc.printBitmapTest(getApplicationContext(), str);
                    Log.i("Info","Print ESC Mode.");
                }else if (PRINT_MODE == PRNT_ZPL_MODE ){
                    /*for ZPL command*/
                    pc.printBitmapZPl(getApplicationContext(),str);
                    Log.i("Info","Print ZPL Mode.");
                }

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
        httpServer.get("/printing", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String str =  request.getQuery().getString("FILE");
                String str_icon =  request.getQuery().getString("ICON");
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
//
//        httpServer.post("/echo", new HttpServerRequestCallback() {
//            @Override
//            public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
//                try {
//                    //assertNotNull(request.getHeaders().get("Host"));
//                    JSONObject json = new JSONObject();
//                    if (request.getBody() instanceof UrlEncodedFormBody) {
//                        UrlEncodedFormBody body = request.getBody();
//                        for (NameValuePair pair : body.get()) {
//                            json.put(pair.getName(), pair.getValue());
//                        }
//                    } else if (request.getBody() instanceof JSONObjectBody) {
//                        json = ((JSONObjectBody) request.getBody()).get();
//                    } else if (request.getBody() instanceof StringBody) {
//                        json.put("foo", ((StringBody) request.getBody()).get());
//                    } else if (request.getBody() instanceof MultipartFormDataBody) {
//                        MultipartFormDataBody body = request.getBody();
//                        for (NameValuePair pair : body.get()) {
//                            json.put(pair.getName(), pair.getValue());
//                        }
//                    }
//
//                    response.send(json);
//                } catch (Exception e) {
//                }
//            }
//        });
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

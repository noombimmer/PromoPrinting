package com.bimmersoft.promoprinting.restserver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.bimmersoft.promoprinting.printutil.PicPrintEx;
import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.http.server.AsyncHttpServer;
import com.bimmersoft.promoprinting.restserver.http.server.AsyncHttpServerRequest;
import com.bimmersoft.promoprinting.restserver.http.server.AsyncHttpServerResponse;
import com.bimmersoft.promoprinting.restserver.http.server.HttpServerRequestCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RestAPI extends Service {
    private Context ctx;
    public static int PRINT_MODE;
    private static final String TAG = RestAPI.class.getSimpleName();
    public static int mZPLWidth;
    public static int mZPLHeight;
    public RestAPI(Context applicationContext) {
        super();
        ctx = applicationContext;
        Log.i("HERE", "here I am!");
    }

    public RestAPI() {

        PRINT_MODE = PRNT_ESC_MODE;
    }
    final static public int PRNT_ZPL_MODE =0x01;
    final static public int PRNT_ESC_MODE = 0x02;
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "serviceonTaskRemoved()");



        // workaround for kitkat: set an alarm service to trigger service again
        Intent intent = new Intent(getApplicationContext(), RestAPI.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);

        super.onTaskRemoved(rootIntent);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        super.onStartCommand(intent, flags, startId);
        startTimer();
        runRestFul();
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        //httpServer.stop();
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
        Log.i("EXIT", "ondestroy!");
        //Intent broadcastIntent = new Intent(ctx, RestAPIBroadcastRec.class);
        Intent broadcastIntent = new Intent("com.bimmersoft.promoprinting.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
        //super.onDestroy();
    }
    public int counter=0;

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
                    Log.w("Info","Print ESC Mode.");
                }else if (PRINT_MODE == PRNT_ZPL_MODE ){
                    /*for ZPL command*/
                    pc.printBitmapZPl(getApplicationContext(),str);
                    Log.w("Info","Print ZPL Mode.");
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
        httpServer.get("/file_print", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                String fn =  request.getQuery().getString("FILE");
                String str;
                str = "/storage/emulated/0/" + fn;
                Log.e("onRequest","request.getQuery() :" + request.getQuery().toString());
                //String icon =  request.getQuery().getString("ICON");
                PicPrintEx pc = new PicPrintEx();
                String mimeType = getMimeType("/storage/emulated/0/" + fn);

                if (PRINT_MODE == PRNT_ESC_MODE) {
                    /*for ESC Command */
                    //pc.printBitmapTest(getApplicationContext(), str);
                    Log.e("onRequest","Convert file :" + str);


                    //ConvertInBackground convert = new ConvertInBackground();
                    //convert.execute(str);

                    byte[] data = pc.printBitmaptoByte(getApplicationContext(), str);

                    pc.saveImage("output",data,mZPLWidth,mZPLHeight);
                    //response.send(mimeType,pc.printBitmaptoFile(getApplicationContext(), str));

//                    try (FileOutputStream out = new FileOutputStream(filename)) {
//                        bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//                        // PNG is a lossless format, the compression factor (100) is ignored
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    response.redirect("/file?FILE=output.bmp&ORG=" + fn );
                    Log.e("Info","Print ESC Mode.");
                    //response.send("OK");
                }else if (PRINT_MODE == PRNT_ZPL_MODE ){
                    /*for ZPL command*/

                    response.send(mimeType,pc.printBitmapZPlToFile(getApplicationContext(),str));
                    //pc.printBitmapZPl(getApplicationContext(),str);
                    Log.w("Info","Print ZPL Mode.");


                }

                //response.send("<HTML><HEAD></HEAD> <BODY><IMG SRC='http://localhost:8080/"+str+"'></IMG></BODY></HTML>");
                //Log.e("Debug","/printing?FILE=" + str + "&ICON=" + icon);
                //response.redirect("/printing?FILE=" + str + "&ICON=" + icon);


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

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 5000, 5000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ConvertInBackground extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            PicPrintEx pc = new PicPrintEx();
            Log.e("ConvertInBackground:doInBackground","param[0]:" + params[0]);
            pc.saveImage("output.png",pc.printBitmaptoBitmap(getApplicationContext(), params[0]));

            return null;
        }





    }
}

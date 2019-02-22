package com.bimmersoft.promoprinting;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bimmersoft.promoprinting.base.AppInfo;
import com.bimmersoft.promoprinting.bt.BluetoothActivity;
import com.bimmersoft.promoprinting.print.PrintMsgEvent;
import com.bimmersoft.promoprinting.print.PrintUtil;
import com.bimmersoft.promoprinting.print.PrinterMsgType;
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
import com.bimmersoft.promoprinting.util.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/***
 *  Created by liugruirong on 2017/8/3.
 */
public class MainActivity extends BluetoothActivity implements View.OnClickListener {

    TextView tv_bluename;
    TextView tv_blueadress;
    boolean mBtEnable = true;
    int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    int READ_STORAGE_PERMISSION_REQUEST_CODE = 0x3;
    /**
     * bluetooth adapter
     */
    BluetoothAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_bluename = findViewById(R.id.tv_bluename);
        tv_blueadress = findViewById(R.id.tv_blueadress);
        findViewById(R.id.btn_setting).setOnClickListener(this);
        findViewById(R.id.btnPrintTest1).setOnClickListener(this);
        findViewById(R.id.btnPrintTest1).setOnClickListener(this);
        findViewById(R.id.btn_print_img).setOnClickListener(this);
        findViewById(R.id.btnStopSVC).setOnClickListener(this);
        findViewById(R.id.btnStartSVC).setOnClickListener(this);
        //6.0以上的手机要地理位置权限
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
//        }

        EventBus.getDefault().register(MainActivity.this);
        FilePath = Environment.getExternalStorageDirectory().getPath();
        //runRestFul();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothController.init(this);
    }



    @Override
    public void btStatusChanged(Intent intent) {
        super.btStatusChanged(intent);
        BluetoothController.init(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                startActivity(new Intent(MainActivity.this, SearchBluetoothActivity.class));
                break;
            case R.id.btnPrintTest1:
                if (TextUtils.isEmpty(AppInfo.btAddress)) {
                    ToastUtil.showToast(MainActivity.this, "Please connect to Bluetooth...");
                    startActivity(new Intent(MainActivity.this, SearchBluetoothActivity.class));
                } else {
                    if (mAdapter.getState() == BluetoothAdapter.STATE_OFF) {//蓝牙被关闭时强制打开
                        mAdapter.enable();
                        ToastUtil.showToast(MainActivity.this, "Bluetooth is turned off, please turn it on...");
                    } else {
                        ToastUtil.showToast(MainActivity.this, "Print test...");
                        Intent intent = new Intent(getApplicationContext(), BtService.class);
                        intent.setAction(PrintUtil.ACTION_PRINT_TEST);
                        startService(intent);
                    }

                }
                break;
            case R.id.btnPrintTest2:
                if (TextUtils.isEmpty(AppInfo.btAddress)) {
                    ToastUtil.showToast(MainActivity.this, "Please connect to Bluetooth...");
                    startActivity(new Intent(MainActivity.this, SearchBluetoothActivity.class));
                } else {
                    ToastUtil.showToast(MainActivity.this, "Print test...");
                    Intent intent2 = new Intent(getApplicationContext(), BtService.class);
                    intent2.setAction(PrintUtil.ACTION_PRINT_TEST_TWO);
                    startService(intent2);

                }
                break;
            case R.id.btnStopSVC:
                Log.e("Test","BTN Stop ");
                break;

            case R.id.btnStartSVC:
                Log.e("Test","BTN Start ");
                ToastUtil.showToast(MainActivity.this, "Start Rest Services.......");
                Intent intent2 = new Intent(getApplicationContext(), RestService.class);
                intent2.setAction(RestService.ACTION_START);
                startService(intent2);
                break;
            case R.id.btn_print_img:
                PicPrintEx pc = new PicPrintEx();
                //pc.printBitmapTest(getApplicationContext(),"Qr-4.png");
                //pc.printBitmapZPl(getApplicationContext(),"20190130_102137.jpg");
                pc.printBitmapTest(getApplicationContext(),"Qr-4.png");

/*
                if (TextUtils.isEmpty(AppInfo.btAddress)) {
                    ToastUtil.showToast(MainActivity.this, "Please connect to Bluetooth...");
                    startActivity(new Intent(MainActivity.this, SearchBluetoothActivity.class));
                } else {
                    ToastUtil.showToast(MainActivity.this, "Print picture...");
                    Intent intent2 = new Intent(getApplicationContext(), BtService.class);
                    intent2.setAction(PrintUtil.ACTION_PRINT_BITMAP);
                    startService(intent2);

                }
*/
//                startActivity(new Intent(MainActivity.this,TextActivity.class));
                break;
        }

    }

    /**
     * handle printer message
     *
     * @param event print msg event
     */
    public void onEventMainThread(PrintMsgEvent event) {
        if (event.type == PrinterMsgType.MESSAGE_TOAST) {
            ToastUtil.showToast(MainActivity.this, event.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().register(MainActivity.this);
    }
    AsyncHttpServer httpServer;
    String FilePath;

    public void runRestFul() {

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

        httpServer.get("/file", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String str =  request.getQuery().getString("FILE");
                Log.e("Debug","http://127.0.0.1:5000/file?FILE=" + str );

                File fs = new File("/storage/emulated/0/" + str);
                String mimeType = getMimeType("/storage/emulated/0/" + str);
                Log.e("Debug11", (("File size:" + fs.length())));
                FileInputStream fileStream = null;
                try {
                    fileStream = new FileInputStream(fs);
                    byte[] bs = new byte[(int) fs.length()];
                    int read = fileStream.read(bs, 0, bs.length);
                    response.send(mimeType,bs);
                    Log.e("Debug44", (("Send File completed")));
                } catch (FileNotFoundException e) {
                    Log.e("Debug FileNotFoundException", e.getMessage());
                    response.send("<HTML><HEAD>TEST</HEAD> <BODY><H1>hello man.......<IMG SRC='"+str+"'></IMG></H1><H1>"+ e.getMessage() +"</H1><INPUT ID='TTT' TYPE='BUTTON' VALUE='TTTETETET'> </INPUT></BODY></HTML>");
                }catch (IOException e) {
                    Log.e("Debug IOException", e.getMessage());
                    response.send("<HTML><HEAD>TEST</HEAD> <BODY><H1>hello man.......<IMG SRC='"+str+"'></IMG></H1><H1>"+ e.getMessage() +"</H1><INPUT ID='TTT' TYPE='BUTTON' VALUE='TTTETETET'> </INPUT></BODY></HTML>");
                }catch (Exception e) {
                    Log.e("Debug Exception", e.getMessage());
                    response.send("<HTML><HEAD>TEST</HEAD> <BODY><H1>hello man.......<IMG SRC='"+str+"'></IMG></H1><H1>"+ e.getMessage() +"</H1><INPUT ID='TTT' TYPE='BUTTON' VALUE='TTTETETET'> </INPUT></BODY></HTML>");
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
                        "<A HREF=\"http://127.0.0.1:5000/popup_print?FILE="+ str +"&ICON="+str_icon+"\" >\n" +
                        "<img src=\"http://127.0.0.1:5000/file?FILE="+str_icon+"\" ></img>\n" +
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

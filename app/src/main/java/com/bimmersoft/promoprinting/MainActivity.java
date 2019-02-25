package com.bimmersoft.promoprinting;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView tv_caption;
    Button btnStartStop;
    boolean mBtEnable = true;
    int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    int READ_STORAGE_PERMISSION_REQUEST_CODE = 0x3;
    /**
     * bluetooth adapter
     */
    BluetoothAdapter mAdapter;
    private String FilePath;
    private RestAPI mRestAPI;
    Context ctx;
    Intent mServiceIntent;

    public Context getCtx() {
        return ctx;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        setContentView(R.layout.activity_main);
        mRestAPI = new RestAPI(getCtx());
        mServiceIntent = new Intent(getCtx(), mRestAPI.getClass());
        tv_caption = findViewById(R.id.textView);
        tv_bluename = findViewById(R.id.tv_bluename);
        tv_blueadress = findViewById(R.id.tv_blueadress);
        btnStartStop = findViewById(R.id.btnStartSVC);
        findViewById(R.id.btn_setting).setOnClickListener(this);
        findViewById(R.id.btnShowContents).setOnClickListener(this);
        findViewById(R.id.btnSyncConf).setOnClickListener(this);
        findViewById(R.id.btn_print_img).setOnClickListener(this);
        findViewById(R.id.btnStopSVC).setOnClickListener(this);
        btnStartStop.setOnClickListener(this);
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if(preferences.getBoolean("print_svc_switch", true)){
            strat_svc();
        }
        if(isMyServiceRunning(mRestAPI.getClass())){
            btnStartStop.setText("Stop Service");
            //btnStartStop.setEnabled(false);
        }
        Log.e("SVC:","STATUS : " + isMyServiceRunning(mRestAPI.getClass()));
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
            case R.id.btnShowContents:
                startActivity(new Intent(MainActivity.this, ItemListActivity.class));
                break;
            case R.id.btnSyncConf:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.btnStopSVC:
                //stop_svc();
                startActivity(new Intent(MainActivity.this, RestTest.class));
                break;
            case R.id.btnStartSVC:
                togleSvc();
                //strat_svc();
                break;
            case R.id.btn_print_img:
                print_images_test();
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
    public void stop_svc(){
        if(!isMyServiceRunning(mRestAPI.getClass())) {
            Toast.makeText(getBaseContext(), "Service is not running...", Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("Test","BTN Stop ");
        //Intent stopServiceIntent = new Intent(MainActivity.this, RestAPI.class);
        RestAPI.stop_listen();
        stopService(mServiceIntent);
        if(!isMyServiceRunning(mRestAPI.getClass())){
            RestAPI.httpServer.stop();
            btnStartStop.setText("Start Service");
            //btnStartStop.setEnabled(false);
        }


    }
    public void strat_svc(){
        if(isMyServiceRunning(mRestAPI.getClass())){
            Log.e("Test","BTN Start ");
            //Toast.makeText(getBaseContext(), "Service is already running", Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("Test","BTN Start ");
        ToastUtil.showToast(MainActivity.this, "Start Rest Services.......");

//                Intent intent2 = new Intent(getApplicationContext(), RestService.class);
//                intent2.setAction(RestService.ACTION_START);
//                startService(intent2);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (preferences.getBoolean("enbale_zpl_switch", true)) {
            RestAPI.PRINT_MODE = RestAPI.PRNT_ZPL_MODE;
        }else{
            RestAPI.PRINT_MODE = RestAPI.PRNT_ESC_MODE;
        }

        //Intent startServiceIntent = new Intent(MainActivity.this, RestAPI.class);
        if (startService(mServiceIntent) != null){
            //Toast.makeText(getBaseContext(), "Service is already running", Toast.LENGTH_SHORT).show();
            Log.w("RestAPI","service is already running");
        }else{
            Log.i("RestAPI","Try to start service....");
        }
        //startService(startServiceIntent);
        if(isMyServiceRunning(mRestAPI.getClass())){
            btnStartStop.setText("Stop Service");
            //btnStartStop.setEnabled(false);
        }


    }
    public void print_images_test(){
        PicPrintEx pc = new PicPrintEx();
        //pc.printBitmapTest(getApplicationContext(),"Qr-4.png");
        //pc.printBitmapZPl(getApplicationContext(),"20190130_102137.jpg");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (preferences.getBoolean("enbale_zpl_switch", true)) {
            pc.printBitmapZPl(getApplicationContext(),"Qr-4.png");
        }else{
            pc.printBitmapTest(getApplicationContext(),"Qr-4.png");
        }

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void togleSvc(){
        if(isMyServiceRunning(mRestAPI.getClass())){
            stop_svc();
        }else{
            strat_svc();
        }
    }
    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().register(MainActivity.this);
//    }
}

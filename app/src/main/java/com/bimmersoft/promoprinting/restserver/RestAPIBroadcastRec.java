package com.bimmersoft.promoprinting.restserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestAPIBroadcastRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
            Log.i(RestAPIBroadcastRec.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
            context.startService(new Intent(context, RestAPI.class));;
            //context.startForegroundService(new Intent(context, RestAPI.class));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, RestAPI.class));
//        } else {
//            context.startService(new Intent(context, RestAPI.class));
//        }
    }

}



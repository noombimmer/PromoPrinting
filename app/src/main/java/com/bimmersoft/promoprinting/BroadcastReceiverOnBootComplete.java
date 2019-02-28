package com.bimmersoft.promoprinting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bimmersoft.promoprinting.restserver.RestAPI;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, RestAPI.class);
            context.startService(serviceIntent);
        }

    }
}

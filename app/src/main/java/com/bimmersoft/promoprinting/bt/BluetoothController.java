package com.bimmersoft.promoprinting.bt;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.bimmersoft.promoprinting.MainActivity;
import com.bimmersoft.promoprinting.print.PrintUtil;

/**
 * Created by liuguirong on 8/1/17.
 */

public class BluetoothController {

    public static void init(MainActivity activity) {
        if (null == activity.mAdapter) {
            activity.mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (null == activity.mAdapter) {
            activity.tv_bluename.setText("The device does not have a Bluetooth module");
            activity.mBtEnable = false;
            activity.tv_caption.setText("Printer not connect");
            activity.tv_caption.setTextColor(Color.RED);
            return;
        }
        Log.d("activity.mAdapter.getState()","activity.mAdapter.getState()"+activity.mAdapter.getState());
        if (!activity.mAdapter.isEnabled()) {
            //没有在开启中也没有打开
//            if ( activity.mAdapter.getState()!=BluetoothAdapter.STATE_TURNING_ON  && activity.mAdapter.getState()!=BluetoothAdapter.STATE_ON ){
            if ( activity.mAdapter.getState()==BluetoothAdapter.STATE_OFF ){//蓝牙被关闭时强制打开
                 activity.mAdapter.enable();

            }else {
                activity.tv_bluename.setText("Bluetooth is not turned on");
                activity.tv_caption.setText("Printer not connect");
                activity.tv_caption.setTextColor(Color.RED);

                return;
            }
        }
        String address = PrintUtil.getDefaultBluethoothDeviceAddress(activity.getApplicationContext());
        if (TextUtils.isEmpty(address)) {
            activity.tv_bluename.setText("No Priner connect...");
            activity.tv_caption.setText("Printer not connect");
            activity.tv_caption.setTextColor(Color.RED);

            return;
        }
        String name = PrintUtil.getDefaultBluetoothDeviceName(activity.getApplicationContext());
        activity.tv_bluename.setText("Printer ：" + name);
        activity.tv_blueadress.setText("MACADDR: " + address);
        activity.tv_caption.setText("Printer connected");
        activity.tv_caption.setTextColor(Color.GREEN);

    }
    public static boolean turnOnBluetooth()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.enable();
        }
        return false;
    }
}

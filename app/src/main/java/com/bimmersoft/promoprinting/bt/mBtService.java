package com.bimmersoft.promoprinting.bt;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.bimmersoft.promoprinting.print.GPrinterCommand;
import com.bimmersoft.promoprinting.print.PrintPic;
import com.bimmersoft.promoprinting.print.PrintQueue;
import com.bimmersoft.promoprinting.print.PrintUtil;
import com.bimmersoft.promoprinting.printutil.PrintOrderDataMaker;
import com.bimmersoft.promoprinting.printutil.PrinterWriter;
import com.bimmersoft.promoprinting.printutil.PrinterWriter58mm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by liuguirong on 8/1/17.
 * <p/>
 * print ticket service
 */
public class mBtService extends IntentService {

    public mBtService() {
        super("BtService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public mBtService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TEST)) {
            printTest();
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TEST_TWO)) {
            printTesttwo(3);
        }else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_BITMAP)) {
            printBitmapTest();
        }

    }

    private void printTest() {
            PrintOrderDataMaker printOrderDataMaker = new PrintOrderDataMaker(this,"", PrinterWriter58mm.TYPE_58, PrinterWriter.HEIGHT_PARTING_DEFAULT);
            ArrayList<byte[]> printData = (ArrayList<byte[]>) printOrderDataMaker.getPrintData(PrinterWriter58mm.TYPE_58);
            PrintQueue.getQueue(getApplicationContext()).add(printData);

    }

    /**
     * 打印几遍
     * @param num
     */
  private void printTesttwo(int num) {
        try {
            ArrayList<byte[]> bytes = new ArrayList<byte[]>();
            for (int i = 0; i < num; i++) {
                String message = "Bluetooth print test\nBluetooth print test\nBluetooth print test\n\n";
                bytes.add(GPrinterCommand.reset);
                bytes.add(message.getBytes("gbk"));
                bytes.add(GPrinterCommand
                        .print);
                bytes.add(GPrinterCommand.print);
                bytes.add(GPrinterCommand.print);
            }
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void print(byte[] byteArrayExtra) {
        if (null == byteArrayExtra || byteArrayExtra.length <= 0) {
            return;
        }
        PrintQueue.getQueue(getApplicationContext()).add(byteArrayExtra);
    }

    private void printBitmapTest() {
        BufferedInputStream bis;
        try {
/*
            bis = new BufferedInputStream(getAssets().open(
                    "Qr-4.png"));
*/
            //File file = new File(Environment.getExternalStorageDirectory().getPath(),"/Qr-4.png");
            FileInputStream fs = new FileInputStream("/storage/emulated/0/Qr-4.png");
            bis = new BufferedInputStream(fs);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demo);
        PrintPic printPic = PrintPic.getInstance();
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(GPrinterCommand.reset);
        printBytes.add(GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(GPrinterCommand.print);
        PrintQueue.getQueue(getApplicationContext()).add(bytes);
    }
//
//    private void printPainting() {
//        byte[] bytes = PrintPic.getInstance().printDraw();
//        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
//        printBytes.add(GPrinterCommand.reset);
//        printBytes.add(GPrinterCommand.print);
//        printBytes.add(bytes);
//        Log.e("BtService", "image bytes size is :" + bytes.length);
//        printBytes.add(GPrinterCommand.print);
//        PrintQueue.getQueue(getApplicationContext()).add(bytes);
//    }
}
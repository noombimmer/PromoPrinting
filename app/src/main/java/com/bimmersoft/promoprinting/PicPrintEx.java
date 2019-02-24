package com.bimmersoft.promoprinting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bimmersoft.promoprinting.print.GPrinterCommand;
import com.bimmersoft.promoprinting.print.PrintPic;
import com.bimmersoft.promoprinting.print.PrintQueue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PicPrintEx {

    public void printBitmapTest(Context ctx, String filename) {
        BufferedInputStream bis;
        try {
/*
            bis = new BufferedInputStream(getAssets().open(
                    "Qr-4.png"));
*/

            //File file = new File(Environment.getExternalStorageDirectory().getPath(),"/Qr-4.png");
            FileInputStream fs = new FileInputStream("/storage/emulated/0/" + filename);
            bis = new BufferedInputStream(fs);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        float ratio2 = ((float) bitmap.getWidth()) / ((float)ZPL_width);
        Bitmap scaleLogo = PicScale.createScaledBitmap(bitmap,ZPL_width,(int)(bitmap.getHeight() / ratio2) + 20,PicScale.ScalingLogic.FIT);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demo);
//
//        PrintPic printPic = PrintPic.getInstance();
//        printPic.init(bitmap);
//        if (null != bitmap) {
//            if (bitmap.isRecycled()) {
//                bitmap = null;
//            } else {
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }


        PrintPic printPic = PrintPic.getInstance();

        printPic.init(scaleLogo);
        if (null != scaleLogo) {
            if (scaleLogo.isRecycled()) {
                scaleLogo = null;
            } else {
                scaleLogo.recycle();
                scaleLogo = null;
            }
        }

        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(GPrinterCommand.reset);
        printBytes.add(GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "ESC COMMAND :" + bytes.toString());
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(GPrinterCommand.print);
        PrintQueue.getQueue(ctx).add(bytes);
    }
    public static int ZPL_width = 385;

    public void printBitmapZPl(Context ctx, String filename) {
        BufferedInputStream bis;

        try {
            PrinterCommandTranslator translator = new PrinterCommandTranslator();
            ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
            FileInputStream fs = new FileInputStream("/storage/emulated/0/" + filename);
            bis = new BufferedInputStream(fs);
            Log.e("DEBUGER", "File :" + "/storage/emulated/0/" + filename);

            //compressHex = true;

            //FileInputStream fs = new FileInputStream("/storage/emulated/0/" + "20190130_102137.jpg");
            //bis = new BufferedInputStream(fs);

            Bitmap myLogo = BitmapFactory.decodeStream(bis);
            int w =  myLogo.getWidth();
            int h = myLogo.getHeight();

            int ratio = h/w;
            float ratio2 = ((float) w) / ((float)ZPL_width);

            Log.e("DEBUGER", "Org-W :" + String.valueOf(w));
            Log.e("DEBUGER", "Org-H :" + String.valueOf(h));
            Log.e("DEBUGER", "Org-Ratio :" + String.valueOf(ratio));
            Log.e("DEBUGER", "Org-FRatio :" + String.format("%.2f",ratio2));
            Log.e("DEBUGER", "Tgt-w :" + String.valueOf(ZPL_width));
            Log.e("DEBUGER", "Tgt-h :" + String.format("%.0f",h / ratio2));
            Bitmap scaleLogo = PicScale.createScaledBitmap(myLogo,ZPL_width,(int)(h / ratio2),PicScale.ScalingLogic.FIT);


            Log.e("DEBUGER", convertFromImageZPL(scaleLogo, true));

            String CommandH = "\r\n! U1 setvar \"zpl.label_length\" \""+String.valueOf(tgt_height)+"\"\r\n";
            Log.e("DEBUGER", CommandH);


            printBytes.add("\r\n! U1 setvar \"device.languages\" \"zpl\"\r\n".getBytes());
            printBytes.add(CommandH.getBytes());
            byte[] bytes = convertFromImageZPL(scaleLogo, true).getBytes();
            printBytes.add(bytes);
/*
            //printBytes.add(convertFromImageZPL(scaleLogo, true).getBytes());
            byte[] bytes = convertFromImageZPL(scaleLogo, true).getBytes();
            printBytes.add(bytes);
            //print(lf.getBytes());
            printBytes.add("\r\n! U1 setvar \"device.languages\" \"line_print\"\r\n".getBytes());
            printBytes.add(translator.toNormalRepeatTillEnd('-'));

            //printBytes.add(bytes);

            PrintQueue.getQueue(ctx).add(bytes);
 */
            printBytes.add("\r\n! U1 setvar \"device.languages\" \"line_print\"\r\n".getBytes());
            printBytes.add(translator.toNormalRepeatTillEnd('-'));
            PrintQueue.getQueue(ctx).add(printBytes);
        }catch(Exception e){
            Log.e("Error",e.getMessage());
        }


    }
    private int tgt_width = 0;
    private int tgt_height = 0;
    public String convertFromImageZPL(Bitmap image, Boolean addHeaderFooter) {
        String hexAscii = createBody(image);
        //if (compressHexZPL) {
        hexAscii = encodeHexAsciiZPL(hexAscii);
        //}

        String zplCode = "^GFA," + totalZPL + "," + totalZPL + "," + widthBytesZPL + ", " + hexAscii;

        if (addHeaderFooter) {
            String header = "^XA " + "^FO0,0^GFA," + totalZPL + "," + totalZPL + "," + widthBytesZPL + ", ";
            String footer = "^FS" + "^XZ";
            zplCode = header + zplCode + footer;
        }
        return zplCode;
    }

    public int heightZPL;

    private String createBody(Bitmap bitmapImage) {
        StringBuilder sb = new StringBuilder();
        int height = bitmapImage.getHeight();
        int width = bitmapImage.getWidth();
        heightZPL = height;
        int rgb, red, green, blue, index = 0;
        char auxBinaryChar[] = {'0', '0', '0', '0', '0', '0', '0', '0'};
        tgt_width = width;
        tgt_height = height;

        Log.e("INFO H: ", String.valueOf(height));
        Log.e("INFO W:", String.valueOf(width));

        widthBytesZPL = width / 8;
        if (width % 8 > 0) {
            widthBytesZPL = (width / 8 + 1);
        } else {
            widthBytesZPL = width / 8;
        }
        this.totalZPL = widthBytesZPL * height;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                rgb = bitmapImage.getPixel(w, h);
                red = (rgb >> 16) & 0x000000FF;
                green = (rgb >> 8) & 0x000000FF;
                blue = (rgb) & 0x000000FF;
                char auxChar = '1';
                int totalColor = red + green + blue;
                if (totalColor > blackLimitZPL) {
                    auxChar = '0';
                }
                auxBinaryChar[index] = auxChar;
                index++;
                if (index == 8 || w == (width - 1)) {
                    sb.append(fourByteBinaryZPL(new String(auxBinaryChar)));
                    auxBinaryChar = new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};
                    index = 0;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    private boolean compressHexZPL = true;
    private int totalZPL;
    private static Map<Integer, String> mapCodeZPL = new HashMap<Integer, String>();
    private int widthBytesZPL;
    private int blackLimitZPL = 380;

    private String fourByteBinaryZPL(String binaryStr) {
        int decimal = Integer.parseInt(binaryStr, 2);
        if (decimal > 15) {
            return Integer.toString(decimal, 16).toUpperCase();
        } else {
            return "0" + Integer.toString(decimal, 16).toUpperCase();
        }
    }

    private String encodeHexAsciiZPL(String code) {
        int maxlinea = widthBytesZPL * 2;
        StringBuilder sbCode = new StringBuilder();
        StringBuilder sbLinea = new StringBuilder();
        String previousLine = null;
        int counter = 1;
        char aux = code.charAt(0);
        boolean firstChar = false;
        for (int i = 1; i < code.length(); i++) {
            if (firstChar) {
                aux = code.charAt(i);
                firstChar = false;
                continue;
            }
            if (code.charAt(i) == '\n') {
                if (counter >= maxlinea && aux == '0') {
                    sbLinea.append(",");
                } else if (counter >= maxlinea && aux == 'F') {
                    sbLinea.append("!");
                } else if (counter > 20) {
                    int multi20 = (counter / 20) * 20;
                    int resto20 = (counter % 20);
                    sbLinea.append(mapCodeZPL.get(multi20));
                    if (resto20 != 0) {
                        sbLinea.append(mapCodeZPL.get(resto20)).append(aux);
                    } else {
                        sbLinea.append(aux);
                    }
                } else {
                    sbLinea.append(mapCodeZPL.get(counter)).append(aux);
                }
                counter = 1;
                firstChar = true;
                if (sbLinea.toString().equals(previousLine)) {
                    sbCode.append(":");
                } else {
                    sbCode.append(sbLinea.toString());
                }
                previousLine = sbLinea.toString();
                sbLinea.setLength(0);
                continue;
            }
            if (aux == code.charAt(i)) {
                counter++;
            } else {
                if (counter > 20) {
                    int multi20 = (counter / 20) * 20;
                    int resto20 = (counter % 20);
                    sbLinea.append(mapCodeZPL.get(multi20));
                    if (resto20 != 0) {
                        sbLinea.append(mapCodeZPL.get(resto20)).append(aux);
                    } else {
                        sbLinea.append(aux);
                    }
                } else {
                    sbLinea.append(mapCodeZPL.get(counter)).append(aux);
                }
                counter = 1;
                aux = code.charAt(i);
            }
        }
        return sbCode.toString();
    }
    {
        mapCodeZPL.put(1, "G");
        mapCodeZPL.put(2, "H");
        mapCodeZPL.put(3, "I");
        mapCodeZPL.put(4, "J");
        mapCodeZPL.put(5, "K");
        mapCodeZPL.put(6, "L");
        mapCodeZPL.put(7, "M");
        mapCodeZPL.put(8, "N");
        mapCodeZPL.put(9, "O");
        mapCodeZPL.put(10, "P");
        mapCodeZPL.put(11, "Q");
        mapCodeZPL.put(12, "R");
        mapCodeZPL.put(13, "S");
        mapCodeZPL.put(14, "T");
        mapCodeZPL.put(15, "U");
        mapCodeZPL.put(16, "V");
        mapCodeZPL.put(17, "W");
        mapCodeZPL.put(18, "X");
        mapCodeZPL.put(19, "Y");
        mapCodeZPL.put(20, "g");
        mapCodeZPL.put(40, "h");
        mapCodeZPL.put(60, "i");
        mapCodeZPL.put(80, "j");
        mapCodeZPL.put(100, "k");
        mapCodeZPL.put(120, "l");
        mapCodeZPL.put(140, "m");
        mapCodeZPL.put(160, "n");
        mapCodeZPL.put(180, "o");
        mapCodeZPL.put(200, "p");
        mapCodeZPL.put(220, "q");
        mapCodeZPL.put(240, "r");
        mapCodeZPL.put(260, "s");
        mapCodeZPL.put(280, "t");
        mapCodeZPL.put(300, "u");
        mapCodeZPL.put(320, "v");
        mapCodeZPL.put(340, "w");
        mapCodeZPL.put(360, "x");
        mapCodeZPL.put(380, "y");
        mapCodeZPL.put(400, "z");
    }
}

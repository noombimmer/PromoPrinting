package com.bimmersoft.promoprinting.printutil;

import android.content.Context;

import com.bimmersoft.promoprinting.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * 测试数据生成器
 * Created by liuguirong on 8/1/17.
 */

public class PrintOrderDataMaker implements PrintDataMaker {


    private String qr;
    private int width;
    private int height;
    Context btService;
    private String remark = "Test Slip Printing ";


    public PrintOrderDataMaker( Context btService, String qr, int width, int height) {
        this.qr = qr;
        this.width = width;
        this.height = height;
        this.btService = btService;
    }



    @Override
    public List<byte[]> getPrintData(int type) {
        ArrayList<byte[]> data = new ArrayList<>();

        try {
            PrinterWriter printer;
            printer = type == PrinterWriter58mm.TYPE_58 ? new PrinterWriter58mm(height, width) : new PrinterWriter80mm(height, width);
            printer.setAlignCenter();
            data.add(printer.getDataAndReset());

            ArrayList<byte[]> image1 = printer.getImageByte(btService.getResources(), R.drawable.demo);

            data.addAll(image1);

            printer.setAlignLeft();
            printer.printLine();
            printer.printLineFeed();

            printer.printLineFeed();
            printer.setAlignCenter();
            printer.setEmphasizedOn();
            printer.setFontSize(1);
            printer.print("If you eat well, you should eat more.");
            printer.printLineFeed();
            printer.setEmphasizedOff();
            printer.printLineFeed();

            printer.printLineFeed();
            printer.setFontSize(0);
            printer.setAlignCenter();
            printer.print("Order number：" + "546545645465456454");
            printer.printLineFeed();

            printer.setAlignCenter();
            printer.print(new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    .format(new Date(System.currentTimeMillis())));
            printer.printLineFeed();
            printer.printLine();

            printer.printLineFeed();
            printer.setAlignLeft();
            printer.print("Order Status: " + "Order received");
            printer.printLineFeed();
            printer.print("User's Nickname: " +"Mr. weekend");
            printer.printLineFeed();
            printer.print("Number of people dining: " + "10");
            printer.printLineFeed();
            printer.print("Use table number:" + "A3" + "Table");
            printer.printLineFeed();
            printer.print("scheduled time：" + "2017-10-1 17：00");
            printer.printLineFeed();
            printer.print("Reserved time：30 Minutes");
            printer.printLineFeed();
            printer.print("Contact information：" + "18094111545454");
            printer.printLineFeed();
            printer.printLine();
            printer.printLineFeed();

            printer.setAlignLeft();
            printer.print("Remarks：" + "Remember to stay in position");
            printer.printLineFeed();
            printer.printLine();

            printer.printLineFeed();

                printer.setAlignCenter();
                printer.print("Dishes information");
                printer.printLineFeed();
                printer.setAlignCenter();
                printer.printInOneLine("Dish name", "Quantity", "unit price", 0);
                printer.printLineFeed();
                for (int i = 0; i < 3; i++) {

                    printer.printInOneLine("Dry pot cabbage", "X" + 3, "$" + 30, 0);
                    printer.printLineFeed();
                }
                printer.printLineFeed();
                printer.printLine();
                printer.printLineFeed();
                printer.setAlignLeft();
                printer.printInOneLine("Total dish：", "$" + 100, 0);


            printer.setAlignLeft();
            printer.printInOneLine("Discounted price：", "$" +"0.00"
                    , 0);
            printer.printLineFeed();

            printer.setAlignLeft();
            printer.printInOneLine("Deposit/refund：", "$" + "0.00"
                          , 0);
            printer.printLineFeed();


            printer.setAlignLeft();
            printer.printInOneLine("Total Amount：", "$" +90, 0);
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();
            printer.setAlignCenter();
            printer.print("Thank you for your patronage, welcome to visit again!");
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();
            printer.feedPaperCutPartial();

            data.add(printer.getDataAndClose());
            return data;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


}

package com.example.androidproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RateThread2 implements Runnable{
    private static final String TAG="MyThread";
    private Handler handler;
    Bundle bundle;
    List<String> retlist=new ArrayList<String>();

    public RateThread2(Handler handler){
        this.handler=handler;
    }

//    public void setHandler(Handler handler) {
//        this.handler = handler;
//    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Elements tables = doc.getElementsByTag("table");
            Element firstTable = tables.get(1);
            Elements trs = firstTable.getElementsByTag("tr");
            trs.remove(0);
            //根据对网页观察，汇率在第4列
            for(Element tr:trs){
                String currency=tr.getElementsByTag("td").get(0).text();
                String rate=tr.getElementsByTag("td").get(5).text();
                retlist.add(currency+" "+rate);
            }

            Message msg = handler.obtainMessage(5);
            //放回消息中
            msg.obj = retlist;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "run: 消息已发送");
    }
}

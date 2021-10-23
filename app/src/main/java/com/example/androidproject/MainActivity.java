package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;//不要选错啦！
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Runnable {//加了一个implements

    private static final String TAG = "MainActivity";//写日志的时候要用到
    private float dollarRate = 0.21f;//定义三个汇率，美元，欧元与won
    private float euroRate = 0.28f;
    private float wonRate = 501;
    TextView show;
    Handler handler;//将定义放在外面，因为要用到
    private final static long ONE_DAY_MSECOND = 24 * 60 * 60 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent();
        //创建闹钟对象
        AlarmManager aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    // 设置闹钟为可将手机从休眠中唤醒，第一次开始时间为现在，间隔为一天，完成的任务为pendingIntent
        aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ONE_DAY_MSECOND, pi);

        //在这里使用intent的话会出现问题，因此转移到下面onActivityResult函数中。
        //Intent intent = getIntent();
        //dollarRate = intent.getFloatExtra("NewDollarRate", 0.21f);
        //euroRate = intent.getFloatExtra("NewEuroRate", 0.28f);
        //wonRate = intent.getFloatExtra("NewWonRate", 501.00f);
        //show = findViewById(R.id.show);
        //开启线程,与主程序交互
        handler = new Handler(Looper.myLooper()) {//获得当前线程的对象
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i(TAG, "handleMessage: 收到消息");
                if (msg.what == 6) {
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: ");
                    //收到msg后，在控件中显示
                    show = findViewById(R.id.show);
                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        };
        Thread t = new Thread(this);
        t.start();//启动
    }

    public void click(View btn) {
        show = findViewById(R.id.show);
        Log.i(TAG, "click:");
        EditText inputText = findViewById(R.id.input);
        String inp = inputText.getText().toString();
        if (inp.length() > 0) {
            Log.i(TAG, "click:inp=" + inp);
            float num = Float.parseFloat(inp);
            float r;
            if (btn.getId() == R.id.btn_dol) {
                r = num * dollarRate;
            } else if (btn.getId() == R.id.btn_euro) {
                r = num * euroRate;
            } else {
                r = num * wonRate;
            }
            Log.i(TAG, "r=" + r);
            show.setText(String.valueOf(r));
        } else {
            show.setText("你好");
            Toast.makeText(this, "请输入金额后再计算", Toast.LENGTH_SHORT).show();
            //如果没有输入，就会有弹窗说已经错了
        }
    }

    public void func(View v) {
        Log.i(TAG, "myfunc:AAA");
        //跳转页面并保存rate
        Intent intent = new Intent(this, SAVEActivity.class);
        intent.putExtra("dollar_rate_key", dollarRate);
        intent.putExtra("euro_rate_key", euroRate);
        intent.putExtra("won_rate_key", wonRate);
        Log.i(TAG, "dollarrate:" + dollarRate);
        Log.i(TAG, "eurorate:" + euroRate);
        Log.i(TAG, "wonrate:" + wonRate);
        //startActivity(intent);去新的窗口
        startActivityForResult(intent, 100);//回到原来窗口这里有一个code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == 3) {//分别在MainActivity.java第73行左右定义，saveactivity第60行定义
            dollarRate = data.getFloatExtra("NewDollarRate", 0.1f);
            euroRate = data.getFloatExtra("NewEuroRate", 0.1f);
            wonRate = data.getFloatExtra("NewWonRate", 0.1f);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//创建菜单文件
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;//真或者假按照这个来看是否显示
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//控制菜单事件
        if (item.getItemId() == R.id.menu_setting) {
            Log.i(TAG, "ON operaion");
            Intent intent = new Intent(this, SAVEActivity.class);
            intent.putExtra("dollar_rate_key", dollarRate);
            intent.putExtra("euro_rate_key", euroRate);
            intent.putExtra("won_rate_key", wonRate);
            Log.i(TAG, "dollarrate:" + dollarRate);
            Log.i(TAG, "eurorate:" + euroRate);
            Log.i(TAG, "wonrate:" + wonRate);
            //startActivity(intent);去新的窗口
            startActivityForResult(intent, 100);//回到原来窗口这里有一个code
        }
        //增加菜单栏操作，加载之前保存的汇率，menu_loading是按钮的名称
        if (item.getItemId() == R.id.menu_loading) {
            SharedPreferences sp =
                    getSharedPreferences("myrate", Activity.MODE_PRIVATE);//activity表示是程序私有
            dollarRate = sp.getFloat("dollar_rate", 0.0f);
            euroRate = sp.getFloat("euro_rate", 0.0f);
            wonRate = sp.getFloat("won_rate", 0.0f);
            Log.i(TAG, "onOptionsItemSelected: dollarRate=" + dollarRate);//输出日志，看看有没有得到
            Log.i(TAG, "onOptionsItemSelected: euroRate=" + euroRate);
            Log.i(TAG, "onOptionsItemSelected: wonRate=" + wonRate);
            Toast.makeText(this, "加载汇率成功", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    //开启线程的run方法
    @Override
    public void run() {
        try {
            Thread.sleep(3000);//等待三秒，再发过去
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "run: ...");
        //获得网站内容
//        URL url = null;
//        try {
//            url = new URL("https://www.usd-cny.com/bankofchina.htm");
//            //连接不上的时候，在manifest文件的application中添加android:usesCleartextTraffic="true"
//            //在manifest文件中添加<uses-permission android:name="android.permission.INTERNET"></uses-permission>
//
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//            String html = inputStream2String(in);
//            Log.i(TAG, "run: html=" + html);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //获取标题，从一个汇率网站，并做到每天更新的关键代码
        try {
            Document doc = null;
            doc = Jsoup.connect("https://usd-cny.com/").get();
            Log.i(TAG, "run: title" + doc.title());
            Elements tables = doc.getElementsByTag("table");
            Element firstTable = tables.first();//从集合中直接获得，避免标签不同产生问题
            Elements trs = firstTable.getElementsByTag("tr");
            trs.remove(0);//或者直接去除第一行
            for (Element tr : trs) {
                //从行中获取td元素
                Elements tds = tr.getElementsByTag("td");
                Element td1 = tds.get(0);
                Element td2 = tds.get(4);
                // Log.i(TAG, "run: "+td1.text()+"——>"+td2.text());
                //运行出币种+数据
                //如何提取？加过滤
                if ("美元".equals(td1.text())) {
                    Log.i(TAG, "run: " + td1.text() + "——>" + td2.text());
                    String val = td2.text();
                    dollarRate=100f/Float.parseFloat(val);
                    Log.i(TAG, "run: 美元汇率"+dollarRate);
                }
                if ("欧元".equals(td1.text())) {
                    Log.i(TAG, "run: " + td1.text() + "——>" + td2.text());
                    String val = td2.text();
                    euroRate=100f/Float.parseFloat(val);
                    Log.i(TAG, "run: 欧元汇率"+euroRate);
                }
                if ("韩币".equals(td1.text())) {
                    Log.i(TAG, "run: " + td1.text() + "——>" + td2.text());
                    String val = td2.text();
                    wonRate=100f/Float.parseFloat(val);
                    Log.i(TAG, "run: 韩元汇率"+wonRate);
                }
                //Log.i(TAG, "run: tds size" + tds.size());//每一行五个.除了第一行。因此可以通过这个去除第一行
            }


//            for(Element item:firstTable.getElementsByClass("bz")){//通过class获得元素
//                Log.i(TAG, "run: item"+item.text());
//            }

            //Log.i(TAG, "run: firstTable"+firstTable);// 找table标签
            //方法一
//            Elements tds = firstTable.getElementsByTag("td");//提取了td标签
//            for (int i = 0; i < tds.size(); i += 5) {//为什么是i+5？因为一行有五列，每次都要他去下一列
//                Element td1 = tds.get(i);
//                Element td2 = tds.get(i + 1);
//                Log.i(TAG, "run: td1=" + td1.text() + "\t td2=" + td2.text());//输出td的text
//                //td1=港币	 td2=82.87
//            }
//            Elements ths = firstTable.getElementsByTag("th");
//            for(Element th:ths){
//                Log.i(TAG, "run: th="+th);
//                //th.html与th.text的区别：html包含了标签，text只有文本
//            }
//            //输出th2
//            Element th2 = ths.get(1);
//            Log.i(TAG, "run: th2="+th2);
            //获取到价格,需要找tr
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage();
        msg.what = 6;
        msg.obj = "Hello from run";
        handler.sendMessage(msg);
        Log.i(TAG, "run: 消息已经发送");
    }

    //获得网站内容
    private String inputStream2String(InputStream inputStream)
            throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        //会有乱码问题，需要修改代码
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
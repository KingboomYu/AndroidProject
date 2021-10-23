package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SAVEActivity extends AppCompatActivity {
    private static final String TAG = "SAVEActivity";//设置独有的TAG，用于查看日志
    TextView dollar2, euro2, won2;
    //全局变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        //使用intent获取到rate
        Intent intent = getIntent();
        float dollarrate = intent.getFloatExtra("dollar_rate_key", 0.00f);
        float eurorate = intent.getFloatExtra("euro_rate_key", 0.00f);
        float wonrate = intent.getFloatExtra("won_rate_key", 0.00f);
        //        String updateDate = sharedPreferences.getString("update_date","");
//        Date today = Calendar.getInstance().getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        final String todayStr=sdf.format(today);

        //在控件中显示rate
        dollar2 = findViewById(R.id.text_dol);
        dollar2.setText(String.valueOf(dollarrate));
        euro2 = findViewById(R.id.text_euro);
        euro2.setText(String.valueOf(eurorate));
        won2 = findViewById(R.id.text_won);
        won2.setText(String.valueOf(wonrate));
    }

    public void func(View v) {
        Log.i(TAG, "myfunc:BBB");
        Intent intent = getIntent();//新方法，回到原来的窗口
        //Intent intent = new Intent(this,MainActivity.class);原方法。去一个新的窗口
        EditText NewDollarRate = findViewById(R.id.text_dol);//从控件中得到rate
        //注意命名方式，首字母小写
        float dollarRate = Float.parseFloat(NewDollarRate.getText().toString());
        EditText NewEuroRate = findViewById(R.id.text_euro);
        float euroRate = Float.parseFloat(NewEuroRate.getText().toString());
        EditText NewWonRate = findViewById(R.id.text_won);
        float wonRate = Float.parseFloat(NewWonRate.getText().toString());
        intent.putExtra("NewDollarRate", dollarRate);//存入到intent中。
        intent.putExtra("NewEuroRate", euroRate);
        intent.putExtra("NewWonRate", wonRate);

        //增加存储汇率文件的操作
        SharedPreferences sp =
                getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//创建一个编辑器
        editor.putFloat("dollar_rate", dollarRate);//存储三个更新后的汇率文件
        editor.putFloat("euro_rate", euroRate);
        editor.putFloat("won_rate", wonRate);
        editor.apply();//记得加apply完成提交！

        Log.i(TAG, "func: dollarRate=" + dollarRate);//输出日志

        //startActivity(intent);//去了一个新的窗口
        setResult(3, intent);//新方法，能够回到原来的窗口
        finish();//新方法。回到原来的窗口
    }
}
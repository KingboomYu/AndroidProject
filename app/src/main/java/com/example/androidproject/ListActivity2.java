package com.example.androidproject;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity2 extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Handler handler;
    ListView mylist2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        ArrayList listItems = new ArrayList<HashMap<String, String>>();
        mylist2 = findViewById(R.id.mylist2);
        mylist2.setOnItemClickListener(this);
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                    ArrayList<String> rlist = (ArrayList<String>) msg.obj;
                    for (String s : rlist) {
                        String[] strings = s.split(" ");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("ItemTitle", strings[0]); // 标题文字
                        map.put("ItemDetail", strings[1]); // 详情描述
                        listItems.add(map);
                    }
                    SimpleAdapter listItemAdapter = new SimpleAdapter(ListActivity2.this, listItems, // listItems 数据源
                            R.layout.list_item,
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail});

                    mylist2.setAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        RateThread2 dt = new RateThread2(handler);
        Thread t = new Thread(dt);
        t.start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ListActivity3.class);
        Object itemAtPosition = mylist2.getItemAtPosition(i);
        HashMap<String, String> map = (HashMap<String, String>) itemAtPosition;
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        intent.putExtra("currency",titleStr);
        intent.putExtra("rate",detailStr);
        startActivityForResult(intent,1);

    }
}
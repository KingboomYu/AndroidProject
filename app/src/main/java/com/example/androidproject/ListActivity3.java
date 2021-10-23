package com.example.androidproject;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity3 extends AppCompatActivity {
    String currency = "";
    double rate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list3);
        Intent intent = getIntent();
        currency = intent.getStringExtra("currency");
        rate = Double.parseDouble(intent.getStringExtra("rate"));
        TextView Currency = findViewById(R.id.Currency);
        Currency.setText(currency);
    }

    public void click(View view) {
        TextView rmb = findViewById(R.id.InputRMB);
        double money = Double.parseDouble(rmb.getText().toString());
        TextView output = findViewById(R.id.Output);
        output.setText(String.valueOf(money * 100 / rate));


    }
}
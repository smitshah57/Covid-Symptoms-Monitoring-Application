package com.example.symptoms_monitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    private int time_period = 5000;
    private dbHandler dbHandler;
    private TextView hello_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        hello_text = findViewById(R.id.hello_text);
        dbHandler = new dbHandler(this,null,null,1);

        Cursor data = dbHandler.getData();
        while(data.moveToNext()){
            hello_text.setText("Hey "+data.getString(0)+" "+data.getString(1));
            break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(i);
            }

        },time_period);
    }
}
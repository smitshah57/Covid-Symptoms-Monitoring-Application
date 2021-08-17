package com.example.symptoms_monitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;


public class MainActivity extends AppCompatActivity {

    private EditText first_name;
    private EditText last_name;
    private Button submit_button;
    private dbHandler dbHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        submit_button = findViewById(R.id.submit_button);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        dbHandler = new dbHandler(this, null, null, 1);


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        SharedPreferences preferences = getSharedPreferences("First_time_app_open", MODE_PRIVATE);
        String first_time = preferences.getString("First Time Open", "Yes");

        if (first_time.equals("Yes")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("First Time Open", "No");
            editor.apply();

            submit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.isEmpty(first_name.getText().toString()) || TextUtils.isEmpty(last_name.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    } else {


                        Intent i = new Intent(MainActivity.this, SecondActivity.class);
                        String first_name_user = first_name.getText().toString();
                        String last_name_user = last_name.getText().toString();
                        add_personal_details(first_name_user, last_name_user);
                        startActivity(i);
                    }

                }
            });
        } else {
            Intent i = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(i);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void add_personal_details(String first_name_user, String last_name_user) {
        dbHandler.add_user_detail(first_name_user, last_name_user);

    }
}
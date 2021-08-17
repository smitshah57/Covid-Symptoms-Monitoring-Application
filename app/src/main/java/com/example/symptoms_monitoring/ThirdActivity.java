package com.example.symptoms_monitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ThirdActivity extends AppCompatActivity implements SensorEventListener  {

    private SensorManager sensor_manager;
    private Button res_rat_button;
    private Button heart_rate_button;
    private Button symptoms;
    private Button upload_data;
    Sensor accelerometer;
    private TextView measure_heart_rate;
    private  TextView measure_respiratory_rate;
    private dbHandler dbHandler;
    String heart_rate= "0";
    String respiratory_rate="0";
    String first_name;
    ArrayList<Double> values_z_dim = new ArrayList<Double>();
    int res_rate;
    int progress = 0;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        dbHandler = new dbHandler(this,null,null,1);
        heart_rate_button = findViewById(R.id.heart_rate_button);
        res_rat_button = findViewById(R.id.res_rate_button);
        measure_respiratory_rate = findViewById(R.id.measure_respiratory_rate);
        measure_heart_rate = findViewById(R.id.measure_heart_rate);
        upload_data = findViewById(R.id.upload_data);
        symptoms = findViewById(R.id.symptoms);
        progressBar = findViewById(R.id.progressBar);

        Cursor data = dbHandler.getData();
        while(data.moveToNext()){
            first_name = data.getString(0);
            break;
        }


        upload_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.onCreateTable(first_name);
                dbHandler.onInsertRates(first_name,heart_rate,respiratory_rate);
                Toast.makeText(ThirdActivity.this, "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();

            }
        });

        res_rat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensor_manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
                accelerometer = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensor_manager.registerListener(ThirdActivity.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                values_z_dim.clear();
                Toast.makeText(ThirdActivity.this, "Respiratory Rate Recording Started", Toast.LENGTH_SHORT).show();
                progress = 0;
                progressBar.setProgress(progress);
                progressBar.setVisibility(View.VISIBLE);

                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        progress+=222;
                        progressBar.setProgress(progress);
                        if(progressBar.getProgress() ==10000){

                            timer.cancel();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                };
                timer.schedule(timerTask,0,1000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(ThirdActivity.this, "Respiratory Rate Recorded", Toast.LENGTH_SHORT).show();
                        sensor_manager.unregisterListener(ThirdActivity.this);
                        calculate_res_rate(values_z_dim);
                    }

                },45000);

            }
        });

        heart_rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ThirdActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(ThirdActivity.this, Measure_Heart_Rate.class);
                    startActivityForResult(intent,5);




                }
                else{
                    Toast.makeText(ThirdActivity.this, "Please grant permission to access the camera!!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(ThirdActivity.this, new String[]{Manifest.permission.CAMERA}, 200);
                }

            }
        });

        symptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThirdActivity.this, FourthActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            values_z_dim.add((double) event.values[2]);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void calculate_res_rate(ArrayList<Double> values_z_dim){
        res_rate = 0;
        int len = values_z_dim.size();
        ArrayList<Float> values_z_avg = new ArrayList<>();
        ArrayList<Float> values_z_result = new ArrayList<>();

        for (int count = len % 25; (count + 25) < len; count += 5) {
            int sum = 0;
            for (int z = count; z < 25 + count; z++) {
                sum += values_z_dim.get(z);
            }
            values_z_avg.add((float) (sum / 25));
        }


        for (int count = 1; count < values_z_avg.size(); count++) {
            values_z_result.add((float)values_z_avg.get(count) - values_z_avg.get(count - 1));
        }
        for (int count = 1; count < values_z_result.size(); count++) {
            if (values_z_result.get(count) == 0 || (values_z_result.get(count - 1) > 0 && values_z_result.get(count) < 0) || (values_z_result.get(count - 1) < 0 && values_z_result.get(count) > 0)) {
                res_rate += 1;
            }
        }
        respiratory_rate = String.valueOf(res_rate * 30 / 45);
        measure_respiratory_rate.setText(("Respiratory Rate: "+ respiratory_rate+" breaths per minute"));
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5 && resultCode == 300) {

            if (data.hasExtra("heart_rate")) {

                heart_rate = data.getStringExtra("heart_rate");
                measure_heart_rate.setText("Heart Rate: "+ heart_rate+" beats per minute");

            }

        }
    }
}
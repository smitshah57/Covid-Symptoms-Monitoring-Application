package com.example.symptoms_monitoring;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class Measure_Heart_Rate extends AppCompatActivity {


    TextView display_heart_rate;
    ProgressBar progressBar;
    TextureView camera_view;
    int heart_rate_measure;
    int cur_avg;
    int prev_avg;
    int final_avg;
    String id;
    CameraDevice device;
    Size dim;
    Handler thread_handler;
    HandlerThread thread;
    CameraCaptureSession session;
    CaptureRequest.Builder builder;
    long[] time_values;
    int captures = 0;
    int beats = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure__heart__rate);

        camera_view = findViewById(R.id.camera_view);
        camera_view.setSurfaceTextureListener(textureListener);
        progressBar = findViewById(R.id.progressBar);
        time_values = new long[45];
        display_heart_rate = (TextView) findViewById(R.id.display_heart_rate);
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Bitmap bitmap = camera_view.getBitmap();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[height * width];
            bitmap.getPixels(pixels, 0, width, width / 2, height / 2, width / 15, height / 15);

            for (int i = 0; i < height * width; i++) {
                int red = (pixels[i] >> 16) & 0xFF;
                count = count + red;
            }

            if (captures == 15) {
                cur_avg = count;
            } else if (captures >= 35) {
                cur_avg = (cur_avg * 20 + count) / 30;
                if (beats < 45 && prev_avg > cur_avg) {
                    time_values[beats] = System.currentTimeMillis();
                    beats++;
                    if (beats == 45) {
                        int avg;
                        long[] a = new long[44];
                        for (int i = 0; i < 44; i++) {
                            a[i] = time_values[i + 1] - time_values[i];
                        }
                        Arrays.sort(a);
                        avg = (int) a[a.length / 2];
                        heart_rate_measure = 60000 / avg;
                        display_heart_rate.setText("Heart Rate = " + heart_rate_measure + " BPM");
                        Intent intent = new Intent();
                        intent.putExtra("heart_rate", heart_rate_measure + "");
                        Measure_Heart_Rate.this.setResult(300, intent);
                        Toast.makeText(Measure_Heart_Rate.this, "Heart Rate Value recorded, Go back to upload the values ", Toast.LENGTH_LONG).show();
                    }
                }
            }
            captures++;

        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int bitmapWidth, int bitmapHeight) {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                id = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                dim = map.getOutputSizes(SurfaceTexture.class)[0];

                if (ActivityCompat.checkSelfPermission(Measure_Heart_Rate.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.openCamera(id, stateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int bitmapWidth, int bitmapHeight) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
    };


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onError(CameraDevice camera, int error) {

        }

        @Override
        public void onOpened(CameraDevice camera) {
            try {
                SurfaceTexture texture = camera_view.getSurfaceTexture();

                Surface surface = new Surface(texture);
                builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(surface);
                device.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

                        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                        try {
                            session.setRepeatingRequest(builder.build(), null, thread_handler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        thread = new HandlerThread("Camera Thread");
        thread.start();
        thread_handler = new Handler(thread.getLooper());
        if (camera_view.isAvailable()) {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                id = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                dim = map.getOutputSizes(SurfaceTexture.class)[0];

                if (ActivityCompat.checkSelfPermission(Measure_Heart_Rate.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.openCamera(id, stateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }


        } else {
            camera_view.setSurfaceTextureListener(textureListener);
        }
    }

}

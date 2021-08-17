package com.example.symptoms_monitoring;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.FormatFlagsConversionMismatchException;

public class FourthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner symptoms_list;
    RatingBar symptoms_ratings;
    Button upload_symptoms;
    private dbHandler dbHandler;
    String Nausea="0";
    String Headache="0";
    String Diarrhoea="0";
    String Soar_Throat="0";
    String Fever="0";
    String Muscle_Ache="0";
    String Loss_Smell_Taste="0";
    String Cough="0";
    String Shortness_of_Breath="0";
    String Feeling_Tired="0";
    String first_name;
    String count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        dbHandler = new dbHandler(this,null,null,1);
        symptoms_list = findViewById(R.id.symptoms_list);
        upload_symptoms = findViewById(R.id.upload_symptoms);
        symptoms_ratings = findViewById(R.id.symptoms_ratings);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Symptoms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symptoms_list.setAdapter(adapter);
        symptoms_list.setOnItemSelectedListener(this);
        Cursor data = dbHandler.getData();
        while(data.moveToNext()){
            first_name = data.getString(0);
            break;
        }
        upload_symptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.onCreateTable(first_name);
                Cursor lastID= dbHandler.findlastID(first_name);
                while(lastID!=null&&lastID.moveToFirst()){
                    count = lastID.getString(0);
                    break;

                }
                lastID.close();

                dbHandler.onInsertSymptoms(first_name,Nausea,Headache,Diarrhoea,Soar_Throat,Fever,Muscle_Ache,Loss_Smell_Taste,Cough,Shortness_of_Breath,Feeling_Tired,count);
                Nausea = "0";
                Headache = "0";
                Diarrhoea = "0";
                Soar_Throat= "0";
                Fever="0";
                Muscle_Ache="0";
                Loss_Smell_Taste="0";
                Cough="0";
                Shortness_of_Breath="0";
                Feeling_Tired="0";
                Toast.makeText(FourthActivity.this, "Symptoms Uploaded successfully!!", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        symptoms_ratings.setRating(0);
        symptoms_ratings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating = symptoms_ratings.getRating();
                if(rating!=0){

                    String text = parent.getItemAtPosition(position).toString();
                    String s = Float.toString(rating);
                    if(text.equals("Nausea")){
                        Nausea = s;
                    }
                    else if(text.equals("Headache")){
                        Headache = s;
                    }
                    else if(text.equals("Diarrhoea")){
                        Diarrhoea = s;
                    }
                    else if(text.equals("Soar Throat")){
                        Soar_Throat = s;
                    }
                    else if(text.equals("Fever")){
                        Fever = s;
                    }
                    else if(text.equals("Muscle Ache")){
                        Muscle_Ache = s;
                    }
                    else if(text.equals("Loss Smell or Taste")){
                        Loss_Smell_Taste = s;
                    }
                    else if(text.equals("Cough")){
                        Cough = s;
                    }
                    else if(text.equals("Shortness of Breath")){
                        Shortness_of_Breath = s;
                    }else if(text.equals("Feeling Tired")){
                        Feeling_Tired = s;
                    }



                }


            }
        });


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
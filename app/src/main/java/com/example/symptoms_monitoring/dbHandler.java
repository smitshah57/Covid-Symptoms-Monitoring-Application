package com.example.symptoms_monitoring;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

public class dbHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION =1;
    private static final String  DATABASE_NAME = "Shah.db";


    public dbHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE Personal_Details "+"(First_name" + " TEXT,Last_Name" + " TEXT" + ");";
        db.execSQL(query);
    }
    public void onCreateTable(String Table_name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "CREATE TABLE IF NOT EXISTS " + Table_name +"(" +
                "ID INTEGER primary key,Heart_Rate TEXT,Respiratory_Rate TEXT, Nausea Float, Headache Float,Diarrhoea Float,Soar_Throat Float,Fever Float,Muscle_Ache Float,Loss_Smell_Taste Float,Cough Float,Shortness_Breath Float,Feeling_Tired Float" +
                ");";
        db.execSQL(query);

    }
    public void add_user_detail(String first_name, String last_name){
        ContentValues values = new ContentValues();
        values.put("First_name",first_name);
        values.put("Last_name",last_name);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("Personal_Details",null,values);

    }
    public Cursor getData(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "Select * from Personal_Details";
        Cursor c = db.rawQuery(query,null);
        return c;
    }
    public void onInsertRates(String Table_name, String heart_rate,String respiratory_rate){
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO " + Table_name +"(" +
                "Heart_Rate,Respiratory_Rate) VALUES"+"("+heart_rate+","+respiratory_rate+")"+";";
        db.execSQL(query);

    }
    public void onInsertSymptoms(String Table_name,String Nausea, String Headache, String Diarrhoea, String Soar_Throat, String Fever, String Muscle_Ache, String Loss_Smell_Taste, String Cough,String Shortness_of_Breath,String Feeling_Tired,String count){
        SQLiteDatabase db = getWritableDatabase();
        String query = "Update "+Table_name+" Set Nausea ="+Nausea+",Headache="+Headache+",Diarrhoea="+Diarrhoea+",Soar_Throat="+Soar_Throat+",Fever="+Fever+",Muscle_Ache="+Muscle_Ache+",Loss_Smell_Taste="+Loss_Smell_Taste+",Cough="+Cough+",Shortness_Breath="+Shortness_of_Breath+",Feeling_Tired="+Feeling_Tired+" Where ID ="+count+";";
        db.execSQL(query);

    }

    public Cursor findlastID(String Table_name){
        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM "+Table_name+" WHERE  ID = (SELECT MAX(ID)  FROM "+Table_name+");" ;
        Cursor c = db.rawQuery(query,null);
        return c;



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




}

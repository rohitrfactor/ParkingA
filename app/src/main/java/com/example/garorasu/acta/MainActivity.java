package com.example.garorasu.acta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private parkingLot[] p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            System.out.println(p.length);
        }catch (Exception e){
            System.out.println("Initiate and reset the data");
            initializeParkingLot();
        }
        dashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dashboard();
    }
    public void dashboard(){
        loadParkingLot();
        System.out.println("Data loaded from the internal storage");
        TextView vacantSpot = (TextView)findViewById(R.id.vacantSpot);
        String vs = String.valueOf(findTotalVacantSpot());
        vacantSpot.setText(vs);
        TextView totalSpot = (TextView)findViewById(R.id.totalSpot);
        String ts = String.valueOf(totalParkingSpot());
        totalSpot.setText(ts);
    }

    public void inActivity(View v){
        Intent inA = new Intent(this,InActivity.class);
        startActivity(inA);
    }
    public void outActivity(View v){
        Intent inO = new Intent(this,OutActivity.class);
        startActivity(inO);
    }
    public void resetActivity(View view){
        initializeParkingLot();
        dashboard();
        Toast.makeText(this,"Parking reset to zero",
                Toast.LENGTH_SHORT).show();
    }
    public void listParkingSpotActivity(View v){
        Intent lpS = new Intent(this,CardsParkedCars.class);
        startActivity(lpS);
    }

    public void initializeParkingLot(){
        String r = loadJSONFromAsset();
        ///System.out.println(r);
        mCreateAndSaveFile("rohit",r);
    }

    public void enterVehicle(String vid) {
        int index = findVacantSpot();
        if (index >= 0) {
            p[index].enterVehicle(vid);
            System.out.println(vid + " vehicle successfully parked at spot " + index);
            System.out.println(p[index].getVid());
            Gson gson = new GsonBuilder().create();
            String x = gson.toJson(p);
            System.out.println("Data loaded");
            mCreateAndSaveFile("rohit",x);
        }
        else{
            System.out.println("Parking full");
        }
    }
    public void exitVehicle(String vid){
        int i=0;
        for(parkingLot x:p){
            if(x.getVid()==vid){
                p[i].exitVehicle();
                // vehicle successfully exited.
                System.out.println(vid+" vehicle successfully exited");
                Gson gson = new GsonBuilder().create();
                String x1 = gson.toJson(p);
                System.out.println("Data loaded");
                mCreateAndSaveFile("rohit",x1);
                return;
            }
            i++;
        }
     //vehicle not found
        System.out.println(vid+" vehicle Not found");
    }
    public void printAll(int size){
        int a=0;
        for(parkingLot x: p){
            if(a<size) {
                System.out.println("Lid: " + x.getLid());
                System.out.println("Ocp: " + x.getOcp());
                System.out.println("Vid: " + x.getVid());
                a++;
            }
        }
    }
    public int findTotalVacantSpot(){
        int totalVacantSpot = 0;
        for(parkingLot x:p){
            if(!x.getOcp()){
                totalVacantSpot++;
            }
        }
        return totalVacantSpot;
    }
    public int totalParkingSpot(){
        return p.length;
    }
    public int findVacantSpot(){
        int i=0;
        for(parkingLot x:p){
            if(x.getOcp()==false){
                return i;
            }
         i++;
        }
       return -1;
    }

    public void loadParkingLot(){
        String red = mReadJsonData("rohit");
        Gson gson = new GsonBuilder().create();
        p = gson.fromJson(red, parkingLot[].class);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("parkingLot.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            FileWriter file = new FileWriter("/data/data/" + getApplicationContext().getPackageName() + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String mReadJsonData(String params) {
        String mResponse = null;
        try {
            File f = new File("/data/data/" + getPackageName() + "/" + params);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            mResponse = new String(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mResponse;
    }
}

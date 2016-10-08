package com.example.garorasu.acta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class OutActivity extends AppCompatActivity {
    private static final String TAG = null;
    private parkingLot[] p;
    int logic;
    int lid;
    int res;
    private AutoCompleteTextView vehicleRegistrationNoOut;
    private ArrayList<String> StringArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);
        TextView noVehicle = (TextView) findViewById(R.id.noVehicle);
        noVehicle.setVisibility(View.INVISIBLE);
        loadParkingLot();
        System.out.println("Find total vacant spot "+findTotalVacantSpot());
        vehicleRegistrationNoOut = (AutoCompleteTextView) findViewById(R.id.vehicleRegistrationNoOut);
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, R.layout.card_view, R.id.card_vid, StringArray);
        vehicleRegistrationNoOut.setAdapter(adapter);
        vehicleRegistrationNoOut.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG,"Enter pressed");
                    String s = vehicleRegistrationNoOut.getText().toString();
                    if(s.length()<4){
                        vehicleRegistrationNoOut.setError("Minimum Length of vehicle number is 4 digits");
                    }else {
                        exitVehicle(s);
                        finish();
                    }

                }
                return false;
            }
        });
    }
    public void searchVehicle(){

    }
    public void submitCarNumberOut(View v){
        EditText vehicleRegistrationNoOut = (EditText)findViewById(R.id.vehicleRegistrationNoOut);
        String s = vehicleRegistrationNoOut.getText().toString();
        exitVehicle(s);
        finish();
    }
    public void enterVehicle(String vid) {
        int index = findVacantSpot();
        if (index >= 0) {
            p[index].enterVehicle(vid);
            System.out.println(vid + " vehicle successfully parked at spot " + index);
            int spot = index+1;
            Toast.makeText(this, vid + " vehicle successfully parked at spot " + spot,
                    Toast.LENGTH_SHORT).show();
            System.out.println(p[index].getVid());
            Gson gson = new GsonBuilder().create();
            String x = gson.toJson(p);
            System.out.println("Data loaded");
            mCreateAndSaveFile("rohit",x);
        }
        else{
            System.out.println("Parking full");
            Toast.makeText(this,"Parking full",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void exitVehicle(String vid){
        int i=0;
        for(parkingLot x:p){
            System.out.println("Vehicle searched "+x.getVid()+" matched for "+vid);
            String veh = x.getVid();
            boolean bo = vid.matches(x.getVid());
            System.out.println(bo);
            if(bo){
                p[i].exitVehicle();
                // vehicle successfully exited.
                System.out.println(vid+" vehicle successfully exited");
                Toast.makeText(this,vid+" vehicle successfully exited",
                        Toast.LENGTH_SHORT).show();
                Gson gson = new GsonBuilder().create();
                String x1 = gson.toJson(p);
                System.out.println("Data loaded");
                mCreateAndSaveFile("rohit",x1);
                return;
            }
            i++;
        }
        //vehicle not found
        Toast.makeText(this,vid+" vehicle Not found",
                Toast.LENGTH_LONG).show();
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
        StringArray = new ArrayList<String>();
        for(parkingLot x: p){
            if(x.getOcp()) {
                StringArray.add(x.getVid());
            }
        }
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

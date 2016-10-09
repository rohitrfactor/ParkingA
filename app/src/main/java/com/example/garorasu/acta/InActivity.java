package com.example.garorasu.acta;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.List;

public class InActivity extends AppCompatActivity {
    private static final String TAG = null;
    private parkingLot[] p;
    int logic;
    int lid;
    int res;
    private EditText vidSubCode,vidACode,vehicleRegistrationNo;
    private Spinner state_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_new);
        loadParkingLot();
        state_spinner = (Spinner) findViewById(R.id.state_spinner);
        loadState();
        System.out.println("Find total vacant spot "+findTotalVacantSpot());
        vidSubCode = (EditText)findViewById(R.id.vidSubCode);
        vidSubCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)||(actionId == EditorInfo.IME_ACTION_NEXT)) {
                    Log.i(TAG,"Enter pressed");
                    String vsc = vidSubCode.getText().toString();
                    if(vsc.length()<2){
                        vidSubCode.setError("Minimum Length is 2 digits");
                    }else {
                        vidACode.requestFocus();
                    }
                }
                return false;
            }
        });
        vidACode = (EditText)findViewById(R.id.vidACode);
        vidACode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)||(actionId == EditorInfo.IME_ACTION_NEXT)) {
                    Log.i(TAG,"Enter pressed");
                    vehicleRegistrationNo.requestFocus();
                }
                return false;
            }
        });
        vehicleRegistrationNo = (EditText)findViewById(R.id.vehicleRegistrationNo);
        vehicleRegistrationNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG,"Enter pressed");
                    String s = vehicleRegistrationNo.getText().toString();
                    String vsc = vidSubCode.getText().toString();
                    if(s.length()<4){
                        vehicleRegistrationNo.setError("Minimum Length of vehicle number is 4 digits");
                    }else if(vsc.length()<2){
                        vidSubCode.setError("Minimum Length is 2 digits");
                        vidSubCode.requestFocus();
                    } else {
                        String car = state_spinner.getSelectedItem().toString()+" "+vidSubCode.getText().toString()+vidACode.getText().toString()+" "+vehicleRegistrationNo.getText().toString();
                        int result = enterVehicle(car);
                        switch (result){
                            case -1:
                                vehicleRegistrationNo.setError(s+" vehicle already parked");
                                vehicleRegistrationNo.setText("");
                                break;
                            case -2:
                            case 0:
                                finish();
                        }

                    }
                }
                return false;
            }
        });
    }

    public void loadState(){
        List<String> stateCodes = new ArrayList<String>();
        stateCodes.add("HR");
        stateCodes.add("UP");
        stateCodes.add("RJ");
        stateCodes.add("HP");
        stateCodes.add("UA");
        stateCodes.add("PB");
        stateCodes.add("DL");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.list_view,stateCodes);

        // attaching data adapter to spinner
        state_spinner.setAdapter(dataAdapter);
    }

    public void submitCarNumber(View v){
        EditText vehicleRegistrationNo = (EditText)findViewById(R.id.vehicleRegistrationNo);
        String s = vehicleRegistrationNo.getText().toString();
        enterVehicle(s);
        finish();
    }

    public int enterVehicle(String vid) {
        int index = findVacantSpot();
        if (index >= 0) {
            for(parkingLot x:p){
                if(x.getVid().equals(vid)){
                    System.out.println(vid+" vehicle is already parked.");
                    Toast.makeText(this,vid+" vehicle is already parked.",
                            Toast.LENGTH_SHORT).show();
                    return -1;
                }
            }
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
                    Toast.LENGTH_SHORT).show();
            return -2;
        }
        return 0;
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

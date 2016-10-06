package com.example.garorasu.acta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ListParkingSpot extends AppCompatActivity {

    private static final String TAG = null;
    private parkingLot[] p;
    int logic;
    int lid;
    int res;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_parking_spot);
        loadParkingLot();
        ArrayList<String> StringArray = new ArrayList<String>();
        for(parkingLot x: p){
            StringArray.add(x.getVid());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_view,StringArray);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean flag = false;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListParkingSpot.this);
                builder.setMessage(R.string.extmsg)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                flag = true;
                                p[i].exitVehicle();
                                System.out.println(" vehicle successfully exited");
                                Gson gson = new GsonBuilder().create();
                                String x1 = gson.toJson(p);
                                System.out.println("Data loaded");
                                mCreateAndSaveFile("rohit",x1);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        }).show();
            }
        });
    }

    public void submitCarNumber(View v){
        EditText vehicleRegistrationNo = (EditText)findViewById(R.id.vehicleRegistrationNo);
        String s = vehicleRegistrationNo.getText().toString();
        enterVehicle(s);
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

package com.example.garorasu.acta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

public class CardsParkedCars extends AppCompatActivity {
    private static final String TAG = null;
    private parkingLot[] p;
    int logic;
    int lid;
    int res;
    private ArrayAdapter adapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_parked_cars);
        publishNumberPlates();
    }
    @Override
    protected void onResume() {
        super.onResume();
        publishNumberPlates();
    }

    public void publishNumberPlates(){
        loadParkingLot();
        ArrayList<String> StringArray = new ArrayList<String>();
        for(parkingLot x: p){
            if(x.getOcp()) {
                //String car = x.getVid().toString().substring(0,1)+x.getVid().toString().substring(2,x.getVid().length()-4)+" "+x.getVid().toString().substring(x.getVid().length()-3,x.getVid().length());
                StringArray.add(x.getVid());
            }
        }
        TextView noVehicle = (TextView) findViewById(R.id.noVehicle);

        if(StringArray.size()==0){
            noVehicle.setVisibility(View.VISIBLE);
        }else {
            noVehicle.setVisibility(View.INVISIBLE);

            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new MyAdapter(StringArray);
            mRecyclerView.setAdapter(mAdapter);
        }
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
                System.out.println(" vehicle successfully exited");
                Gson gson = new GsonBuilder().create();
                String x1 = gson.toJson(p);
                System.out.println("Data loaded");
                mCreateAndSaveFile("rohit",x1);
                int spot = i+1;
                Toast.makeText(getApplicationContext(),vid+" vehice successfully exited from spot "+spot,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            i++;
        }
        //vehicle not found
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
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView cardVid;
            public ViewHolder(View v) {
                super(v);
                cardVid = (TextView) v.findViewById(R.id.card_vid);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.cardVid.setText(mDataset.get(position));
            holder.cardVid.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    String vid = mDataset.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardsParkedCars.this);
                      builder.setMessage("Are you sure you want to exit vehicle "+vid+" ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Exit ith vehicle
                                    String vid = mDataset.get(position);
                                    exitVehicle(vid);
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

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}

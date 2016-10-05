package com.example.garorasu.acta;

/**
 * Created by garorasu on 2/10/16.
 */
public class parkingLot {
    private int lid;
    private boolean ocp;
    private String vid;

    public int getLid(){
        return lid;
    }
    public boolean getOcp(){
        return ocp;
    }
    public String getVid(){
        return vid;
    }

    public void enterVehicle(String vid){
        ocp = true;
        this.vid = vid;
    }
    public void exitVehicle(){
        ocp = false;
        this.vid = "na";
    }
}

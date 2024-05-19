package com.go.sgm_android.model;

public class Prediction {
    private String PPname;
    private float PPtargetCapacity;
    private float PPtotalCurrentCapacity;

    public Prediction(String PPname, float PPtargetCapacity, float PPtotalCurrentCapacity) {
        this.PPname = PPname;
        this.PPtargetCapacity = PPtargetCapacity;
        this.PPtotalCurrentCapacity = PPtotalCurrentCapacity;
    }

    public String getPPname() {
        return PPname;
    }

    public void setPPname(String PPname) {
        this.PPname = PPname;
    }

    public float getPPtargetCapacity() {
        return PPtargetCapacity;
    }

    public void setPPtargetCapacity(float PPtargetCapacity) {
        this.PPtargetCapacity = PPtargetCapacity;
    }

    public float getPPtotalCurrentCapacity() {
        return PPtotalCurrentCapacity;
    }

    public void setPPtotalCurrentCapacity(float PPtotalCurrentCapacity) {
        this.PPtotalCurrentCapacity = PPtotalCurrentCapacity;
    }
}

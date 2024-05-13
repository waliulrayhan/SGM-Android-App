package com.go.sgm_android.model;

public class PowerPlant {
    private String PPdivision;
    private String PPdistrict;
    private String PPupazilla;
    private String PPoperator;
    private String PPownership;
    private String PPfuelType;
    private String PPmethod;
    private String PPoutput;
    private String PPname;
    private float PPcurrentCapacity;
    private float PPtargetCapacity;
    private float PPtotalCurrentCapacity;

    public PowerPlant(String PPdivision, String PPdistrict, String PPupazilla, String PPoperator, String PPownership, String PPfuelType, String PPmethod, String PPoutput, String PPname) {
        this.PPdivision = PPdivision;
        this.PPdistrict = PPdistrict;
        this.PPupazilla = PPupazilla;
        this.PPoperator = PPoperator;
        this.PPownership = PPownership;
        this.PPfuelType = PPfuelType;
        this.PPmethod = PPmethod;
        this.PPoutput = PPoutput;
        this.PPname = PPname;
    }

    public PowerPlant(String PPname, float PPcurrentCapacity, float PPtargetCapacity) {
        this.PPname = PPname;
        this.PPcurrentCapacity = PPcurrentCapacity;
        this.PPtargetCapacity = PPtargetCapacity;
    }
    public PowerPlant(String PPname, float PPcurrentCapacity, float PPtargetCapacity, float PPtotalCurrentCapacity) {
        this.PPname = PPname;
        this.PPcurrentCapacity = PPcurrentCapacity;
        this.PPtargetCapacity = PPtargetCapacity;
        this.PPtotalCurrentCapacity = PPtotalCurrentCapacity;
    }

    public String getPPname() {
        return PPname;
    }

    public void setPPname(String PPname) {
        this.PPname = PPname;
    }

    public String getPPdivision() {
        return PPdivision;
    }

    public void setPPdivision(String PPdivision) {
        this.PPdivision = PPdivision;
    }

    public String getPPdistrict() {
        return PPdistrict;
    }

    public void setPPdistrict(String PPdistrict) {
        this.PPdistrict = PPdistrict;
    }

    public String getPPupazilla() {
        return PPupazilla;
    }

    public void setPPupazilla(String PPupazilla) {
        this.PPupazilla = PPupazilla;
    }

    public String getPPoperator() {
        return PPoperator;
    }

    public void setPPoperator(String PPoperator) {
        this.PPoperator = PPoperator;
    }

    public String getPPownership() {
        return PPownership;
    }

    public void setPPownership(String PPownership) {
        this.PPownership = PPownership;
    }

    public String getPPfuelType() {
        return PPfuelType;
    }

    public void setPPfuelType(String PPfuelType) {
        this.PPfuelType = PPfuelType;
    }

    public String getPPmethod() {
        return PPmethod;
    }

    public void setPPmethod(String PPmethod) {
        this.PPmethod = PPmethod;
    }

    public String getPPoutput() {
        return PPoutput;
    }

    public void setPPoutput(String PPoutput) {
        this.PPoutput = PPoutput;
    }

    public float getPPcurrentCapacity() {
        return PPcurrentCapacity;
    }

    public void setPPcurrentCapacity(float PPcurrentCapacity) {
        this.PPcurrentCapacity = PPcurrentCapacity;
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

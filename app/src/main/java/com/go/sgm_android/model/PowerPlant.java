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
    private String PPcurrentCapacity;
    private String PPtargetCapacity;

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

    public PowerPlant(String PPname, String PPcurrentCapacity, String PPoutput) {
        this.PPname = PPname;
        this.PPcurrentCapacity = PPcurrentCapacity;
        this.PPoutput = PPoutput;
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

    public String getPPcurrentCapacity() {
        return PPcurrentCapacity;
    }

    public void setPPcurrentCapacity(String PPcurrentCapacity) {
        this.PPcurrentCapacity = PPcurrentCapacity;
    }

    public String getPPtargetCapacity() {
        return PPtargetCapacity;
    }

    public void setPPtargetCapacity(String PPtargetCapacity) {
        this.PPtargetCapacity = PPtargetCapacity;
    }
}

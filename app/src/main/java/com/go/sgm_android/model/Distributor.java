package com.go.sgm_android.model;

public class Distributor {
    private String DDdistributor;
    private String DDzone;
    private String DDcircle;
    private String DDname;
    private float DDcurrentDemand;
    private float DDtargetDemand;

    public Distributor(String DDname, String DDzone, String DDcircle,String DDdistributor) {
        this.DDname = DDname;
        this.DDzone = DDzone;
        this.DDcircle = DDcircle;
        this.DDdistributor = DDdistributor;
    }
    public Distributor(String DDdistributor, String DDzone, String DDcircle, String DDname, float DDcurrentDemand, float DDtargetDemand) {
        this.DDdistributor = DDdistributor;
        this.DDzone = DDzone;
        this.DDcircle = DDcircle;
        this.DDname = DDname;
        this.DDcurrentDemand = DDcurrentDemand;
        this.DDtargetDemand = DDtargetDemand;
    }

    public Distributor(String DDname, float DDcurrentDemand, float DDtargetDemand) {
        this.DDname = DDname;
        this.DDcurrentDemand = DDcurrentDemand;
        this.DDtargetDemand = DDtargetDemand;
    }

    public String getDDdistributor() {
        return DDdistributor;
    }

    public void setDDdistributor(String DDdistributor) {
        this.DDdistributor = DDdistributor;
    }

    public String getDDzone() {
        return DDzone;
    }

    public void setDDzone(String DDzone) {
        this.DDzone = DDzone;
    }

    public String getDDcircle() {
        return DDcircle;
    }

    public void setDDcircle(String DDcircle) {
        this.DDcircle = DDcircle;
    }

    public String getDDname() {
        return DDname;
    }

    public void setDDname(String DDname) {
        this.DDname = DDname;
    }

    public float getDDcurrentDemand() {
        return DDcurrentDemand;
    }

    public void setDDcurrentDemand(float DDcurrentDemand) {
        this.DDcurrentDemand = DDcurrentDemand;
    }

    public float getDDtargetDemand() {
        return DDtargetDemand;
    }

    public void setDDtargetDemand(float DDtargetDemand) {
        this.DDtargetDemand = DDtargetDemand;
    }
}

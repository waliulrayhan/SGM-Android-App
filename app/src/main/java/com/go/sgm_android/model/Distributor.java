package com.go.sgm_android.model;

public class Distributor {
    private String DDdistributor;
    private String DDzone;
    private String DDcircle;
    private String DDname;
    private String DDcurrentDemand;
    private String DDtargetDemand;

    public Distributor(String DDdistributor, String DDzone, String DDcircle, String DDname) {
        this.DDdistributor = DDdistributor;
        this.DDzone = DDzone;
        this.DDcircle = DDcircle;
        this.DDname = DDname;
    }
    public Distributor(String DDdistributor, String DDzone, String DDcircle, String DDname, String DDcurrentDemand, String DDtargetDemand) {
        this.DDdistributor = DDdistributor;
        this.DDzone = DDzone;
        this.DDcircle = DDcircle;
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

    public String getDDcurrentDemand() {
        return DDcurrentDemand;
    }

    public void setDDcurrentDemand(String DDcurrentDemand) {
        this.DDcurrentDemand = DDcurrentDemand;
    }

    public String getDDtargetDemand() {
        return DDtargetDemand;
    }

    public void setDDtargetDemand(String DDtargetDemand) {
        this.DDtargetDemand = DDtargetDemand;
    }
}

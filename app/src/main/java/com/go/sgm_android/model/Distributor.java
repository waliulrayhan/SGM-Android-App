package com.go.sgm_android.model;

public class Distributor {
    private String distributorName;
    private long currentDemand;
    private long targetDemand;

    public Distributor(String distributorName, long currentDemand, long targetDemand) {
        this.distributorName = distributorName;
        this.currentDemand = currentDemand;
        this.targetDemand = targetDemand;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public long getCurrentDemand() {
        return currentDemand;
    }

    public void setCurrentDemand(long currentDemand) {
        this.currentDemand = currentDemand;
    }

    public long getTargetDemand() {
        return targetDemand;
    }

    public void setTargetDemand(long targetDemand) {
        this.targetDemand = targetDemand;
    }
}

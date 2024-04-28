package com.go.sgm_android.model;

public class PowerPlant {
    private String name;
    private long currentCapacity;
    private long targetCapacity;

    public PowerPlant(String name, long currentCapacity, long targetCapacity) {
        this.name = name;
        this.currentCapacity = currentCapacity;
        this.targetCapacity = targetCapacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(long currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public long getTargetCapacity() {
        return targetCapacity;
    }

    public void setTargetCapacity(long targetCapacity) {
        this.targetCapacity = targetCapacity;
    }
}

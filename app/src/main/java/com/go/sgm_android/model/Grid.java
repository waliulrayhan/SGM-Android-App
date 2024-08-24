package com.go.sgm_android.model;

public class Grid {
    private String Name;
    private String NID;
    private String Phone;
    private String Address;
    private String Bank;
    private float GridtotalCurrentSupply;

    public Grid(String name, String NID, String phone, String address, String bank, float gridtotalCurrentSupply) {
        Name = name;
        this.NID = NID;
        Phone = phone;
        Address = address;
        Bank = bank;
        GridtotalCurrentSupply = gridtotalCurrentSupply;
    }

    public Grid(String name, String NID, String phone, float gridtotalCurrentSupply) {
        Name = name;
        this.NID = NID;
        Phone = phone;
        GridtotalCurrentSupply = gridtotalCurrentSupply;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNID() {
        return NID;
    }

    public void setNID(String NID) {
        this.NID = NID;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBank() {
        return Bank;
    }

    public void setBank(String bank) {
        Bank = bank;
    }

    public float getGridtotalCurrentSupply() {
        return GridtotalCurrentSupply;
    }

    public void setGridtotalCurrentSupply(float gridtotalCurrentSupply) {
        GridtotalCurrentSupply = gridtotalCurrentSupply;
    }
}

package com.example.tttscheduling;

public class AdminModel {
    private String Name = "", Email = "", Password = "", Address = "", Phone = "";

    public AdminModel() {
    }

    public AdminModel(String Name, String Email, String Password, String Address, String Phone) {
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.Address = Address;
        this.Phone = Phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }
}

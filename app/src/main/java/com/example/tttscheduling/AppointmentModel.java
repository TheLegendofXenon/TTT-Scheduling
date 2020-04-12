package com.example.tttscheduling;

public class AppointmentModel {
    private String Name = "", Email = "", Phone = "", Date = "", Time = "", DOB = "", Address = "";

    public AppointmentModel() {
    }

    public AppointmentModel(String Name, String Email, String Phone, String Date, String Time, String DOB, String Address) { // Add location later
        this.Name = Name;
        this.Email = Email;
        this.Phone = Phone;
        this.Date = Date;
        this.Time = Time;
        this.DOB = DOB;
        this.Address = Address;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }
}

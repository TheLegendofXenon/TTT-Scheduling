package com.example.tttscheduling;

public class PatientModel {
    private int icon;
    private String Name = "", Email = "", Password = "", DOB = "", SSN = "", Phone = "";

    public PatientModel() {
    }

    public PatientModel(int icon, String Name, String Email, String Password, String DOB, String SSN, String Phone) {
        this.icon = icon;
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.DOB = DOB;
        this.SSN = SSN;
        this.Phone = Phone;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getSSN() {
        return SSN;
    }

    public void setSSN(String SSN) {
        this.SSN = SSN;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }
}

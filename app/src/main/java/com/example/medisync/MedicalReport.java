package com.example.medisync;

public class MedicalReport {
    private String reportName;
    private String doctorName;
    private String hospitalName;
    private String date;
    private String description;

    //constructors
    public MedicalReport(String reportName,String doctorName,String hospitalName,String date, String description){
        this.reportName=reportName;
        this.doctorName=doctorName;
        this.hospitalName=hospitalName;
        this.date=date;
        this.description=description;
    }
    //display report details
    public void displayReport(){
        System.out.println("Report Name:"+reportName);
        System.out.println("Doctor:"+doctorName);
        System.out.println("Hospital:"+hospitalName);
        System.out.println("Date:"+date);
        System.out.println("Description:"+description);
    }

    //getters
    public String getReportName(){
        return reportName;
    }
    public String getDoctorName(){
        return doctorName;
    }
    public String getHospitalName(){
        return hospitalName;
    }
    public String getDate(){
        return date;
    }
    public String getDescription(){
        return description;
    }
    //setters
    public void setReportName(String reportName){
        this.reportName=reportName;
    }
    public void setDoctorName(String doctorName){
        this.doctorName=doctorName;
    }
    public void setHospitalName(String hospitalName){
        this.hospitalName=hospitalName;
    }
    public void setDate(String date){
        this.date=date;
    }
    public void setDescription(String description){
        this.description=description;
    }
}

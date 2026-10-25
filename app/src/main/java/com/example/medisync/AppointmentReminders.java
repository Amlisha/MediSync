package com.example.medisync;

public class AppointmentReminders extends Reminder {
    private String doctorName;
    private String hospitalName;
    //constructor
    public AppointmentReminders(String doctorName,String hospitalName,String date,String time,String description){
        super(date,time,description);
        this.doctorName=doctorName;
        this.hospitalName=hospitalName;
    }

    //overriding the abstract method
    @Override
    public void displayReminder(){
        System.out.println("----Appointment Reminder----");
        System.out.println("Doctor:"+doctorName);
        System.out.println("Hospital:"+hospitalName);
        System.out.println("Date:"+date);
        System.out.println("Time:"+time);
        System.out.println("Description:"+description);
    }

    //getters
    public String getDoctorName(){
        return doctorName;
    }
    public String getHospitalName(){
        return hospitalName;
    }

    //setters
    public void setDoctorName(String doctorName){
        this.doctorName=doctorName;
    }
    public void setHospitalName(String hospitalName){
        this.hospitalName=hospitalName;
    }



}

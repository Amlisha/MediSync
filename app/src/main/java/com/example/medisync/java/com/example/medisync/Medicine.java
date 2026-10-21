package com.example.medisync;

public class Medicine {
    private String medicineName;
    private String dosage;
    private String frequency;
    private String time;
    private String startDate;
    private String endDate;

    //constructor
    public Medicine(String medicineName,String dosage,String frequency,String time, String startDate, String endDate){
        this.medicineName=medicineName;
        this.dosage=dosage;
        this.frequency=frequency;
        this.time=time;
        this.startDate=startDate;
        this.endDate=endDate;
    }
    //display medicine details
    public void displayMedicine(){
        System.out.println("Medicine:"+medicineName);
        System.out.println("Dosage:"+dosage);
        System.out.println("Frequency:"+frequency);
        System.out.println("Time:"+time);
        System.out.println("Start Date:"+startDate);
        System.out.println("End Date:"+endDate);
    }
    //getters
    public String getMedicineName(){
        return medicineName;
    }
    public String getDosage(){
        return dosage;
    }
    public String getFrequency(){
        return frequency;
    }
    public String getTime(){
        return time;
    }
    public String getStartDate(){
        return startDate;
    }
    public String getEndDate(){
        return endDate;
    }

    //setters
    public void setMedicineName(String medicineName){
        this.medicineName=medicineName;
    }
    public void setDosage(String dosage){
        this.dosage=dosage;
    }
    public void setFrequency(String frequency){
        this.frequency=frequency;
    }
    public void setTime(String time){
        this.time=time;
    }
    public void setStartDate(String startDate){
        this.startDate=startDate;
    }
    public void setEndDate(String endDate){
        this.endDate=endDate;
    }
}

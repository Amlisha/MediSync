package com.example.medisync;

public abstract class Reminder {
    protected String date;
    protected String time;
    protected String description;

    //constructor
    public Reminder(String date,String time,String description){
        this.date=date;
        this.time=time;
        this.description=description;
    }

    //abstract method
    public abstract void displayReminder();

    //getters
    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
    public String getDescription(){
        return description;
    }
    //setters
    public void setDate(String date){
        this.date=date;
    }
    public void setTime(String time){
        this.time=time;
    }
    public void setDescription(String description){
        this.description=description;
    }
}

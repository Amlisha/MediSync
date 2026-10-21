package com.example.medisync;

public class MedicineReminder extends Reminder{
    private Medicine medicine;
    //constructors
    public MedicineReminder(Medicine medicine,String date,String time,String description){
        super(date,time,description);
        this.medicine=medicine;
    }
    //overriding the abstract method
    @Override
    public void displayReminder(){
        System.out.println("---Medicine Reminder---");
        System.out.println("Medicine:"+medicine.getMedicineName());
        System.out.println("Dosage:"+medicine.getDosage());
        System.out.println("Date:"+date);
        System.out.println("Time:"+time);
        System.out.println("Description:"+description);
    }

    //getter
    public Medicine getMedicine(){
        return medicine;
    }
    //setter
    public void setMedicine(Medicine medicine){
        this.medicine=medicine;
    }

}

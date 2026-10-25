package com.example.medisync;

public class HealthProfile {
    private int age;
    private String bloodGroup;
    private String allergies;
    private String medicalNotes;

    //constructors
    public HealthProfile(int age,String bloodGroup,String allergies,String medicalNotes){
        this.age=age;
        this.bloodGroup=bloodGroup;
        this.allergies=allergies;
        this.medicalNotes=medicalNotes;
    }

    //display health profile
    public void displayProfile(){
        System.out.println("Age:"+age);
        System.out.println("Blood Group:"+bloodGroup);
        System.out.println("Allergies:"+allergies);
        System.out.println("Medical Notes:"+medicalNotes);
    }

    //getters
    public int getAge(){
        return age;
    }
    public String getBloodGroup(){
        return bloodGroup;
    }
    public String getAllergies(){
        return allergies;
    }
    public String getMedicalNotes(){
        return medicalNotes;
    }
    //setters
    public void setAge(int age){
        this.age=age;
    }
    public void setBloodGroup(String bloodGroup){
        this.bloodGroup=bloodGroup;
    }
    public void setAllergies(String allergies){
        this.allergies=allergies;
    }
    public void setMedicalNotes(String medicalNotes){
        this.medicalNotes=medicalNotes;
    }
}

package com.example.medisync;

public class EmergencyContact {
    private String contactName;
    private String relationship;
    private String phoneNumber;

    //constructors
    public EmergencyContact(String contactName,String relationship,String phoneNumber){
        this.contactName=contactName;
        this.relationship=relationship;
        this.phoneNumber=phoneNumber;
    }
    //display contact details
    public void displayContact(){
        System.out.println("Contact Name:"+contactName);
        System.out.println("Relationship:"+relationship);
        System.out.println("Phone Number:"+phoneNumber);
    }

    //getters
    public String getContactName(){
        return contactName;
    }
    public String getRelationship(){
        return relationship;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }

    //setters
    public void setContactName(String contactName){
        this.contactName=contactName;
    }
    public void setRelationship(String relationship){
        this.relationship=relationship;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }
}

package com.example.medisync;

public class User {
    private String name;
    private String username;
    private String password;

    //constructors
    public User(String name,String username,String password){
        this.name=name;
        this.username=username;
        this.password=password;
    }
    //logic
    public boolean login(String username,String password){
        return
                this.username.equals(username)
                    &&
                this.password.equals(password);
    }
    //logout
    public void logout(){
        System.out.println("User logged out");
    }
    //display user details
    public void displayUserDetails(){
        System.out.println("Name:"+name);
        System.out.println("Username:"+username);
    }
    //getters
    public String getName(){
        return name;
    }
    public String getUsername(){
        return username;
    }

    //setters
    public void setName(String name){
        this.name=name;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public void setPassword(String password){
        this.password=password;
    }
}

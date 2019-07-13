package com.example.donardrones;

import java.util.Map;

public class User {
    private String user_name;
    private String email;
    private String phone;
    private int type;

    public User(String user_name, String email, String phone,int type) {
        this.user_name = user_name;
        this.email = email;
        this.phone = phone;
        this.type=type;
    }

    public User() {
        user_name="";
        email="";
        phone="";
        type=-1;
    }

    public User(Map<String,Object> m){
        user_name = m.get("user_name").toString();
        email = m.get("email").toString();
        phone = m.get("phone").toString();
        type= ((Long)m.get("type")).intValue();
    }
    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

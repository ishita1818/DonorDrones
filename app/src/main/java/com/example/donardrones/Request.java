package com.example.donardrones;

import java.util.Map;

public class Request {
    private String blood_group;
    private String units;
    private User acceptor;
    private User donor;
    private User medassist;
    private String active;
    private User_Location acceptor_location;
    private User_Location donor_location;
    private User_Location medassist_location;

    public User_Location getAcceptor_location() {
        return acceptor_location;
    }

    public void setAcceptor_location(User_Location acceptor_location) {
        this.acceptor_location = acceptor_location;
    }
    public Request(Map<String, Object> m){

        blood_group = m.get("blood_group").toString();
        units = m.get("units").toString();
        active = m.get("active").toString();
        acceptor = new User((Map<String,Object>)m.get("acceptor"));
        donor = new User((Map<String,Object>)m.get("donor"));
        medassist = new User((Map<String,Object>)m.get("medassist"));
        acceptor_location = new User_Location((Map<String,Object>)m.get("acceptor_location"));
        donor_location = new User_Location((Map<String,Object>)m.get("donor_location"));
        medassist_location = new User_Location((Map<String,Object>)m.get("medassist_location"));
    }

    public User_Location getDonor_location() {
        return donor_location;
    }

    public void setDonor_location(User_Location donor_location) {
        this.donor_location = donor_location;
    }

    public User_Location getMedassist_location() {
        return medassist_location;
    }

    public void setMedassist_location(User_Location medassist_location) {
        this.medassist_location = medassist_location;
    }

    public Request() {
        blood_group="";
        units="";
        acceptor= new User();
        donor=new User();
        medassist= new User();
        acceptor_location= new User_Location();
        donor_location= new User_Location();
        medassist_location= new User_Location();
    }

    public Request(String blood_group, String units, User acceptor, User donor, User medassist, String active) {
        this.blood_group = blood_group;
        this.units = units;
        this.acceptor = acceptor;
        this.donor = donor;
        this.medassist = medassist;
        this.active = active;
    }


    public String getBlood_group() {
        return blood_group;
    }

    public String getUnits() {
        return units;
    }

    public User getAcceptor() {
        return acceptor;
    }

    public User getDonor() {
        return donor;
    }

    public User getMedassist() {
        return medassist;
    }

    public String getActive() {
        return active;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setAcceptor(User acceptor) {
        this.acceptor = acceptor;
    }

    public void setDonor(User donor) {
        this.donor = donor;
    }

    public void setMedassist(User medassist) {
        this.medassist = medassist;
    }

    public void setActive(String active) {
        this.active = active;
    }
}

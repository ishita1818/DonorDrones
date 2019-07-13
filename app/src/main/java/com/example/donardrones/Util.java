package com.example.donardrones;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Util {
    public static User currentUser;

    public static DocumentReference request_reference=null;

    public static ArrayList<String> deny_request_ids=new ArrayList<>();

    public static ArrayList<String> getDeny_request_ids() {
        return deny_request_ids;
    }

    public static void setDeny_request_ids(ArrayList<String> deny_request_ids) {
        Util.deny_request_ids = deny_request_ids;
    }

    public static DocumentReference getRequest_reference() {
        return request_reference;
    }

    public static void setRequest_reference(DocumentReference request_reference) {
        Util.request_reference = request_reference;
    }

    public static void setCurrentUser(User currentUser) {
        Util.currentUser = currentUser;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getUserType(int type){
        switch (type) {
            case 0:
                return "acceptor";
            case 1:
                return "donor";
            case 2:
                return "medassist";
        }
            return "invalid";
    }
}

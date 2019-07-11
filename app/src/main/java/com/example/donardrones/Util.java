package com.example.donardrones;

public class Util {
    public static User currentUser;

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

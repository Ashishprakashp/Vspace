package com.example.v_space;

public class UserSession {
    private static UserSession instance;
    private String userEmail;
    private String L_id;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Get the singleton instance
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Getter for userEmail
    public String getUserEmail() {
        return userEmail;
    }
    public String getL_id() {
        return L_id;
    }
    public void setL_id(String L_id) {
        this.L_id = L_id;
    }

    // Setter for userEmail
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}


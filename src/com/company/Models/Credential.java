package com.company.Models;

import java.util.Comparator;

public class Credential implements Comparator<Credential> {
    private String user;
    private String password;
    private int priority;

    public Credential(String user, String password, int priority) {
        this.user = user;
        this.password = password;
        this.priority = priority;
    }

    @Override
    public int compare(Credential credential, Credential t1) {
        return credential.priority - t1.priority;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

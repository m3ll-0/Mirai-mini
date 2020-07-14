package com.company.Models;

import java.sql.Timestamp;

public class Vulnerable {

    private String server;
    private String user;
    private String password;
    private String protocol_type;
    private Timestamp time_discovered;

    public Vulnerable(String server, String user, String password, String protocol_type, Timestamp time_discovered) {
        this.server = server;
        this.user = user;
        this.password = password;
        this.protocol_type = protocol_type;
        this.time_discovered = time_discovered;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
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

    public String getProtocol_type() {
        return protocol_type;
    }

    public void setProtocol_type(String protocol_type) {
        this.protocol_type = protocol_type;
    }

    public Timestamp getTime_discovered() {
        return time_discovered;
    }

    public void setTime_discovered(Timestamp time_discovered) {
        this.time_discovered = time_discovered;
    }
}

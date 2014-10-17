package com.synload.socketframework.clienttest;

public class User {
    public User( String name, String password, String description, boolean isAdmin){
        this.setAdmin(isAdmin);
        this.setPassword(password);
        this.setName(name);
        this.setDescription(description);
    }
    public String name, password = "";
    public String description = "";
    public boolean isAdmin = false;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}

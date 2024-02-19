package com.example.mqtt_project.request;

import java.io.Serializable;
import java.util.Objects;

public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public LoginRequest(){

    }
    public LoginRequest(String user_acct,String user_pwd){
        this.user_acct = user_acct;
        this.user_pwd = user_pwd;
    }
    private String user_acct;
    private String user_pwd;

    public String getUser_acct() {
        return user_acct;
    }

    public void setUser_acct(String user_acct) {
        this.user_acct = user_acct;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(user_acct, that.user_acct) && Objects.equals(user_pwd, that.user_pwd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_acct, user_pwd);
    }
}

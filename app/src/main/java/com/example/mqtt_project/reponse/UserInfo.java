package com.example.mqtt_project.reponse;

public class UserInfo {
    private String user_name;
    private String user_tel;
    private String user_gender;
    private String wh_id;
    private String user_position;
    private String user_institution;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getWh_id() {
        return wh_id;
    }

    public void setWh_id(String wh_id) {
        this.wh_id = wh_id;
    }

    public String getUser_position() {
        return user_position;
    }

    public void setUser_position(String user_position) {
        this.user_position = user_position;
    }

    public String getUser_institution() {
        return user_institution;
    }

    public void setUser_institution(String user_institution) {
        this.user_institution = user_institution;
    }
}

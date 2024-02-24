package com.example.mqtt_project.pojo;

public class HcInfo {
    private String hc_name;
    private String hc_enname;
    private String remnant;

    public String getHc_name() {
        return hc_name;
    }

    public void setHc_name(String hc_name) {
        this.hc_name = hc_name;
    }

    public String getHc_enname() {
        return hc_enname;
    }

    public void setHc_enname(String hc_enname) {
        this.hc_enname = hc_enname;
    }

    public String getRemnant() {
        return remnant;
    }

    public void setRemnant(String remnant) {
        this.remnant = remnant;
    }

    public HcInfo(String hc_name, String hc_enname, String remnant) {
        this.hc_name = hc_name;
        this.hc_enname = hc_enname;
        this.remnant = remnant;
    }
}

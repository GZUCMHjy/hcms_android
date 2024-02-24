package com.example.mqtt_project.reponse;

public class QRcode {
    private String destDevice;
    private String account_id;
    private String originDevice;
    private String code_id;

    private String status;

    public String getDestDevice() {
        return destDevice;
    }

    public void setDestDevice(String destDevice) {
        this.destDevice = destDevice;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getOriginDevice() {
        return originDevice;
    }

    public void setOriginDevice(String originDevice) {
        this.originDevice = originDevice;
    }

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

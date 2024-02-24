package com.example.mqtt_project.reponse;

public class LoginResponse {
    private Integer status;
    private Integer account_id;

    public void setAccount_id(Integer account_id){
        this.account_id = account_id;
    }
    public Integer getAccount_id(){
        return account_id;
    }
    public void setStatus(Integer status){
        this.status = status;
    }
    public Integer getStatus(){
        return status;
    }
}

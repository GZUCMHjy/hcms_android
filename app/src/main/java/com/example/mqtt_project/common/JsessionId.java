package com.example.mqtt_project.common;

public class JsessionId {
    private static final ThreadLocal<String> tl = new ThreadLocal<>();

    public static void saveUser(String cookie){
        tl.set(cookie);
    }

    public static String getCookieValue(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}

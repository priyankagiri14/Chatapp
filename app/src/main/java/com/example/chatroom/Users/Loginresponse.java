package com.example.chatroom.Users;

import java.util.HashMap;
import java.util.Map;

public class Loginresponse {

    private LoginData data;
    private String message;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

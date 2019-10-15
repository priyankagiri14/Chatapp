package com.example.chatroom.Users.model;

import java.util.HashMap;
import java.util.Map;

public class Refreshtoken {

    private RefData data;
    private String message;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public RefData getData() {
        return data;
    }

    public void setData(RefData data) {
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

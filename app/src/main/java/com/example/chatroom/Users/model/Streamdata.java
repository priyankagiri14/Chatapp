package com.example.chatroom.Users.model;

import java.util.HashMap;
import java.util.Map;

public class Streamdata  {

    private String users;
    private Integer groupid;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
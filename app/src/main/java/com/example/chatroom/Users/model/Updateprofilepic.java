package com.example.chatroom.Users.model;

import java.util.HashMap;
import java.util.Map;

public class Updateprofilepic {

    private String profileId;
    private String id;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
package com.example.chatroom.Users.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fileupload {


    @SerializedName("data")
    @Expose
    private List<FileData> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<FileData> getData() {
        return data;
    }

    public void setData(List<FileData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
package com.example.chatroom.Users.model;

public class Message {
    private String text,msgtype,time,filename; // message body
    private MemberData memberData; // data of the user that sent this message
    public static String imageurl;

    public  Integer is_downloaded,msgid;
    public static boolean belongsToCurrentUser=false; // is this message sent by us?


    public Message(String text, String msgtype, String filename, String time, boolean belongsToCurrentUser , Integer is_downloaded, Integer msgid) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.msgtype=msgtype;
        this.time=time;
        this.msgid=msgid;
        this.is_downloaded =is_downloaded;
        this.filename=filename;
    }

    public  Integer getIs_downloaded() {
        return is_downloaded;
    }

    public Integer getMsgid() {
        return msgid;
    }

    public  String getFilename() {
        return filename;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getText() {
        return text;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public String getTime() {
        return time;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
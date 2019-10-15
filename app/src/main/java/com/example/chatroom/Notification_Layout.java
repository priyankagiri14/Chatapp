package com.example.chatroom;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class Notification_Layout extends AppCompatActivity {

    public static TextView appname,sendername,message;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);
        appname=findViewById(R.id.appname);
        sendername=findViewById(R.id.sendername);
        message=findViewById(R.id.message);
    }
}

package com.example.chatroom;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class Attachment_layout extends AppCompatActivity {
    public static LinearLayout mRevealView;
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachment_layout);

        mRevealView.setVisibility(View.INVISIBLE);
    }
}

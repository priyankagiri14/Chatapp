package com.example.chatroom.Users.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.chatroom.R;
import com.example.chatroom.Users.Chat.Chat_View;

public class Profile_Activity extends AppCompatActivity implements View.OnClickListener {

    ImageView profile;
    Context context;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);
        profile=findViewById(R.id.profileicon);
        profile.setOnClickListener(this);
        linearLayout=findViewById(R.id.userprofilelinear);
        linearLayout.setOnClickListener(this);
        context=getApplicationContext();
    }

    /** Handle different on click listener on different views
     * @param v Each view is differentiated by their resource id
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.userprofilelinear){
            Intent intent = new Intent(this, Profile_Setting.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}

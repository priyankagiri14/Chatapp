package com.example.chatroom.Users.Chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.R;
import com.example.chatroom.Users.model.Message;

import java.util.ArrayList;
import java.util.List;

public class Imageadapter  extends BaseAdapter {

    public static Context context;
    public List<String> imageurl ;
    public static ImageHolder imageHolder;


    public Imageadapter(List<String> imageurl,Context context) {
        this.imageurl=imageurl;
        this.context = context;
    }


    @Override
    public int getCount() {
        return imageurl.size();
    }

    @Override
    public Object getItem(int i) {
        return imageurl.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        String imgurl=imageurl.get(position);
        imageHolder=new ImageHolder();
        if(Chat_View.mediaFiles!=null){
            convertView=messageInflater.inflate(R.layout.image_message_layout,null);
            imageHolder.time=convertView.findViewById(R.id.time);
            imageHolder.sentimage=convertView.findViewById(R.id.sendimg);
            imageHolder.time.setText("13:26 PM");

        }
        return convertView;
    }
}
class ImageHolder {
    public static ImageView sentimage;
    public TextView time;
}

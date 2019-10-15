package com.example.chatroom.Users.Chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chatroom.R;

import java.util.List;

public class Receive_Msglist_Adapter  extends ArrayAdapter<String> {
    public static List<String> rcvmsg;
    public static int pos;

    private final Activity context;
    public static TextView rcvmsgbody;

    public Receive_Msglist_Adapter(Activity context,List<String> rcvmsg) {
        super(context, R.layout.receive_messagelist_adapter,rcvmsg);
        //super(context, R.layout.message_adapter,sentmsg,rcvmsg);
        this.context=context;
        this.rcvmsg=rcvmsg;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.receive_messagelist_adapter, null, true);
        rcvmsgbody=rowView.findViewById(R.id.rcv_message_body);
        rcvmsgbody.setText(rcvmsg.get(position));
        pos=position;
        return rowView;
    }


}


package com.example.chatroom.Users;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chatroom.R;
import com.example.chatroom.Userslist;

import java.util.ArrayList;
import java.util.List;

public class Userslistadapter extends ArrayAdapter<String> {

    private final List<String> usersidslist;
    private final List<String> usersnameslist;
    private final Userslist context;
     public Userslistadapter(Userslist context, List<String> usersidslist,List<String> usersnameslist) {
        super(context,R.layout.users_list_adapter,usersnameslist);
        this.context=context;
        this.usersidslist=usersidslist;
        this.usersnameslist=usersnameslist;
        this.notifyDataSetChanged();
    }




    @Override
public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.users_list_adapter, null, true);
        TextView usersids = (TextView) rowView.findViewById(R.id.userid);
        TextView usersnames=rowView.findViewById(R.id.username);

        if(usersidslist.get(position).equals("")){
          usersids.setVisibility(View.INVISIBLE);
            usersnames.setText(usersnameslist.get(position));
        }
        else if(!usersidslist.get(position).equals("")){
        usersids.setText(usersidslist.get(position));
            usersnames.setText(usersnameslist.get(position));
        }

        return rowView;
        }
        }

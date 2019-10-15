package com.example.chatroom.Users.individual_chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroom.R;
import com.example.chatroom.Users.Chat.MessageAdapter;
import com.example.chatroom.Userslist;
import com.mindorks.editdrawabletext.DrawablePosition;
import com.mindorks.editdrawabletext.EditDrawableText;
import com.mindorks.editdrawabletext.onDrawableClickListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Individual_chat extends AppCompatActivity implements onDrawableClickListener, View.OnClickListener {

    TextView personname, sentmsg;
    EditDrawableText typemsg;
    ImageView profileimg;
    ImageButton send;
    ListView sentmsglistview;
    MessageAdapter messageAdapter;
    private static final int PICK_Camera_IMAGE =1;
    private List<String> sentmsglist=new ArrayList<>();

    //Mqtt message
    public  static MqttMessage mqttMessage;
    Context appcontext;
    private MqttClient client;
    public static String publishtopic = "chat";
    public static MqttAndroidClient mqttAndroidClient;
    public static String publishmessage = "";
    public static byte[] encodedPayload;
    MqttHelper mqttHelper;
    public Handler handler = null;
    public static Runnable runnable = null;
    //List<String> publishmsgglist = new ArrayList<>();
    List<String> recvmsglist = new ArrayList<>();
    private SharedPreferences sharedPreferences,sharedPreferences1;
    Set<String> set,set1;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);
        personname = findViewById(R.id.person_name);
        personname.setText(Userslist.selectedusername);
        sentmsglistview=findViewById(R.id.sentmsglist);
        typemsg = findViewById(R.id.typemsg);
        send=findViewById(R.id.send);
        send.setOnClickListener(this);
        sentmsglistview.setDivider(null);
        typemsg.setDrawableClickListener(this);
        appcontext=getApplicationContext();
        sharedPreferences = getSharedPreferences("rcvkey", Context.MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences("sendey", Context.MODE_PRIVATE);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }
        startMqtt();
        //recvmsglist.add(mqttMessage.toString());


        recvmsglist.add("rcv");
        sentmsglist.add("sent");
        set = sharedPreferences.getStringSet("rcvkey", null);
        set1=sharedPreferences1.getStringSet("sendkey",null);
        set = new HashSet<>(recvmsglist);
        recvmsglist=new ArrayList<>(set);
        set1=new HashSet<>(sentmsglist);
        sentmsglist=new ArrayList<>(set1);
        //messageAdapter=new MessageAdapter(Individual_chat.this,sentmsglist,recvmsglist);
        sentmsglistview.setAdapter(messageAdapter);
        Log.d("rcvmsglist",recvmsglist.toString());
        Log.d("setlist",sentmsglist.toString());

      /* handler = new Handler();
         runnable = new Runnable() {
            public void run() {
                startMqtt();
                handler.postDelayed(runnable, 1000);
            }
         };

         handler.postDelayed(runnable, 1000);*/

    }

    public void startMqtt() {
        mqttHelper = new MqttHelper(appcontext);

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

//Log.d("messagearrived",mqttMessage.getPayload().toString());
                onSubscribe(mqttMessage);
                   // MqttHelper.mqttAndroidClient.subscribe(publishtopic,1);
                  /*  messageAdapter=new MessageAdapter(Individual_chat.this,sentmsglist,recvmsglist);
                    recvmsglist.add(mqttMessage.toString());
                    Log.d("rcvmsglist",recvmsglist.toString());
                    Log.d("recivedmsg",mqttMessage.toString());

                    //Log.d("Message arrived",mqttMessage.toString());
                      sentmsglistview.setAdapter(messageAdapter);

                editor=sharedPreferences.edit();
                Set<String> set = new HashSet<String>(recvmsglist);
                editor.putStringSet("rcvkey", set);
                editor.apply();*/


                // messageAdapter.sentmsgbody.setText(mqttMessage.toString());

//                Log.d("Debug", sentmsglist.get(Integer.parseInt(MessageAdapter.pos)));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        //   connect();
    }

    public void onSubscribe(final MqttMessage mqttMessage){
        try {
            MqttHelper.mqttAndroidClient.subscribe(publishtopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("IMqttToken",mqttMessage.toString());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    public void publishMessage(@NonNull MqttAndroidClient client,
                               @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        msg=typemsg.getText().toString();
        encodedPayload = msg.getBytes("UTF-8");
        mqttMessage = new MqttMessage(encodedPayload);
        mqttMessage.setId(123);
        mqttMessage.setRetained(true);
        mqttMessage.setQos(qos);
        client.publish(topic, mqttMessage);
    }


    @Override
    public void onClick(DrawablePosition target) {
        switch (target){
            case LEFT:
                Toast.makeText(this,"Imjoi clicked",Toast.LENGTH_SHORT).show();

                break;
            case RIGHT:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PICK_Camera_IMAGE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.send){
           /* publishmessage=typemsg.getText().toString();
            encodedPayload = new byte[0];
            try {
                encodedPayload = publishmessage.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mqttMessage=new MqttMessage(encodedPayload);
            mqttMessage.setId(123);
            mqttMessage.setRetained(true);
            mqttMessage.setQos(0);*/
          /*  try {
                MqttHelper.mqttAndroidClient.publish(publishtopic, mqttMessage, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("Mqtt","publish!");

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("Mqtt", "publish fail!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();

            }*/
          if(MqttHelper.mqttAndroidClient==null){
              return;
          }
          else if(MqttHelper.mqttAndroidClient.isConnected()) {
              encodedPayload = new byte[0];
              publishmessage=typemsg.getText().toString();
              try {
                  encodedPayload = publishmessage.getBytes("UTF-8");
              } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
              mqttMessage = new MqttMessage(encodedPayload);
              mqttMessage.setId(123);
              mqttMessage.setRetained(true);
              mqttMessage.setQos(1);
              try {
                  MqttHelper.mqttAndroidClient.publish(publishtopic,mqttMessage);
                  sentmsglist.add(mqttMessage.toString());
                  Log.d("sentmsglist", sentmsglist.toString());
                /*  messageAdapter=new MessageAdapter(Individual_chat.this,sentmsglist,recvmsglist);
                 sentmsglistview.setAdapter(messageAdapter);*/
                  editor1=sharedPreferences1.edit();
                  Set<String> set1 = new HashSet<String>(sentmsglist);
                  editor1.putStringSet("sendkey", set1);
                  editor1.apply();

              } catch (MqttException e) {
                  e.printStackTrace();
              }
               /* try {
                MqttHelper.mqttAndroidClient.publish(publishtopic, mqttMessage, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("messagepubsuc", mqttMessage.toString());
                        try {
                            MqttHelper.mqttAndroidClient.disconnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        sentmsglist.add(mqttMessage.toString());
                        messageAdapter=new MessageAdapter(Individual_chat.this,sentmsglist);
                        sentmsglistview.setAdapter(messageAdapter);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("Mqtt", "publish fail!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
*/              /*try {
                  MqttHelper.mqttAndroidClient.publish(publishtopic,mqttMessage);
                  Log.d("messagepub", mqttMessage.toString());
                  MqttHelper.mqttAndroidClient.disconnect();
                  sentmsglist.add(mqttMessage.toString());
                  messageAdapter=new MessageAdapter(Individual_chat.this,sentmsglist);
                  sentmsglistview.setAdapter(messageAdapter);
              } catch (MqttException e) {
                  e.printStackTrace();
              }*/
          }
            Log.d("sentmsglist",sentmsglist.toString());
            typemsg.setText("");

        }

    }
}

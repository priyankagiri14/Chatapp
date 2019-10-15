package com.example.chatroom.Users.Chat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.chatroom.Chat;

import java.util.Timer;
import java.util.TimerTask;

public class MymqttService extends Service {
    public static boolean isRunning = false;
    Mqttbasic mqttbasic;
    Handler handler;
    Timer timer = new Timer();
    TimerTask timerTask;
    MqttHelper mqttHelper;
    Chat chat;



      public MymqttService() {

          mqttbasic=new Mqttbasic(this);

      }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    public void getToast(){
        Toast.makeText(this,"runnnnnin",Toast.LENGTH_SHORT).show();
    }




@Override
 public void onCreate(){
        super.onCreate();
        isRunning=true;
    Intent intent=new Intent("com.example.CUSTOM_INTENT");
    sendBroadcast(intent);
    Log.d("","Mymqttservice");



        Log.d("Mymqttservice", "Mymqttserviceoncreate");
       // mqttbasic.subscribetopic(mqttbasic.mqttAndroidClient);
        /*timerTask=new TimerTask() {
            @Override
            public void run() {

                timer.scheduleAtFixedRate(timerTask, 0, 1000);


                //mqttbasic.subscribetopic(mqttbasic.mqttAndroidClient);


            }
        };*/


    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("Mymqttservice", "Mymqttserviceonstart");
        return Service.START_STICKY;



    }
        @Override
        public void onDestroy () {
            super.onDestroy();
            Intent intent = new Intent("com.example.CUSTOM_INTENT");
            sendBroadcast(intent);
            Log.d("Mymqttservice", "Mymqttservice");

        }


    }
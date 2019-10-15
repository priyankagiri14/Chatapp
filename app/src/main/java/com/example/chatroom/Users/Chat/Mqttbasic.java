package com.example.chatroom.Users.Chat;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chatroom.Chat;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Mqttbasic  {
    public  MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://soldier.cloudmqtt.com:17303";
    //final String serverUri = "mqtt://192.168.43.114:1883";
    final String clientId = "ExampleAndroidClient";
    final String topic = "chat";
    int qos=2;
    MqttMessage mqttMessage;

    final String username = "buwhatun";
    final String password = "Z3-fbg1GQ_mb";
    private Object usercontext;
    public  Context context;
    MqttHelper mqttHelper;
    Chat chat;
    public  String msg="";
    private String toast;


    public Mqttbasic(Context context) {
        this.context=context;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);


//        connect(mqttAndroidClient);
    }

    public void connect(final MqttAndroidClient mqttAndroidClient) {

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic1, MqttMessage message) throws Exception {
                Log.d("messagearrived1","topic "+topic1+" msg "+ message.toString());
                //Toast.makeText(context,"message "+chat.hi,Toast.LENGTH_SHORT).show();
                if(topic1.equals(topic)){
                    Toast.makeText(context, "msg "+message.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("messagearrived1","msg "+message.toString());
                }
                Log.d("messagearrived1","null ");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

               // subscribe(mqttAndroidClient);
            }

            public void subscribe(MqttAndroidClient mqttAndroidClient1) {
                if (mqttAndroidClient1.isConnected()) {
                    try {
                        mqttAndroidClient1.subscribe(topic, 2, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                            }
                        }, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                Log.d("messagearrived", topic+" "+message.toString());
                            mqttAndroidClient.unsubscribe(topic);
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if(!mqttAndroidClient.isConnected()) {
            try {

                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(true);
                        disconnectedBufferOptions.setDeleteOldestMessages(true);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        if (mqttAndroidClient == null) {
                            return;
                        } else {
                            Log.d("Connection succesful", "We are connected");
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("Connection failure" +exception, "Not connected");
                    }
                });


            } catch (MqttException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void subscribetopic(MqttAndroidClient client) {

        client=mqttAndroidClient;

                if(!client.isConnected()){
                    Log.d("messagearrived", "notconnected");
                    connect(mqttAndroidClient);
                }
            else if(client.isConnected()){
                try {


                    client.subscribe(topic, 2, new IMqttMessageListener() {
                                @Override
                                public void messageArrived(String topic, MqttMessage message) throws Exception {
                                    if(message.toString().equals(null)){
                                        Log.d("messagearrivedd", "null");
                                    }
                                    else if(message.toString() != null) {
                                        Log.d("messagearrivedd", message.toString());
                                        Toast.makeText(context, "msg " + message.toString(), Toast.LENGTH_SHORT).show();

                                    }
                                    unsubscribe(mqttAndroidClient);

                                }
                            });

                }

             catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }




    public void unsubscribe(MqttAndroidClient client){
        Toast.makeText(context, "msg " +toast, Toast.LENGTH_SHORT).show();

        try {
            IMqttToken unsubtoken = client.unsubscribe(topic);
            unsubtoken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MqttunSubscribed", "Mqtt unSubscribed Success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MqttunSubscribed", "Mqtt unSubscribed Failure");
                }
            });
        }
        catch(MqttException e){
            e.printStackTrace();
        }
    }


}

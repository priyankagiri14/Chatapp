package com.example.chatroom.Users.Chat;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.example.chatroom.Chat;
import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.MainActivity;
import com.example.chatroom.Userslist;
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
import org.json.JSONObject;


public class MqttConnection {
    //public static String serverUri = "tcp://soldier.cloudmqtt.com:17303";
    //
    public static String serverUri = "tcp://13.233.89.219:1883";
    //public static String serverUri = "tcp://192.168.43.114:1883";
    public static String clientId ;
    public static String subscribetopic =Userslist.userid;
    public static int qos=2;

    public static String username = "mqroot";
    public static String password = "qwe123QWE!@#";
    public static Context context;
    public static MqttConnection singltonInstance;
    public MqttAndroidClient conn;
    public static String msg;
    JSONObject jsonObject;
    private String senderid,message1;
    static JSONObject jsonObj;
    static ChatDatabase chatDatabase;


    private MqttConnection() { }

    public static MqttConnection getInstance(){

        context= Userslist.context;

        if(singltonInstance==null){

            //Initialize singlton instance
            singltonInstance = new MqttConnection();


            //Make connection to MQTT server
            clientId = "chatuser-"+Userslist.userid;

            //Setup connection parameters
            singltonInstance.conn = new MqttAndroidClient(context, serverUri,clientId);
            singltonInstance.conn.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("connection", "Connection Lost: "+cause.getMessage());
                    getInstance();

                }

                /** This function will execute when a new message is arrived on mqtt server
                 * @param topic1 subscribe topic
                 * @param payload consist of mqtt message recived through mqtt server
                 * @throws MqttException it throws mqtt exception if found any
                 */
                @Override
                public void messageArrived(String topic1, MqttMessage payload) throws MqttException {


                   /* try {
                        jsonObj = new JSONObject(payload.toString());
                        JSONArray sys = jsonObj.getJSONArray("msgdata");
                        JSONObject obj = sys.getJSONObject(0);

                        String uid = obj.getString("senderid");
                        String message = obj.getString("message");


                        chatDatabase.insertData(Integer.valueOf(uid),Integer.valueOf(MainActivity.id),uid,message,payload.getId(),currenttime,1);
                        Log.d("connection", "Message arrived: "+ payload.toString());

                    }catch(Exception e){
                        Log.d("communication", e.getMessage() + payload.toString());
                    }*/
                }

                /** This function will execute when message is successfully deliver to the user
                 * @param token It consist of imqttdelivery token
                 */
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    try {
                        Log.d("connection", "Message delivered: " + token.getMessage()  );
                        //   subscribe();
                        //  Chat.chatView.addMessage(new ChatMessage(token.getMessage().toString(), System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
                    } catch (MqttException e) {
                        Log.d("connection", "Message delivered with exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            try {
                singltonInstance.conn.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(true);
                        disconnectedBufferOptions.setDeleteOldestMessages(true);
                        singltonInstance.conn.setBufferOpts(disconnectedBufferOptions);
                        if (singltonInstance.conn == null) {
                            Log.d("connection", "conn null" );
                            return;
                        } else {
                            singltonInstance.subscribe();
                            Log.d("Connection", "Connected successfully (client id: '"+clientId+"' )");
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("Connection", "Connection attempt failed: "+exception.getMessage());
                    }
                });
            } catch (MqttException e) {

                Log.d("connection", ("Connection attempt failed with exception: " + e.getMessage()));
                Log.d("connection", (singltonInstance.conn.isConnected()? "_connected": "_not_connected"));
                e.printStackTrace();
            }
        }

        return singltonInstance;
    }

    /**
     * It is subscribe function that will update the chat screen if user already subscribed to a particular topic
     * and recived the message from the same
     */
    public void subscribe() {

        try {
            conn.subscribe(subscribetopic, qos, new IMqttMessageListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    Log.d("connection","Subscribed message received: "+ topic +" | "+message.toString()+" | "+message.getId()+"topic"+subscribetopic +"\n" +chatDatabase.blocked);
                    Log.d("msgrcvd", "messageArrived: "+chatDatabase.blocked);
                    if(chatDatabase.blocked==1){
                        Log.d("is_blocked","subscribe_fail user is blcoked"+chatDatabase.blocked);
                    }
                    else {
                        Userslist.updateUserStream(message);
                    }
                    /*if(Chat.blocked.equals("true")){
                        Log.d("is_blocked","subscribe_fail user is blcoked"+Chat.blocked);
                    }
                    else if(Chat.blocked.equals("false")){
                        Userslist.updateUserStream(message);
                        Log.d("is_blocked","subscribe_success user unblocked"+Chat.blocked);
                    }*/

                   /* Log.d("connection","Subscribed message received: "+ topic +" | "+message.toString()+" | "+message.getId()+"topic"+subscribetopic);
                    if(Block_Unblockuser.blocked.equals("true")){
                        Log.d("is_blocked","subscribe_fail user is blcoked"+Chat.blocked);
                    }
                    else if(Block_Unblockuser.blocked.equals("false")){
                        if(subscribetopic.equals(Userslist.uid)) {
                            Userslist.updateUserStream(message);
                        }
                        Log.d("is_blocked","subscribe_success user unblocked"+Chat.blocked);
                    }*/
                }
            });
        }catch (MqttException e) {
            e.printStackTrace();
            Log.d("connection","Subscribed message received with exception: "+subscribetopic +" | "+e.getMessage());
        }
    }

}

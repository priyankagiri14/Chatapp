package com.example.chatroom;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.Users.Chat.Chat_View;
import com.example.chatroom.Users.Chat.MqttConnection;
import com.example.chatroom.Users.Profile.Profile_Activity;
import com.example.chatroom.Users.Profile.Profile_Setting;
import com.example.chatroom.Users.Userslistadapter;
import com.example.chatroom.Users.model.Refreshtoken;
import com.example.chatroom.Users.model.Streamresponse;
import com.example.chatroom.Users.model.User;
import com.example.chatroom.Users.model.Userslistmodel;
import com.example.chatroom.WebServices.RetrofitClient;
import com.example.chatroom.WebServices.Web_Interface;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunabaranurut.microdb.base.MicroDB;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import co.intentservice.chatui.models.ChatMessage;
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack;
import lolodev.permissionswrapper.wrapper.PermissionWrapper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.app.NotificationManager.IMPORTANCE_DEFAULT;


public class Userslist extends AppCompatActivity implements Callback<Userslistmodel>  {

    private static String message,name,filename;
    private static ArrayList<Object> lstt;
    public static String uid;
    private static String notificationmessage;
    TextView users;
    static ListView userslist;
    SearchView searchView;
    private static Userslistadapter userslistadapter;
    private static List<String> usersname;
    private static List<String> usersname1;
    private static List<String> userids1;
    private List<String> usersids;
    public static String selecteduserid;
    public static String currentuserid = "4";
    public static String selectedusername;
    public static String st;
    public static MqttConnection mc;
    public static Integer notificationcount=0;
    public static MqttAndroidClient conn;
    public static ChatDatabase chatDatabase;
    public static Context context;
    public static Activity activity;
    static JSONObject jsonObj;
    public static List<ChatMessage> msglist;
    public static Handler hanlder;
    private static final String CHANNEL_ID = "com.example.chatroom";
    private static final String MESSAGE ="Notification" ;
    public static Notification notification;
   static Calendar calendar=Calendar.getInstance();
    static Integer month=calendar.get(Calendar.MONTH);
    static Integer m=month+1;
   static String date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
    static String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
    static String currenttime=time;
    static MicroDB microDB;
    private static ObjectMapper objectMapper;
    private static WeakReference<Context> wmContext;
    private HashSet<String> reservedKeys;
    private String DB_NAME = "MicroDB";
    private static HashSet<String> keySet;
    private static String KEY_DEPOSIT_REFERENCE = "userlistt";
    static User user;
    static Userslist contextt;
    public static RelativeLayout relativeLayout;
    private static boolean isInBackground=true;
    SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public static String userid;
    private String username;
    public static String token;
    private String refreshtoken;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userslist);
        userslist = findViewById(R.id.userslist);
        userslist.setDivider(null);
        relativeLayout=findViewById(R.id.r1);
        searchView = findViewById(R.id.searchview);
        toolbar=findViewById(R.id.userlisttoolbar);
        setSupportActionBar(toolbar);
        new PermissionWrapper.Builder(this)
                .addPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
                //enable rationale message with a custom message
                .addPermissionRationale("Rationale message")
                //show settings dialog,in this case with default message base on requested permission/s
                .addPermissionsGoSettings(true)
                //enable callback to know what option was choosed
                .addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        Log.i(Userslist.class.getSimpleName(), "Permission was granted.");
                    }

                    @Override
                    public void onDenied(String permission) {
                        Log.i(Userslist.class.getSimpleName(), "Permission was not granted.");
                    }
                }).build().request();
        Log.d("refresh","done");
        preferences=getApplicationContext().getSharedPreferences("tokenpref", 0);
        editor=preferences.edit();
        fetchuser();
        //makeconnection();

        st=getClass().getName();
        context=getApplicationContext();
        contextt=Userslist.this;
        activity=this;
        lstt=new ArrayList<>();
        user=new User();
        //microdb
        microDB=new MicroDB(context);
        wmContext = new WeakReference<>(context);
        objectMapper = new ObjectMapper();
        reservedKeys = new HashSet<>();
        reservedKeys.addAll(Arrays.asList(KEY_DEPOSIT_REFERENCE,DB_NAME));
        isAppIsInBackground(context);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(userslistadapter==null){

                return false;
                }
                else {
                    userslistadapter.getFilter().filter(newText);
                    userslistadapter.notifyDataSetChanged();
                    return true;
                }

            }
        });
       // addBadge(this,5);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent i = new Intent(this, Profile_Activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

               return true;
               default:
                   return true;
        }
    }

    private void addBadge(Context context, int count) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", this.getClass().getName());
        context.sendBroadcast(intent);
    }

    private void makeconnection() {
            mc = MainActivity.mc;
            conn =mc.conn;
            Log.d("publishimagefunc", "connection lost "+conn);
            Handler handler=new Handler();
            if(!conn.isConnected()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeconnection();
                    }
                }, 3000);
                Log.d("connected check", "makeconnection: " + conn.isConnected());
            }
            return;

    }


    /**
     * @param context Application context
     * @return return true or false accordingly if the app is in backround or not
     */
    private static boolean isAppIsInBackground(Context context) {
        isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
         }
        Log.d("background", "isAppIsInBackground: "+isInBackground);

        return isInBackground;
    }


    /**
     *  This function will executed when a new message is received by the user and
     *  then it will notified the user by updating the stream
     * @param payload MqttMessgae in form of json sent by the sender
     *
     */
    public static void updateUserStream(MqttMessage payload){
        isAppIsInBackground(context);
        try {
            jsonObj = new JSONObject(payload.toString());
            JSONArray sys = jsonObj.getJSONArray("msgdata");
            JSONObject obj = sys.getJSONObject(0);
            uid = obj.getString("senderid");
            String msgtype=obj.getString("msgtype");
            notificationcount++;
            message = obj.getString("message");
            filename=obj.getString("file_name");
            name=obj.getString("sendername");
            date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
            time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
            currenttime=date+":"+time;
            chatDatabase.insertData(Integer.valueOf(uid),Integer.valueOf(userid),uid,message,payload.getId(),msgtype,filename, String.valueOf(System.currentTimeMillis()),time,1,1);
            Log.d("st", "onCreate: "+msgtype);
            notificationmessage=message;
            if(!msgtype.equals("text")){
                notificationmessage=msgtype;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(selecteduserid!=uid)
                shownotification();
            }
            isAppIsInBackground(context);
            if(isInBackground==true){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(selecteduserid!=uid)
                    shownotification();
                }
                st="";
            }

              if(st.equals("com.example.chatroom.Userslist") && isInBackground==false) {
                selecteduserid = uid;
                selectedusername = name;
                lstt.add(message);
                usersname1=new ArrayList<>();
                userids1=new ArrayList<>();
                chatDatabase=new ChatDatabase(context);
                chatDatabase.msgcount =new ArrayList<>();
                usersname1=user.getUserlist();
                userids1=user.getUserids();
                 for (int i = 0; i <userids1.size(); i++) {
                     chatDatabase.messagecount(userids1.get(i));
                 }
                Intent i = new Intent(context, Userslist.class);
                activity.getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.overridePendingTransition(0, 0);
                activity.startActivity(i);
                //context.getSharedPreferences("userlist", Context.MODE_PRIVATE);


            }
            else if(!st.equals("com.example.chatroom.Userslist") || st.equals("") ) {
                if (Chat_View.lastMessageId != payload.getId() && uid.equals(selecteduserid) && isInBackground==false) {
                    Chat_View.lastMessageId = payload.getId();
                    Log.d("connection", "update stream" + message);
                    Log.d("connection", "userlist class" + st);
                    Chat_View.onTrigger(uid);
                }
            }
           /* else if(isInBackground==true){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    shownotification();
                }
            }*/

        }catch(Exception e){
            Log.d("communication", e.getMessage()+"MQTT Message contains invalid JSON");
        }
    }


    /**
     * Every time user recieves a new message this function will be executed
     * and notified the user by showing a notification
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void shownotification() {
     NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, Chat_View.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            Notification.Builder builder = new Notification.Builder(context);
            notification = builder.setContentTitle("new message")
                    .setSmallIcon(R.drawable.chat_icon)
                    .setNumber(4)
                    .setContentText(name +": "+ notificationmessage)
                    .setTicker("New Message Alert!"
                    )
                    .setSubText("at: "+currenttime)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent).build();
                 builder.setChannelId(CHANNEL_ID);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);

        }
        else {
            Notification.Builder builder = new Notification.Builder(context);
            notification = builder.setContentTitle("Ontrack App Notification")
                    .setSmallIcon(R.drawable.chat_icon)
                    .setNumber(4)
                    .setContentText(message)
                    .setTicker("New Message Alert!")
                    .setSubText("at: "+System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent).build();
            builder.setChannelId(CHANNEL_ID);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notification);
    }


    /**
     * This function shows the list of users available for the chat it execute a fetch user api of node js that
     * list all the users who are registered on the app.
     */
    public void fetchuser() {
        token=preferences.getString("token",null);
        refreshtoken=preferences.getString("refreshtoken",null);
        userid=preferences.getString("userid",null);
        username=preferences.getString("username",null);
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        try {
            Call<Userslistmodel> call = webInterface.userslist(userid,token);
            call.enqueue(this);
            Log.d("fetchuser", "fetchuser: "+token);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("catcherror", e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        st=getClass().getName();
        Log.d("UserList", "onResume: called");
        chatDatabase=new ChatDatabase(this);
        fetchuser();
    }

    @Override
    public void onResponse(Call<Userslistmodel> call, Response<Userslistmodel> response) {
        if (response.isSuccessful() && response.code() == 200) {
            usersname = new ArrayList<>();
            usersids = new ArrayList<>();
            chatDatabase.msgcount=new ArrayList<>();
            for (int i = 0; i < response.body().getData().size(); i++) {
                usersids.add(response.body().getData().get(i).getId().toString());
                usersname.add(response.body().getData().get(i).getName());
                chatDatabase.messagecount(response.body().getData().get(i).getId().toString());
            }
            user.setUserlist(usersname);
            user.setUserids(usersids);
            Log.d("responselist", chatDatabase.msgcount.toString());
            userslistadapter = new Userslistadapter(Userslist.this, chatDatabase.msgcount, usersname);
            userslist.setAdapter(userslistadapter);
            userslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    selecteduserid = usersids.get(position);
                    selectedusername = usersname.get(position);
                   // Toast.makeText(Userslist.this, "You Clicked at user " + usersids.get(position), Toast.LENGTH_SHORT).show();
                    createstream(usersids.get(position));
                    createstreamlocally(usersids.get(position));
                    Intent i=new Intent(Userslist.this, Chat_View.class);
                    startActivity(i);


                }
            });

        }

        else{
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                Toast.makeText(this,jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                recreatetoken(refreshtoken);
            }
            catch (Exception e) {
                e.printStackTrace();
                //Log.d("catchmsg",e.getMessage() +" "+response.body());
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }

        }


    }

    private void createstreamlocally(String otheruserid) {
        final String stream_users = userid + "," + otheruserid;
        chatDatabase.checkstream(userid, otheruserid);
        Log.d("chkstrm", "createstreamlocally: " + chatDatabase.checkstream(userid, otheruserid));
        if (chatDatabase.checkstream(userid, otheruserid) == 0) {
            chatDatabase.updatestream("", stream_users, userid, Integer.valueOf(otheruserid), 0);
            Log.d("createstream", "createstreamlocally: "+"streamcreated");
        }
        else{
            Log.d("createstream", "createstreamlocally: "+"streamalreadyexist");
        }
    }
    /**
     * Recreate token using refresh token
     * @param refreshtoken refreshtoken created at the time of login and saved in sharedprefrences
     */
    private void recreatetoken(String refreshtoken) {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        try {
            Call<Refreshtoken> call = webInterface.refreshtoken(refreshtoken);
            call.enqueue(new Callback<Refreshtoken>() {
                @Override
                public void onResponse(Call<Refreshtoken> call, Response<Refreshtoken> response) {
                    if(response.isSuccessful() && response.code()==200){
                        String tk=response.body().getData().getToken();
                        String reftk=response.body().getData().getRefreshtoken();
                        editor.putString("token",tk);
                        editor.putString("refreshtoken",reftk);
                        editor.commit();
                        fetchuser();
                    }
                }

                @Override
                public void onFailure(Call<Refreshtoken> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("catcherror", e.getMessage());
        }
    }


    @Override
    public void onFailure(Call<Userslistmodel> call, Throwable t) {
        Log.d("Failure",t.getMessage());
    }


    /**  Create Stream if not already available
     * @param usersids Userid of the user selected from userlist with whom user want to chat
     */
    private void createstream(final String usersids) {

        //
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("users", userid + "," + usersids);
            paramObject.put("stream_userid", userid);
            paramObject.put("block_userid", usersids);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf((paramObject)));
            Log.d("thisparam", paramObject.toString());
            Call<Streamresponse> call1 = webInterface.stream(body);
            call1.enqueue(new Callback<Streamresponse>() {
                @Override
                public void onResponse(Call<Streamresponse> call, Response<Streamresponse> response) {
                    Log.d("stream response", "onResponse: "+response.body());
                  if(response.isSuccessful()&& response.code()==200){
                     // Toast.makeText(Userslist.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                      Log.d("createstreamapi if", "onResponse: "+response.body());
                  }
                  else if(response.isSuccessful()) {
                   //   Toast.makeText(Userslist.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                      Log.d("createstreamapi else", "onResponse: "+response.message());
                  }
                }

                @Override
                public void onFailure(Call<Streamresponse> call, Throwable t) {
                    Log.d("createstreamapi", "onResponse: "+t.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


}

interface UpdateUI{
    void onUpdate();

}
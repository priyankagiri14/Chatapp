package com.example.chatroom;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.Users.Chat.MqttConnection;
import com.example.chatroom.WebServices.RetrofitClient;
import com.example.chatroom.WebServices.Web_Interface;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import info.androidhive.fontawesome.FontTextView;
import io.codetail.animation.ViewAnimationUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Chat extends AppCompatActivity implements Callback<ResponseBody>, View.OnClickListener {

    TextView typingstatus,personname;
    ImageView dp_profile;
    ImageButton imageButton;
    FontTextView camera,gallery;
//    Date currenttime;
    Calendar calendar=Calendar.getInstance();
    int hour;
    public static String date,time,currenttime;
    MqttAndroidClient client;
    private MqttConnection mqttConnection;
    public byte[] encodedPayload;
    public Handler handler = null;
    public static Runnable runnable = null;
    public static String publishmessage = "";
    MyBroadcast myBroadcast;
    public static MqttMessage mqttMessage;
    public String publishtopic ;
    public String hi="hello";
    public static Context context;
    private boolean isBound=false;
    public static ChatView chatView;
    public MqttConnection mc;
    public MqttAndroidClient conn;
    public static int lastMessageId;
    String senderid = "";
    public  static String selecteduserid,selectedusername;
    private JSONObject jsonObject;
    private String message,name;
    static JSONObject jsonObj;
    public static ChatDatabase chatDatabase;
    public static Userslist userslist;
    public static RelativeLayout rl;
    public static Activity activity;
    public static String st;
    Integer month=calendar.get(Calendar.MONTH);
    Integer m=month+1;
    public static String blocked;
    private String is_blocked;
    private boolean block;
    private LinearLayout linearChat;
    ProgressDialog progressDialog;
    public static RelativeLayout mRevealView;
    public static LinearLayout attachmentbg;
    private boolean hidden=true;
    public Animator animator;
    private int cx,cy,radius;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private final static int FILE_REQUEST_CODE = 1;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rl = findViewById(R.id.rl);
        activity = this;
        getlastseen();
        findviews();


        //initialize
        progressDialog=new ProgressDialog(this);
        chatDatabase = new ChatDatabase(this);
        userslist = new Userslist();
        selecteduserid = Userslist.selecteduserid;
        selectedusername = Userslist.selectedusername;
        personname.setText(selectedusername);
        context = getApplicationContext();
        publishtopic = selecteduserid;


        //date time
        date = calendar.get(Calendar.DATE) + "-" + m + "-" + calendar.get(Calendar.YEAR);
        time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        currenttime = date + " " + hour + ":" + time;

        //getmessage from database and set it on screen
         chatDatabase.getmessage(selecteduserid);
        //make connection
        mc = MqttConnection.getInstance();
        conn = mc.conn;
        st = getClass().getName();
        userslist.st = "";
        //checkblockeduser
        checkblockeduser(selecteduserid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputedittextlistner();
    }


    private void inputedittextlistner() {
        chatView.getInputEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>10){
                    chatView.getInputEditText().removeTextChangedListener(this);
                    //chatView.getInputEditText().setText(chatView.getInputEditText().getText().append("\n"));
                    //chatView.getInputEditText().append("\n");
                    chatView.getInputEditText().setText(chatView.getInputEditText().getText());
                    String cat="Indians";
                    chatView.getInputEditText().getText().append(cat,0,7);

                    chatView.getInputEditText().addTextChangedListener(this);
                }
            }
        });
    }

    private void findviews() {
        typingstatus = findViewById(R.id.typingstatus);
        linearChat=(findViewById(R.id.linearChat));
        personname = findViewById(R.id.person_name);
        imageButton=findViewById(R.id.imgUpload);
        imageButton.setOnClickListener(this);
        mRevealView =findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);
        attachmentbg=findViewById(R.id.atchmnts);
        chatView = (ChatView) findViewById(R.id.chat_view);
        gallery=findViewById(R.id.gallery);
        gallery.setOnClickListener(this);
        dp_profile=findViewById(R.id.dp_profile);
    }

    private void publishtopicfun() {
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(final ChatMessage chatMessage) {
                if (conn.isConnected()) {
                    encodedPayload = new byte[0];
                    JSONObject msgData = new JSONObject();
                    try {
                        msgData.put("senderid", MainActivity.id);
                        msgData.put("sendername", MainActivity.name);
                        msgData.put("msgtype", "1");
                        msgData.put("message", chatMessage.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(msgData);
                    JSONObject msgobject = new JSONObject();
                    try {
                        msgobject.put("msgdata", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("jsonmsg", msgobject.toString());
                    publishmessage = msgobject.toString();
                    //publishmessage = chatMessage.getMessage();
                    try {
                        encodedPayload = publishmessage.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mqttMessage = new MqttMessage(encodedPayload);
                    //mqttMessage.setId(123);
                    mqttMessage.setRetained(false);
                    mqttMessage.setQos(2);


                    try {
                        jsonObject = new JSONObject(publishmessage);
                        JSONArray sys = jsonObject.getJSONArray("msgdata");
                        JSONObject obj = sys.getJSONObject(0);
                        senderid = obj.getString("senderid");
                        message = obj.getString("message");
                        name = obj.getString("sendername");


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("json exception", "exception" + "|" + e.getMessage());
                    }


                    try {
                        chatDatabase.insertData(Integer.valueOf(MainActivity.id), Integer.valueOf(selecteduserid), selecteduserid, message, mqttMessage.getId(),"text", null,
                                String.valueOf(System.currentTimeMillis()),"i2t3g", 0,0);
                        conn.publish(publishtopic, encodedPayload, 2, false, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.d("messagepublish", "success" + message + "topic " + publishtopic + "|" + senderid);
                                chatView.addMessage(new ChatMessage("" + message, System.currentTimeMillis(), ChatMessage.Type.SENT));
                                chatView.setClickable(false);
                                Log.d("clikcable", "onCreate: false");
                                chatView.getInputEditText().setText("");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.d("messagepublish", "failure" + exception);
                                Toast.makeText(Chat.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Log.d("messagepublish", e.getLocalizedMessage());
                    }
                }
                return false;
            }


        });
    }

    private void checkblockeduser(String selecteduserid) {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        try {
            Call<ResponseBody> call = webInterface.statusofblock(selecteduserid);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful() && response.code()==200){
                        try {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                         is_blocked = jsonObject.getString("message");
                         invalidateOptionsMenu();
                         Log.d("is_blocked", "onResponse: "+is_blocked);
                        if(is_blocked.equals("User is blocked")){
                            typingstatus.setText("");
                            blocked="true";
                            chatView.getInputEditText().setText("You can't reply to this conversation ");
                            chatView.getInputEditText().setVisibility(View.GONE);
                            chatView.getActionsMenu().setVisibility(View.GONE);
                            chatView.getInputEditText().setBackgroundColor(getResources().getColor(R.color.white));

                        }
                        else if(is_blocked.equals("User unblocked")){
                            getlastseen();
                            blocked="false";
                            publishtopicfun();
                            chatView.getInputEditText().setText("");
                            chatView.getInputEditText().setVisibility(View.VISIBLE);
                            chatView.getActionsMenu().setVisibility(View.VISIBLE);
                            chatView.getInputEditText().setBackgroundColor(getResources().getColor(R.color.inputetxt));

                        }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("catcherror", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        Log.d("is_blocked", "onCreateOptionsMenu: "+is_blocked+"\n"+blocked);

        if(is_blocked!=null) {
            if (blocked.equals("true")) {
                MenuItem item1 = menu.findItem(R.id.block);
                item1.setTitle("Unblock");
            }
            else {
                if(blocked.equals("false")) {
                    MenuItem item=menu.findItem(R.id.block);
                    item.setTitle("Block");
                }
            }
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
          case R.id.block:
              if(item.getTitle().equals("Block")) {
                 final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
                 alertdialog.setMessage("Are you sure you want to block this user?")
                         .setCancelable(false)
                         .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 progressDialog.setTitle("Please Wait");
                                 progressDialog.setMessage("Blocking user..");
                                 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                 progressDialog.setCancelable(false);
                                 progressDialog.show();
                                 item.setTitle("Unblock");
                                 blockuser(MainActivity.id+","+selecteduserid);

                             }
                         })
                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.cancel();
                             }
                         })
                         .setTitle("Block the user!");
                 alertdialog.show();

             }

             //Unblock
             else if(item.getTitle().equals("Unblock")){
                 final AlertDialog.Builder alertdialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AppTheme1));
                 alertdialog.setMessage("Are you sure you want to unblock this user?")
                         .setCancelable(false)
                         .setPositiveButton("Unblock", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 progressDialog.setTitle("Please Wait");
                                 progressDialog.setMessage("Unblocking user..");
                                 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                 progressDialog.setCancelable(false);
                                 progressDialog.show();
                                 item.setTitle("Block");
                                 blockuser(MainActivity.id+","+selecteduserid);

                             }
                         })
                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.cancel();
                             }
                         })
                         .setTitle("Unblock the user!");
                 alertdialog.show();

             }


            return true;

          default:
            return true;
       }

    }

    private void blockuser(String stream) {
        Web_Interface web_interface=RetrofitClient.getClient().create(Web_Interface.class);
        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("users",stream);
            RequestBody body=RequestBody.create(MediaType.parse("text/plain"),String.valueOf(jsonObject));
            Call<ResponseBody> call=web_interface.block(body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()&& response.code()==200){
                        try {
                            JSONObject jsonObject=new JSONObject(response.body().string());
                            String userblockstatus = jsonObject.getString("message");
                            Log.d("userblocked", "onResponse: "+userblockstatus);
                            if(userblockstatus.equals("User unblocked")){

                                Handler h=new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        block=false;
                                        progressDialog.dismiss();
                                        checkblockeduser(selecteduserid);
                                    }
                                },1000);

                                Log.d("userblocked", "onResponse: "+block);
                            }
                            else if(userblockstatus.equals("User blocked")){
                                Handler h=new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        block=true;
                                        progressDialog.dismiss();
                                        checkblockeduser(selecteduserid);
                                    }
                                },1000);
                              // progressDialog.dismiss();

                                Log.d("userblocked", "onResponse: "+block);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updatelastseen(String typing) {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("last_seen",typing);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf((paramObject)));
            Call<ResponseBody> call = webInterface.updatelastseen(MainActivity.id,body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful() && response.code()==200){
                        try {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        String last_seen = jsonObject.getString("last_seen");
                            Log.d("response","lastseen"+last_seen);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("lastseen update failure",t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("catcherror", e.getMessage());
        }
    }


    private void getlastseen() {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        //String id= MainActivity.id;
        try {

            Call<ResponseBody> call = webInterface.lastseen(Userslist.selecteduserid);
            call.enqueue(this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("catcherror", e.getMessage());
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if(response.isSuccessful() && response.code()==200){
           try{
               JSONObject jsonObject=new JSONObject(response.body().string());
               String last_seen=jsonObject.getString("last_seen");
               Log.d("response","lastseen"+last_seen);
               if(last_seen.equals("null")){
                   typingstatus.setText("");
               }
               else if(!last_seen.equals("null")) {
                   typingstatus.setText(last_seen);
               }

           } catch (JSONException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }

        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.d("Failure",t.getMessage());
    }



    public static void onTrigger(String message) throws ClassNotFoundException {
                // chatView.addMessage(new ChatMessage(message,System.currentTimeMillis(),ChatMessage.Type.RECEIVED));
                Log.d("connection", "onTrigger: "+st);

                Intent intent = new Intent(context, Chat.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                activity.finish();

        }



    public void onPause() {
        super.onPause();
        Log.d("onpause", "onPause called: ");
        getlastseen();
        //typingstatus.setText( currenttime);

    }
    public void onResume() {
        super.onResume();
        getlastseen();
        updatelastseen(currenttime);
        checkblockeduser(selecteduserid);
        //chatDatabase.getmessage(selecteduserid);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(Chat.this, Userslist.class));
        //moveTaskToBack(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStop() {

        super.onStop();
        String date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        String currenttime=date+" "+hour+":"+time;
        updatelastseen(currenttime);
        Log.d("onstop","onstop call " +currenttime);

    }
    public void onDestroy() {

        super.onDestroy();
        String date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        String currenttime=date+" "+hour+":"+time;
        updatelastseen(currenttime);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgUpload){
          /*   cx = (mRevealView.getLeft() + mRevealView.getRight());
             cy = (mRevealView.getTop() + mRevealView.getBottom())/2;*/
            cx = (int) (attachmentbg.getX() + (attachmentbg.getWidth() / 2));
            cy = (int) (attachmentbg.getY() + (attachmentbg.getHeight() / 2));
             /*radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());*/
            radius= (int) Math.hypot(mRevealView.getWidth(),mRevealView.getHeight());
            animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(500);
            hideattachments();

            //Animator animator_reverse = animator.reverse();
        }

        if(v.getId()==R.id.gallery){
            Intent intent = new Intent(context, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSelectedMediaFiles(mediaFiles)
                    .enableImageCapture(true)
                    .setShowVideos(false)
                    .setSkipZeroSizeFiles(true)
                    .setMaxSelection(10)
                    .build());
            startActivityForResult(intent,FILE_REQUEST_CODE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            mediaFiles.clear();
            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
            for(int i=0;i<mediaFiles.size();i++){
                Log.d("mediafilesclicked", "path: "+mediaFiles.get(i).getPath());
                Log.d("mediafilesclicked", "mimetype: "+mediaFiles.get(i).getMimeType());
                Log.d("mediafilesclicked", "size: "+mediaFiles.get(i).getSize());
                Log.d("mediafilesclicked", "tumbnail"+mediaFiles.get(0).getBucketName());
                File file=new File(mediaFiles.get(0).getPath());
                Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                Log.d("mediafilesclicked", "bitmap"+bitmap);
                Glide.with(context)
                        .load(mediaFiles.get(0).getPath())
                        .into(dp_profile);

            }
        }
    }







    private void hideattachments() {
        if (hidden) {
            mRevealView.setVisibility(View.VISIBLE);
            animator.start();
            hidden = false;

        } else if(!hidden) {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius,0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRevealView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(500);
            anim.start();
            hidden=true;
        }
    }

}

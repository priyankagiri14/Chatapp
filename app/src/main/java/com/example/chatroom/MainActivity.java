package com.example.chatroom;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.example.chatroom.Users.Chat.MqttConnection;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroom.Users.Loginresponse;
import com.example.chatroom.WebServices.RetrofitClient;
import com.example.chatroom.WebServices.Web_Interface;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Callback<Loginresponse> {

    private TextInputEditText username,password;
    Button login;
    TextView signup;
    private ProgressDialog progressDialog;
    private MqttClient_file pahoMqttClient;
    private Object mqttAndroidClient;
    private String Clientid=MqttClient_file.clientId;
    private String Base_url=MqttClient_file.BROKER_URL;
    private MqttAndroidClient mqttAndroidClient1;
    public static String id;
    public static String name,token,refreshtoken;
     public static Context context;
    Calendar calendar=Calendar.getInstance();
    File dir;
    public static MqttConnection mc;
    public static MqttAndroidClient conn;
    public static SharedPreferences pref;
    public SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=findViewById(R.id.editusername);
        password=findViewById(R.id.editpswrd);
        signup=findViewById(R.id.signupheretext);
        signup.setOnClickListener(this);
        login=findViewById(R.id.loginbtn);
        login.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        context=getApplicationContext();
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        // dir = context.getCacheDir();

        pref = getApplicationContext().getSharedPreferences("tokenpref", 0); // 0 - for private mode
        editor = pref.edit();



    }


    /**
     * @param v  Views are differentiated by their id
     *           this function handle onclick event on buttons and text
     */
    @Override
    public void onClick(View v) {
       // deleteDir(dir);
        progressDialog.show();
      if(v.getId()==R.id.loginbtn) {
          if (username.length() == 0) {
              Toast.makeText(this, "Username required", Toast.LENGTH_SHORT).show();
              progressDialog.dismiss();
          } else if (password.length() == 0) {
              Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show();
              progressDialog.dismiss();
          } else {
              login(username.getText().toString(), password.getText().toString());


          }
          /*Intent i=new Intent(this,Chat.class);
          startActivity(i);
          finish();*/
      }
      else if(v.getId()==R.id.signupheretext){
          Intent i=new Intent(this,Signup_Activity.class);
          startActivity(i);
          finish();
      }
    }

    /**
     * @param email Registered email id needs to be enter here
     * @param password Password correspond to the enter email id needs to be enter here
     *       After clicking on the login button this function is executed which uses login api and show result accordingly
     */
    private void login(String email, String password) {
        Web_Interface webInterface= RetrofitClient.getClient().create(Web_Interface.class);
        try {
        JSONObject paramObject = new JSONObject();
        paramObject.put("email", email);
        paramObject.put("password", password);
        paramObject.put("last_seen","Online");
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf((paramObject)));
        Log.d("thisparam",paramObject.toString());
        Call<Loginresponse> call=webInterface.login(body);
        call.enqueue(this);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @param call The resposne from the server by using the api is
     * @param response It is resposnsible to get the response form the api
     */
    @Override
    public void onResponse(Call<Loginresponse> call, Response<Loginresponse> response) {
        if(response.isSuccessful() && response.code()==200){
            Toast.makeText(this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

            username.setText(""); password.setText("");
            Log.d("loginbody",response.body().getData().getId());
            id=response.body().getData().getId();
            name=response.body().getData().getName();
            token=response.body().getData().getToken();
            refreshtoken=response.body().getData().getRefreshtoken();
            editor.putString("userid",id);
            editor.putString("username",name);
            editor.putString("token",token);
            editor.putString("refreshtoken",refreshtoken);
            editor.commit();
            pahoMqttClient = new MqttClient_file();
            progressDialog.dismiss();
          /*  mc=MqttConnection.getInstance();
            conn=mc.conn;
            if(conn.isConnected()){
                Log.d("connected true", "onResponse: "+conn.isConnected());
            }
            else*/
//                Log.d("connected false", "onResponse: "+conn.isConnected());
            Intent i=new Intent(this,Userslist.class);
            startActivity(i);
            finish();
        }
        else{
            try {
               JSONObject jObjError = new JSONObject(response.errorBody().string());
                Toast.makeText(this,jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            catch (Exception e) {
               e.printStackTrace();
                //Log.d("catchmsg",e.getMessage() +" "+response.body());
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }
    }

    /**
     * @param t if the api is not giving the right response and throws and excpetion that exception can be caught here
     */
    @Override
    public void onFailure(Call<Loginresponse> call, Throwable t) {
       // Log.d("failuremsg",t.getMessage());
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }
}
package com.example.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroom.Users.Signupresponse;
import com.example.chatroom.WebServices.RetrofitClient;
import com.example.chatroom.WebServices.Web_Interface;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup_Activity extends AppCompatActivity implements View.OnClickListener, Callback<Signupresponse> {

    TextView logintext;
    Button signup;
    private TextInputEditText name,email,password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_);
        logintext=findViewById(R.id.loginheretext);
        signup=findViewById(R.id.signupbtn);
        name=findViewById(R.id.editname);
        email=findViewById(R.id.editemail);
        password=findViewById(R.id.editpassword);
        signup.setOnClickListener(this);
        logintext.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    /** It will handle on click listener for different button , textview etc.
     * @param v Each button , textview, is differentiated by their resource id.
     */
    @Override
    public void onClick(View v) {
        progressDialog.show();
      if(v.getId()==R.id.signupbtn) {
          if (name.length() == 0) {
              Toast.makeText(this, "Name field required", Toast.LENGTH_SHORT).show();
              progressDialog.dismiss();
          } else if (email.length() == 0) {
              Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show();
              progressDialog.dismiss();
          } else if (password.length() == 0) {
              Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show();
              progressDialog.dismiss();
          } else {
              signup(name.getText().toString(), email.getText().toString(), password.getText().toString());
          }
      }
      else if(v.getId()==R.id.loginheretext){
          Intent i=new Intent(this,MainActivity.class);
          startActivity(i);
          finish();
      }
    }

    /** This function is responsible for signup process this function also uses a signup nodejs api.
     *  Every time user click on signup button the signup api will exexcuted on the server and give response accordingly.
     * @param name It include username
     * @param email it include user email id
     * @param password it include user password
     */
    private void signup(String name, String email, String password) {
        Web_Interface webInterface= RetrofitClient.getClient().create(Web_Interface.class);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("name", name);
            paramObject.put("email", email);
            paramObject.put("password", password);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf((paramObject)));
           // Log.d("thisparam",paramObject.toString());
            Call<Signupresponse> call=webInterface.registration(body);
            call.enqueue(this);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    /** This function will execute every time signup is executed on the server and
     * it is responsible to give the response accordingly
     * @param call Call the api use the response from the server and respond accordingly to the user
     * @param response it will include the response from the api
     */
    @Override
    public void onResponse(Call<Signupresponse> call, Response<Signupresponse> response) {
        if(response.isSuccessful() && response.code()==200){
            Toast.makeText(this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            name.setText("") ; email.setText(""); password.setText("");

        }
        else{
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                //Log.d("RegistrationActivity",jObjError.getString("message"));
                Toast.makeText(this,jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            catch (Exception e) {
                e.printStackTrace();
               // Log.d("catchmsg",e.getMessage() +" "+response.body());
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }
    }

    @Override
    public void onFailure(Call<Signupresponse> call, Throwable t) {
       // Log.d("failuremsg",t.getMessage());
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }
}

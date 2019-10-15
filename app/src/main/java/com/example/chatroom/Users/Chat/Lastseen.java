package com.example.chatroom.Users.Chat;

import android.util.Log;
import android.view.View;

import com.example.chatroom.MainActivity;
import com.example.chatroom.Userslist;
import com.example.chatroom.WebServices.RetrofitClient;
import com.example.chatroom.WebServices.Web_Interface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lastseen implements Callback<ResponseBody> {


    public Lastseen() {
    }


    /**
     * This function is use to fetch the last seen of a particular user
     */
    public void getlastseen() {
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
                    Chat_View.lastseen.setText("");
                }
                else if(!last_seen.equals("null")) {
                    Chat_View.lastseen.setText(last_seen);
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


    /** This function is use to update the last seen of the user
     * @param typing current status either i.e. typing, online, or last seen
     */
    public void updatelastseen(String typing) {
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
}

package com.example.chatroom.Users.Chat;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.R;
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

public class Block_Unblockuser {


    private boolean block;
    public static ChatDatabase chatDatabase;

    public Block_Unblockuser() {
    }
    public String is_blocked;
    public static String blocked;
    private Lastseen lastseenclass=new Lastseen();


    /**
     * This function check if the user is blocked or not
     * @param selecteduserid selected user id
     */
    public void checkblockeduser(String selecteduserid) {
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
                            if(is_blocked.equals("User is blocked")){
                                Chat_View.lastseen.setText("");
                                blocked="true";
                                Chat_View.refreshmenu();
                                Chat_View.editText.setHint("You can't reply to this conversation");
                                Chat_View.editText.setEnabled(false);
                                Chat_View.send.setVisibility(View.GONE);
                            }
                            else if(is_blocked.equals("User unblocked")){
                                lastseenclass.getlastseen();
                                blocked="false";
                                Chat_View.refreshmenu();
                                Chat_View.editText.setHint("Start typing");
                                Chat_View.editText.setEnabled(true);
                                Chat_View.send.setVisibility(View.VISIBLE);

                            }
                            Log.d("is_blocked", "onResponse: "+is_blocked +blocked);

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

    /** This function block the user
     * @param stream selected user id
     */
    public void blockuser(String stream) {
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

                                //chatDatabase.blockuserlocally(Integer.valueOf(Userslist.selecteduserid));
                                Handler h=new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        block=false;
                                        Chat_View.progressDialog.dismiss();
                                        checkblockeduser(Userslist.selecteduserid);
                                    }
                                },1000);

                                Log.d("userblocked", "onResponse: "+block);
                            }
                            else if(userblockstatus.equals("User blocked")){
                                //chatDatabase.unblockuserlocally(Integer.valueOf(Userslist.selecteduserid));
                                Handler h=new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        block=true;
                                        Chat_View.progressDialog.dismiss();
                                        checkblockeduser(Userslist.selecteduserid);
                                    }
                                },1000);


                                Log.d("userunblocked", "onResponse: "+block);
                            }
                        } catch (JSONException e) {
                            Chat_View.progressDialog.dismiss();
                            e.printStackTrace();
                        } catch (IOException e) {
                            Chat_View.progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Chat_View.progressDialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

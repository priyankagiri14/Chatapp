package com.example.chatroom.Users.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.chatroom.MainActivity;
import com.example.chatroom.R;
import com.example.chatroom.Users.Chat.Chat_View;
import com.example.chatroom.Users.model.Fileupload;
import com.example.chatroom.Users.model.Updateprofilepic;
import com.example.chatroom.Userslist;
import com.example.chatroom.WebServices.RetrofitClient;
import com.example.chatroom.WebServices.Web_Interface;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class Profile_Setting extends AppCompatActivity implements View.OnClickListener {

    CircleImageView imageView;
    public ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    ProgressDialog progressDialog;
    int FILE_REQUEST_CODE=1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__setting);
        imageView=findViewById(R.id.chngprofile);
        imageView.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        context=getApplicationContext();
    }


    /**
     *  Handle differnt onclick function
     * @param v Each view is differentaiated by their resource id
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.chngprofile){
            Intent intent = new Intent(this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSelectedMediaFiles(mediaFiles)
                    .enableImageCapture(true)
                    .setShowVideos(false)
                    .setSkipZeroSizeFiles(true)
                    .setMaxSelection(1)
                    .build());
            startActivityForResult(intent,FILE_REQUEST_CODE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.setMessage("Updating profile pic");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
            String profilepicpath=mediaFiles.get(0).getPath();
            compressimage(profilepicpath);

        }
        else {
            progressDialog.dismiss();
        }
        mediaFiles.clear();
    }

    /**
     * Compress profile image before uploading
     * @param path url of original image
     */
    private void compressimage(String path) {
        Uri selectedImage =Uri.fromFile(new File(path));

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(
                    selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bmp = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        Log.d("ChatView123", "compressimage: "+byteArray+"\n"+"bmp:"+bmp);
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {

            e.printStackTrace();
        }
        uploadfile(byteArray,path);
    }

    /** To upload profile pic
     * @param filelist Compress image
     * @param path Non compressed and original image url
     */
    private void uploadfile(byte[] filelist,String path) {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        if(mediaFiles!=null) {
            Call<Fileupload> call;
            final List<MultipartBody.Part> list = new ArrayList<>();
            File file = new File(path);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), filelist);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", file.getName(), requestBody);
            list.add(body);
            call = webInterface.uploadfile(list, Userslist.token);
            Log.d("tokenn", "uploadfile: "+Userslist.token);
            call.enqueue(new Callback<Fileupload>() {
                @Override
                public void onResponse(Call<Fileupload> call, Response<Fileupload> response) {
                    if(response.isSuccessful()&&response.code()==200){
                        Log.d("upload profile pic", "filename "+response.body().getData().get(0).getFileName()+
                                "\n"+"fileurl "+ response.body().getData().get(0).getFileUrl()+
                                "\n"+"fileid "+ response.body().getData().get(0).getId());
                        String imgpath=response.body().getData().get(0).getFileUrl();
                        updateprofile(response.body().getData().get(0).getId());
                        loadImage(imgpath);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<Fileupload> call, Throwable t) {
                    Log.d("profileimagefailure", "onFailure: "+t.getMessage());
                    progressDialog.dismiss();
                }
            });
        }
    }

    /**
     * Update profile pic id in user table using this id the profile pic can be fetch later
     * @param id profile_pic id
     */
    private void updateprofile(Integer id) {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("profile_id", id);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject));
            Call<ResponseBody> call = webInterface.updateprofilepic(body, Userslist.token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        Toast.makeText(Profile_Setting.this, response.message(), Toast.LENGTH_SHORT).show();

                    }
                    Log.d("profile_id", "onResponse: "+response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("updateprofile picfail", "onFailure: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /** loadimage of profile pic using url
     * @param imgpath profile pic url
     */
        private void loadImage(String imgpath) {
            final String[] imgp = {null};
            Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
            Call<ResponseBody> call = webInterface.setprofilepic(Userslist.userid, Userslist.token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if(response.isSuccessful()&& response.code()==200){
                        Log.d("setprofilepic", "onResponse: "+response.message());

                    }
                   imgp[0] =response.message();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("setprofilepic", "onResponse: "+t.getMessage());
                }
            });
            Log.d("Profile_settings", "loadImage: " + imgp[0]);
        GlideUrl url = new GlideUrl(imgp[0], new LazyHeaders.Builder()
                .addHeader("x-access-code", Userslist.token)
                .build());
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_account_circle_grey_24dp)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }
}

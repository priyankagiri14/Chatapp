package com.example.chatroom.Users.Chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatroom.Chat;
import com.example.chatroom.R;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.File;
import java.util.ArrayList;

import info.androidhive.fontawesome.FontTextView;

public class Attachments extends AppCompatActivity implements View.OnClickListener {

    FontTextView gallery;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private final static int FILE_REQUEST_CODE = 1;


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.gallery){
            Intent intent = new Intent(Chat_View.context, FilePickerActivity.class);
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
                Glide.with(Chat_View.context)
                        .load(mediaFiles.get(0).getPath())
                        .into(Chat_View.dprofile);

            }
        }
    }

}

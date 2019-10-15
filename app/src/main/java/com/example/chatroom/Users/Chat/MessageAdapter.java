package com.example.chatroom.Users.Chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.MainActivity;
import com.example.chatroom.R;
import com.example.chatroom.Users.model.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;
import pub.devrel.easypermissions.EasyPermissions;

public class MessageAdapter extends BaseAdapter implements EasyPermissions.PermissionCallbacks {

    public static Context context;
    Context activity;
    List<Message> messages = new ArrayList<Message>();
    private static String savedImagePath;
    private int isclicked=0;
    private ByteArrayOutputStream stream;
    private static Message message;
    static String filenm;
    public static MediaPlayer mediaPlayerr;
    public static MessageViewHolder1 holder1;
    public static MessageViewHolder holder;
    String imagepath="/storage/emulated/0/Chatapp/Image/";
    String sentimagepath="/storage/emulated/0/Chatapp/SENT/Image/";
    String audiopath="/storage/emulated/0/Chatapp/Music/";
    String sentaudiopath="/storage/emulated/0/Chatapp/SENT/Music/";
    public static String errormsg;
    static String timestamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());


    public MessageAdapter(List<Message> messages,Context context) {
        this.messages=messages;
        this.context = context;
    }

    /*public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }*/

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
         holder = new MessageViewHolder();
         holder1 = new MessageViewHolder1();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        message = messages.get(i);
        filenm=message.getFilename();
        List<String> blnlist = ChatDatabase.bln;
        blnlist.get(i);


        if (blnlist.get(i).equals("true") && message.getMsgtype().equals("text")) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder1.messageBody = (TextView) convertView.findViewById(R.id.message_body1);
            holder1.time = convertView.findViewById(R.id.time);
            holder1.messageBody.setText(message.getText());
            holder1.time.setText(message.getTime());
            convertView.setTag(holder1);
            Log.d("isBelongsToCurrentUser", "getView: " + blnlist.get(i));
        } else if (blnlist.get(i).equals("true") && message.getMsgtype().equals("image")) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder1.linearLayout = (LinearLayout) convertView.findViewById(R.id.sentmsglinear);
            holder1.linearLayout.setVisibility(View.GONE);
            holder1.sentmsglinear = (LinearLayout) convertView.findViewById(R.id.linearimgsend);
            holder1.sentmsglinear.setVisibility(View.VISIBLE);
            holder1.sentimg = (ImageView) convertView.findViewById(R.id.sentimg);
           /* holder1.sentimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder1.sentimg.setBlur(1);
                }
            });*/
           String image=sentimagepath+message.getFilename();
            holder1.imagetime = (TextView) convertView.findViewById(R.id.sentimgtime);
            holder1.imagetime.setText(message.getTime());
            Glide.with(context)
                    .load(image)
                    .into(holder1.sentimg);
            convertView.setTag(holder1);
            Log.d("isBelongsToCurrentUser", "getView: " + message.getText());
        }
        else if (blnlist.get(i).equals("true") && message.getMsgtype().equals("file")) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder1.sentmsglinear = (LinearLayout) convertView.findViewById(R.id.sentmsglinear);
            holder1.sentmsglinear.setVisibility(View.GONE);
            holder1.sentfilelinear = (LinearLayout) convertView.findViewById(R.id.sentfilelinear);
            holder1.sentfilelinear.setVisibility(View.VISIBLE);
            holder1.sentfilename = (TextView) convertView.findViewById(R.id.filesentname);
            holder1.sentfilename.setText(message.getFilename());
            holder1.sentfiletime = (TextView) convertView.findViewById(R.id.filesenttime);
            holder1.sentfiletime.setText(message.getTime());

        }
        else if(blnlist.get(i).equals("true") && message.getMsgtype().equals("audio")){
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder1.sentmsglinear = (LinearLayout) convertView.findViewById(R.id.sentmsglinear);
            holder1.sentmsglinear.setVisibility(View.GONE);
            holder1.sentmusilinear=(LinearLayout)convertView.findViewById(R.id.musicsentlinear);
            holder1.sentmusilinear.setVisibility(View.VISIBLE);
            holder1.sentmusicname=(TextView) convertView.findViewById(R.id.musicsentname);
            holder1.sentmusicname.setText(message.getFilename());
            holder1.sentmusicduration=(TextView) convertView.findViewById(R.id.musicsentduration);
            holder1.sentmusictime=(TextView) convertView.findViewById(R.id.musicsenttime);
            holder1.sentmusictime.setText(message.getTime());
            holder1.sentmusicseekbar=(SeekBar) convertView.findViewById(R.id.musicsentbar);
            holder1.musicsentplay=(ImageView) convertView.findViewById(R.id.musicsentplay);
            holder1.musicsentpause=(ImageView) convertView.findViewById(R.id.musicsentpause);
            String music=sentaudiopath+message.getFilename();
            new Playmusic().execute(music);


        }

        //Receive MessageHolder
        else if (blnlist.get(i).equals("false") && message.getMsgtype().equals("text")) {
            convertView = messageInflater.inflate(R.layout.thier_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.rcv_message_body);
            holder.messageBody.setText(message.getText());
            holder.recvmsgtime = (TextView) convertView.findViewById(R.id.rcvmsg_time);
            holder.recvmsgtime.setText(message.getTime());
            convertView.setTag(holder);
            Log.d("ntBelongsToCurrentUser", "getView: " + message.isBelongsToCurrentUser());

        } else if (blnlist.get(i).equals("false") && message.getMsgtype().equals("image")) {
            convertView = messageInflater.inflate(R.layout.thier_message, null);
            holder.rcvmsglinear = (LinearLayout) convertView.findViewById(R.id.rcvmsglinear);
            holder.rcvmsglinear.setVisibility(View.GONE);
            holder.rcvimglinear = (LinearLayout) convertView.findViewById(R.id.linearimgrcv);
            holder.rcvimglinear.setVisibility(View.VISIBLE);
            holder.rcvimg = (ImageView) convertView.findViewById(R.id.rcvimg);
            holder.rcvimgtime = (TextView) convertView.findViewById(R.id.rcvimgtime);
            holder.rcvimgtime.setText(message.getTime());
            holder.dwnldimglinear = (LinearLayout) convertView.findViewById(R.id.dwnldimglinear);
            holder.dwnldimg = (ImageView) convertView.findViewById(R.id.dwnldimg);
            final View finalConvertView = convertView;
            if (message.getIs_downloaded() == 1) {
                Glide.with(context)
                        .load(message.getText())
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                        .into(holder.rcvimg);
                convertView.setTag(holder);
                holder.dwnldimglinear.setVisibility(View.VISIBLE);
                final String imagename=message.getFilename();
                final String fileurl=message.getText();
                final Integer msgid=message.getMsgid();
                final String filetype=message.getMsgtype();
                holder.dwnldimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadfile(fileurl,filetype,imagename);
                        if(errormsg==null) {
                            Log.d("errormsg", "storage pth " + errormsg);

                            Glide.with(context)
                                    .load(message.getText())
                                    .into(holder.rcvimg);
                            holder.dwnldimg.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("storagepath", "storage pth " + DownloadFile.storagepath);
                                    ChatDatabase.updateisdownloaded(msgid);
                                }
                            }, 1000);
                        }else{
                            Log.d("errormsg", "storage error " + errormsg);
                        }


                        finalConvertView.setTag(holder);
                       /* Toast.makeText(context, "img clicked", Toast.LENGTH_SHORT).show();
                        Glide.with(context)
                                .asBitmap()
                                .load(fileurl)
                                .into(new SimpleTarget<Bitmap>() {

                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        *//*saveImage(resource);*//*
                                        if (savedImagePath != null) {
                                            holder.dwnldimglinear.setVisibility(View.GONE);
                                            Log.d("savedimagepath", "getView: " + savedImagePath);
                                            Glide.with(context)
                                                    .load(fileurl)
                                                    .into(holder.rcvimg);
                                            ChatDatabase.updateisdownloaded(msgid);
                                            finalConvertView.setTag(holder);
                                        }
                                    }
                                });
                        isclicked = 1;*/



                    }
                });


            }
            String image=imagepath+message.getFilename();
            if (message.getIs_downloaded() == 0) {
                Log.d("imagepath","imagename: "+image);
                Glide.with(context)
                        .load(image)
                        .into(holder.rcvimg);
            }

        }
        else if (blnlist.get(i).equals("false") && message.getMsgtype().equals("file")) {
            convertView = messageInflater.inflate(R.layout.thier_message, null);
            holder.rcvmsglinear = (LinearLayout) convertView.findViewById(R.id.rcvmsglinear);
            holder.rcvmsglinear.setVisibility(View.GONE);
            holder.rcvfilelinear = (LinearLayout) convertView.findViewById(R.id.rcvfilelinear);
            holder.rcvfilelinear.setVisibility(View.VISIBLE);
            holder.rcvfilename = (TextView) convertView.findViewById(R.id.filercvname);
            holder.rcvfilename.setText(message.getFilename());
            holder.rcvfiletime = (TextView) convertView.findViewById(R.id.filercvtime);
            holder.rcvfiletime.setText(message.getTime());
            holder.rcvdwnldlinear = (LinearLayout) convertView.findViewById(R.id.rcvdwnldlinear);
            holder.rcvfiledwnld = (ImageView) convertView.findViewById(R.id.filercvdwnld);
            Log.d("fileurl", "onClick0: " + message.getText());

            final String fileurl = message.getText();
            final Integer msgid = message.getMsgid();
            if (message.getIs_downloaded() == 1) {
                final String filename=message.getFilename();
                final String filetype=message.getMsgtype();
                holder.rcvdwnldlinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("fileurl", "onClick: " + fileurl);
                        downloadfile(fileurl, filetype, filename);
                        if (errormsg == null) {
                            holder.rcvfiledwnld.setVisibility(View.INVISIBLE);
                            ChatDatabase.updateisdownloaded(msgid);
                        }
                    }
                });
            }
            if (message.getIs_downloaded() == 0) {
                holder.rcvfiledwnld.setVisibility(View.INVISIBLE);
                holder.rcvdwnldlinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "File already downloaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }

          }

        else if(blnlist.get(i).equals("false") && message.getMsgtype().equals("audio")){
            convertView = messageInflater.inflate(R.layout.thier_message, null);
            holder.rcvmsglinear = (LinearLayout) convertView.findViewById(R.id.rcvmsglinear);
            holder.rcvmsglinear.setVisibility(View.GONE);
            holder.rcvmusilinear=(LinearLayout)convertView.findViewById(R.id.musicrcvlinear);
            holder.rcvmusilinear.setVisibility(View.VISIBLE);
            holder.rcvmusicname=(TextView) convertView.findViewById(R.id.musicrcvname);
            holder.rcvmusicname.setText(message.getFilename());
            holder.rcvmusicduration=(TextView) convertView.findViewById(R.id.musicrcvduration);
            holder.rcvmusictime=(TextView) convertView.findViewById(R.id.musicrcvtime);
            holder.rcvmusictime.setText(message.getTime());
            holder.rcvmusicseekbar=(SeekBar) convertView.findViewById(R.id.musicrcvbar);
            holder.musicrcvplay=(ImageView) convertView.findViewById(R.id.musicrcvplay);
            holder.musicrcvpause=(ImageView) convertView.findViewById(R.id.musicrcvpause);
            holder.rcvmusicdwnld=(ImageView) convertView.findViewById(R.id.dwnldrcvmusic);
            final String fileurl = message.getText();
            final Integer msgid = message.getMsgid();
            final String filename=message.getFilename();
            final String filetype=message.getMsgtype();
            if (message.getIs_downloaded() == 1) {
                holder.rcvmusicdwnld.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("fileurl", "onClick: " + fileurl);
                        downloadfile(fileurl,filetype , filename);
                        if (errormsg == null) {
                            holder.rcvmusicdwnld.setVisibility(View.GONE);
                            holder.musicrcvplay.setVisibility(View.VISIBLE);
                            ChatDatabase.updateisdownloaded(msgid);
                        }
                    }
                });
            }
            if (message.getIs_downloaded() == 0) {
                holder.rcvmusicdwnld.setVisibility(View.GONE);
                holder.musicrcvplay.setVisibility(View.VISIBLE);
                Log.d("audiopath", "getView: "+audiopath+message.getFilename());
                new Playmusic().execute(audiopath+message.getFilename());
                /*holder.rcvdwnldlinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "File already downloaded", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }


        }
        return convertView;
    }
    public static void downloadfile(String url, String filetype,String filename) {
        new DownloadFile().execute(url,filetype,filename);
        Log.d("errormsg", "downloadfile: "+errormsg);
    }

    public static void enableClick(final MediaPlayer mediaPlayer){
        mediaPlayerr=mediaPlayer;
        if(holder1.musicsentplay !=null) {
            holder1.musicsentplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        holder1.musicsentplay.setVisibility(View.GONE);
                        holder1.musicsentpause.setVisibility(View.VISIBLE);

                        holder1.musicsentpause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    holder1.musicsentpause.setVisibility(View.GONE);
                                    MessageViewHolder1.musicsentplay.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        Log.d("mediaPlayer", "is null: " + mediaPlayer);
                    }

                }

            });
        }
        else if(holder.musicrcvplay !=null) {
            holder.musicrcvplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        holder.musicrcvplay.setVisibility(View.GONE);
                        holder.musicrcvpause.setVisibility(View.VISIBLE);

                        holder.musicrcvpause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    holder.musicrcvpause.setVisibility(View.GONE);
                                    holder.musicrcvplay.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    else {
                        Log.d("mediaPlayer", "is null: " + mediaPlayer);
                    }
                }
            });
        }


        Log.d("finctionenableClick", "enableClick: "+mediaPlayer);
    }

    private String saveImage(Bitmap resource) {

        final Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        final String fname = "image" + n + ".jpg";


        final File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Chatapp");

        boolean success = true;
        if(!storageDir.exists()){
            success = storageDir.mkdirs();
        }

        if(success){
            File imageFile = new File(storageDir, fname);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                stream = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            } catch (Exception e) {
                e.printStackTrace();
            }
            galleryAddPic(savedImagePath);

            // Add the image to the system gallery
            Log.d("saved", "saveImage: "+imageFile);
            Toast.makeText(context, "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        new DownloadFile().execute(message.getText());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("context", "Permission has been denied");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,context);
    }

    public static class Playmusic extends AsyncTask<String, String, MediaPlayer> {

        public static MediaPlayer mediaPlayer1;

        @Override
        protected MediaPlayer doInBackground(String... strings) {


            final String fileurl=strings[0];
            Log.d("fileurlmusic", "doInBackground: "+fileurl);
            MediaPlayer mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(fileurl);
                mPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            mediaPlayer1 = MediaPlayer.create(Chat_View.context,uri);
            Log.d("fileurlmusic", "doInBackground: "+fileurl+"\n"+"media:"+mPlayer);

            return mPlayer;
        }

        @Override
        protected void onPostExecute(MediaPlayer mediaPlayer) {
            super.onPostExecute(mediaPlayer);
            Log.d("fileurlmusic", "postexecute: \n"+"media:"+mediaPlayer);

            MessageAdapter.enableClick(mediaPlayer);
        }


    }


    public static class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;
        public static String storagepath;
        final Random generator = new Random();
        int n = 10000;




        @Override
        protected String doInBackground(String... strings) {
            int count;
            String chkfiletype=strings[1];
            File storageDir = null;
            Log.d("urll", "doInBackground: "+strings[0]);

            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);


                n = generator.nextInt(n);
                String fname = strings[2];

                //Extract file name from URL
               // fileName = strings[0].substring(strings[0].lastIndexOf('/') + 1, strings[0].length());

                //Append timestamp to file name


                if (chkfiletype.equals("image")){
                    //fname = "IMG" + n + ".jpg";
                    fileName = fname;
                    //External directory path to save file
                    // folder = Environment.getExternalStorageDirectory() + File.separator + "chatappfile/";
                     storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Chatapp/Image");
                }
               if (chkfiletype.equals("audio")){
                    fileName = fname;
                    //External directory path to save file
                    // folder = Environment.getExternalStorageDirectory() + File.separator + "chatappfile/";
                    storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Chatapp/Music");
                }

               if (chkfiletype.equals("file")){
                    fileName = fname;
                    //External directory path to save file
                    // folder = Environment.getExternalStorageDirectory() + File.separator + "chatappfile/";
                    storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Chatapp/Document");
                }

                //Create androiddeft folder if it does not exist
                //File directory = new File(folder);

                assert storageDir != null;
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                File imageFile = new File(storageDir, fileName);
                // Output stream to write file
                OutputStream output = new FileOutputStream(imageFile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("context", "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                storagepath=imageFile+fileName;
                Log.d("storagepath", "doInBackground: "+storagepath);
                return "Downloaded at: " + imageFile ;

            } catch (Exception e) {
                Log.e("Error: ","error"+ e.getLocalizedMessage());
                errormsg="Something went wrong";
            }

            return errormsg;
        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(context.getApplicationContext());
            Chat_View.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            Chat_View.progressDialog.setCancelable(false);
            Chat_View.progressDialog.setTitle("downloading file");
            Chat_View.progressDialog.setMessage("Please Wait...");
            Chat_View.progressDialog.show();
        }



        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            Chat_View.progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            Chat_View.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(context,message, Toast.LENGTH_LONG).show();
        }
    }
}

class MessageViewHolder {
    public View avatar;
    public TextView name,recvmsgtime,rcvimgtime,rcvfilename,rcvfiletime,rcvmusictime,rcvmusicname;
    public TextView messageBody,rcvmusicduration;
    public static ImageView rcvimg,dwnldimg,rcvfiledwnld,musicrcvplay,musicrcvpause,rcvmusicdwnld;
    public SeekBar rcvmusicseekbar;
    public LinearLayout rcvmsglinear,rcvimglinear,dwnldimglinear,rcvfilelinear,rcvdwnldlinear,rcvmusilinear;
}
class MessageViewHolder1 {
    public TextView messageBody,sentfilename,sentfiletime,sentmusictime,sentmusicname;;
    public TextView time,imagetime,sentmusicduration;
    public static ImageView sentimg,musicsentplay,musicsentpause;
    public SeekBar sentmusicseekbar;
    public LinearLayout linearLayout,sentmsglinear,sentfilelinear,sentmusilinear;
}


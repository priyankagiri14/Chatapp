package com.example.chatroom.Users.Chat;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.chatroom.Database.ChatDatabase;
import com.example.chatroom.MainActivity;
import com.example.chatroom.R;
import com.example.chatroom.Users.model.FileData;
import com.example.chatroom.Users.model.Fileupload;
import com.example.chatroom.Users.model.Message;
import com.example.chatroom.Userslist;
import com.example.chatroom.WebServices.DrawableClickListener;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import info.androidhive.fontawesome.FontTextView;
import io.codetail.animation.ViewAnimationUtils;
import lib.ar.arvind.drawableclicklistener.DrawableEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class Chat_View extends AppCompatActivity implements View.OnClickListener {

    public static String st;
    public static Activity activity;
    private static String msgtype;
    private static int i,keyboardstate;
    Toolbar toolbar;
    public TextView username;
    public static TextView lastseen;
    public static ImageView dprofile;
    public FontTextView gallery;
    public static ListView msglistView;
    public static DrawableEditText editText;
    public static ImageButton send,backarrow;
    public static RelativeLayout mRevealView;
    public static LinearLayout attachmentbg;
    public static PopupWindow popupWindow;
    public static MqttConnection mc;
    public static MqttAndroidClient conn;
    public static String publishmessage = "";
    public static byte[] encodedPayload;
    public static MqttMessage mqttMessage;
    private static JSONObject jsonObject;
    private static String message;
    private static String name;
    static String senderid = "";
    private static ChatDatabase chatDatabase;
    private static String publishtopic;
    private static String selecteduserid;
    private String selectedusername;
    public static int lastMessageId;
    public static Context context;
    public static MessageAdapter messageAdapter;
    private static List<Message> msglist;
    public static Userslist userslist;
    Lastseen lastseenclass;
    private static Calendar calendar= Calendar.getInstance();;
    static Integer month=calendar.get(Calendar.MONTH);
    static Integer m=month+1;
    Block_Unblockuser block_unblockuser=new Block_Unblockuser();
    public static ProgressDialog progressDialog;
    private DrawableClickListener clickListener;
    private int cx,cy,radius;
    private Animator animator;
    private boolean hidden=true;
    public static ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private final static int FILE_REQUEST_CODE = 1;
    static String date,userid,usernm;
    static String time;
    static String currenttime;
    private List<byte[]> compressimagelist;
    private String filetype;
    private ArrayList<String> docpath;
    private List<String> filelistname;
    public static RelativeLayout relativeLayout;
    static SharedPreferences pref,tokpref;
    static SharedPreferences.Editor editor,tokeditor;

    public Chat_View() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        findViews();
        chatDatabase = new ChatDatabase(this);
        userslist=new Userslist();
        mc = MqttConnection.getInstance();
        conn = mc.conn;
        if(!conn.isConnected()){
            mc = MqttConnection.getInstance();
            conn = mc.conn;
        }
        selecteduserid = userslist.selecteduserid;
        selectedusername = userslist.selectedusername;
        chatDatabase.getmessage(selecteduserid);
        username.setText(selectedusername);
        lastseenclass=new Lastseen();
        lastseenclass.getlastseen();
        lastseenclass.updatelastseen("Online");
        chatDatabase.checkuserblockstatus(Integer.valueOf(selecteduserid));
        block_unblockuser.checkblockeduser(selecteduserid);
        context = getApplicationContext();
        attachmentbg=findViewById(R.id.atchmnts);
        publishtopic = selecteduserid;
        msglist = new ArrayList<>();
        messageAdapter = new MessageAdapter(msglist,context);
        st = getClass().getName();
        userslist.st = "";
        activity=this;
        mRevealView =findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);
        compressimagelist=new ArrayList<byte[]>();
         pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
         editor = pref.edit();
         tokpref=getApplicationContext().getSharedPreferences("tokenpref", 0);
         tokeditor=tokpref.edit();
         userid=tokpref.getString("userid",null);
         usernm=tokpref.getString("username",null);

            int kstate=pref.getInt("keyboardstate", -1);
            String edittextvalue=pref.getString("inputtext",null);
            Log.d("sharedprefvalue", "onCreate: "+kstate +"\n" +edittextvalue);
            if(kstate==1){
                InputMethodManager inputMethodManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        relativeLayout.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
                editText.setText(edittextvalue);

            }

            relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    relativeLayout.getWindowVisibleDisplayFrame(r);
                    int screenHeight = relativeLayout.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        String inputtext = editText.getText().toString();
                        keyboardstate=1;

                    } else {
                        keyboardstate=0;

                    }
                }
            });


    }

    public static void refreshmenu(){
        activity.invalidateOptionsMenu();
        Log.d("is_blocked", "refreshmenu: ");
    }
    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        dprofile = findViewById(R.id.dp_profile);
        username = findViewById(R.id.person_name);
        lastseen = findViewById(R.id.typingstatus);
        msglistView = findViewById(R.id.messages_view);
        editText = findViewById(R.id.editText);
        send = findViewById(R.id.imgsend);
        backarrow=findViewById(R.id.backarw);
        backarrow.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog=new ProgressDialog(this);
        send.setOnClickListener(this);
        gallery=findViewById(R.id.gallery);
        gallery.setOnClickListener(this);
        relativeLayout=findViewById(R.id.chatview_rv);


        editText.setDrawableClickListener(new lib.ar.arvind.drawableclicklistener.DrawableClickListener() {
            @Override
            public void onDrawableClick(lib.ar.arvind.drawableclicklistener.DrawablePosition drawablePosition) {
                if(drawablePosition== lib.ar.arvind.drawableclicklistener.DrawablePosition.END){
                    openDilaog();
                }
            }
        });

    }


    /**
     * Open the attachment dialog box that consist of different attachment icons
     */
    private void openDilaog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate the custom popup layout
        final View inflatedView;

        inflatedView = layoutInflater.inflate(R.layout.attachment_layout, null, false);

        RelativeLayout layoutGallery;
        FontTextView galleryatch,music,video,contact,document,camera;
        layoutGallery = (RelativeLayout) inflatedView.findViewById(R.id.reveal_items);
        galleryatch=(FontTextView) inflatedView.findViewById(R.id.gallery);
        document=(FontTextView) inflatedView.findViewById(R.id.document);
        music=(FontTextView) inflatedView.findViewById(R.id.audio);
        video=(FontTextView) inflatedView.findViewById(R.id.video);
        document.setOnClickListener(this);
        music.setOnClickListener(this);
        galleryatch.setOnClickListener(this);
        video.setOnClickListener(this);
        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismissWindow();
            }
        });
        onShowPopup(this, inflatedView);
    }


    /**
     * @param activity
     * @param inflatedView
     */
    public static void onShowPopup(Activity activity, View inflatedView) {
       // get device size
        Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        // fill the data to the list items
        // set height depends on the device size
        popupWindow = new PopupWindow(inflatedView, size.x, ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        // set a background drawable with rounders corners
        /*popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(
                R.drawable.ic_account_circle_grey_24dp));*/
        // make it focusable to show the keyboard to enter in `EditText`
        popupWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popupWindow.setOutsideTouchable(true);

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // show the popup at bottom of the screen and set some margin at
        // bottom ie,

        popupWindow.showAtLocation(activity.getCurrentFocus(), Gravity.BOTTOM, 0,
                0);

    }


    /**
     * Dismiss the popup window to choose media from gallery
     *
     */
    public static void dismissWindow() {
        popupWindow.dismiss();
    }


    /**
     * Hide the attachment dialog layout
     */
        private void hideattachments() {
            animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(500);
        if(!hidden) {
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


    /**
     * @param v Handle the different onclick on attahcment buttons like image,video,audio,contact,location,document
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgsend) {
            if(!editText.getText().toString().trim().equals("")) {
                publishtopicfunc();
            }
        }
        if(v.getId()==R.id.gallery){
            filetype="image";
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
            dismissWindow();
        }
        else if(v.getId()==R.id.video){
            filetype="video";
            Intent intent = new Intent(Chat_View.context, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSelectedMediaFiles(mediaFiles)
                    .enableImageCapture(false)
                    .setShowVideos(true)
                    .setShowImages(false)
                    .enableVideoCapture(true)
                    .setSkipZeroSizeFiles(true)
                    .setMaxSelection(10)
                    .build());
            startActivityForResult(intent,FILE_REQUEST_CODE);
            dismissWindow();
        }
        else if(v.getId()==R.id.backarw){
            finish();
        }
        else if(v.getId()==R.id.audio){
            filetype="audio";
            Intent intent = new Intent(Chat_View.this, FilePickerActivity.class);
            MediaFile file = null;
            for (int i = 0; i < mediaFiles.size(); i++) {
                if (mediaFiles.get(i).getMediaType() == MediaFile.TYPE_AUDIO) {
                    file = mediaFiles.get(i);
                }
            }
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setShowAudios(true)
                    .setSingleChoiceMode(true)
                    .setSelectedMediaFile(file)
                    .build());
            startActivityForResult(intent, FILE_REQUEST_CODE);
            dismissWindow();
        }
        else if(v.getId()==R.id.document){
            filetype="file";
            Intent intent = new Intent(Chat_View.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSelectedMediaFiles(mediaFiles)
                    .setShowFiles(true)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setMaxSelection(10)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreHiddenFile(true)

                    .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                    .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml","zip", "tar", "gz", "rar", "7z","torrent","doc", "docx", "odt", "ott","ppt", "pptx", "pps","xls", "xlsx", "ods", "ots")
                    .build());
            startActivityForResult(intent, FILE_REQUEST_CODE);
            dismissWindow();
        }

    }

    /**
     * This function is executed after we select the media files to sent.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.setMessage("Please wait");
        if(filetype.equals("image")) {
            progressDialog.setTitle("Sending image..");
        }
        else if(filetype.equals("audio")) {
            progressDialog.setTitle("Sending audio..");
        }
        else if(filetype.equals("file")){
            progressDialog.setTitle("Sending file..");
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        List<String> filelist=new ArrayList<>();
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            /*if (filetype.equals("file")) {
                docpath = new ArrayList<>();
                docpath.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                for (int i = 0; i < docpath.size(); i++) {
                    Log.d("filesclicked", "path: " + docpath.get(i));
                    *//*Log.d("mediafilesclicked", "mimetype: " + mediaFiles.get(i).getMimeType());
                    Log.d("mediafilesclicked", "size: " + mediaFiles.get(i).getSize());
                    Log.d("mediafilesclicked", "name " + mediaFiles.get(0).getName());
                    File file = new File(mediaFiles.get(0).getPath());*//*
                }
            }
            if (filetype.equals("image")) {*/
                 filelistname=new ArrayList<>();
                mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
                for (int i = 0; i < mediaFiles.size(); i++) {
                    Log.d("mediafilesclicked", "path: " + mediaFiles.get(i).getPath());
                    Log.d("mediafilesclicked", "mimetype: " + mediaFiles.get(i).getMimeType());
                    Log.d("mediafilesclicked", "size: " + mediaFiles.get(i).getSize());
                    Log.d("mediafilesclicked", "name " + mediaFiles.get(0).getName());
                    filelistname.add(mediaFiles.get(0).getName());
                    File file = new File(mediaFiles.get(0).getPath());
                    if (filetype.equals("image")) {
                        compressimage(mediaFiles.get(i).getPath());
                        Glide.with(context)
                                .load(mediaFiles.get(0).getPath())
                                .into(dprofile);
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.d("mediafilesclicked", "bitmap" + bitmap);
                    filelist.add(mediaFiles.get(i).getPath());


                }

                uploadfile(compressimagelist, filelist);


        }
        else{
            progressDialog.dismiss();
        }
    }


    /**
     * @param byteArray it consist a list of all compress images that we have selected to sent
     * @param filelist it consist a list of url for all the mediafiles that we have selected to sent
     *        A api to upload these files on the server is also executed here
     */
    private void uploadfile(List<byte[]> byteArray,List<String> filelist) {
        Web_Interface webInterface = RetrofitClient.getClient().create(Web_Interface.class);
        if(mediaFiles!=null) {
            Call<Fileupload> call;
            final List<MultipartBody.Part> list = new ArrayList<>();


                for (int i = 0; i < filelist.size(); i++) {
                    RequestBody requestBody = null;
                    File file = new File(mediaFiles.get(i).getPath());
                    if(filetype.equals("image") ) {
                         requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray.get(i));

                    }else if(filetype.equals("file") || filetype.equals("audio")){
                        requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    }

                    MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", file.getName(), requestBody);
                    list.add(body);

                }

            call = webInterface.uploadfile(list,MainActivity.token);

            call.enqueue(new Callback<Fileupload>() {
                @Override
                public void onResponse(Call<Fileupload> call, Response<Fileupload> response) {
                    date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
                    time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
                    currenttime=date+":"+time;

                    List<FileData> fileData;

                    if(response.isSuccessful()&& response.code()==200) {
                        if (conn.isConnected()) {
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                fileData = response.body().getData();
                            publishimage(response.body().getData().get(i).getFileUrl(),response.body().getData().get(i).getId(),response.body().getData().get(i).getFileName());
                            movefiles(response.body().getData().get(i).getFileUrl(),filetype,response.body().getData().get(i).getFileName());
                            }

                       /* Message msg=new Message("",true);
                        msg.setImageurl(mediaFiles.get(0).getPath());*/
                        }
                        chatDatabase.getmessage(selecteduserid);
                        progressDialog.dismiss();
                    }
                    else {
                        Log.d("resposne", "onResponse: else case");
                        progressDialog.dismiss();
                    }

                    /*Handler h=new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        progressDialog.dismiss();

                        }
                    },2000);*/

                }


                @Override
                public void onFailure(Call<Fileupload> call, Throwable t) {
                    Toast.makeText(Chat_View.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("uploadimagefailure", "onFailure: "+t.getMessage());
                    progressDialog.dismiss();
                }
            });

        }
        mediaFiles.clear();
    }


    /**
     * When user sent any media type as a message to the user than that particular media file is move in a sperate folder
     * on the basis of their file type using async task inbackground.
     * @param url the url of the media file
     * @param filetype Type of the media file i.e. Image, Documnet, Audio, Video, Location, Contact
     * @param filename Name of the media file that that user selects to send
     */
    public static void movefiles(String url, String filetype,String filename) {
        new MoveFile().execute(url,filetype,filename);
    }
    public static class MoveFile extends AsyncTask<String, String, String> {

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
            //Log.d("urll", "doInBackground: "+strings[0] +"\n"+ strings[1]+"\n"+strings[2]);

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
                    storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Chatapp/SENT/Image");
                }
                if (chkfiletype.equals("audio")){

                    storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Chatapp/SENT/Music");
                }

                if (chkfiletype.equals("file")){
                    storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Chatapp/SENT/Document");
                }

                //Create androiddeft folder if it does not exist
                //File directory = new File(folder);

                assert storageDir != null;
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                File imageFile = new File(storageDir, fname);
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
                storagepath=imageFile+fname;
                Log.d("storagepath", "doInBackground: "+storagepath);
                return chkfiletype+" sent successfully";

            } catch (Exception e) {
                Log.e("Error: uploadfiles ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(context.getApplicationContext());
            /*Chat_View.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            Chat_View.progressDialog.setCancelable(false);
            Chat_View.progressDialog.setTitle("downloading file");
            Chat_View.progressDialog.setMessage("Please Wait...");
            Chat_View.progressDialog.show();*/
        }



        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
           // Chat_View.progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
         //   Chat_View.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(context,message, Toast.LENGTH_LONG).show();
        }
    }

    /** Function to compress image
     * @param imguri Url of the image that we want to compress
     */
    private void compressimage(String imguri) {
        Uri selectedImage =Uri.fromFile(new File(imguri));

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
        compressimagelist.add(byteArray);


    }


    /**
     *  This function publish the file on the mqtt server
     * @param imageurl Url of the image
     * @param imageid Id of the file from the db
     * @param filename Name of the file
     */
    private void publishimage(final String imageurl, final Integer imageid,final String filename) {
        if (!conn.isConnected()){
            mc = MqttConnection.getInstance();
            conn = mc.conn;
            Log.d("publishimagefunc", "connection lost "+conn);
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    publishimage(imageurl,imageid,filename);
                }
            },3000);
            return;
        }


        encodedPayload = new byte[0];
        JSONObject msgData = new JSONObject();
        try {
            msgData.put("senderid", userid);
            msgData.put("sendername", usernm);
            msgData.put("msgtype", filetype);
            msgData.put("file_name",filename);
            msgData.put("message",imageurl );
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
            msgtype=obj.getString("msgtype");



        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("json exception", "exception" + "|" + e.getMessage());
        }


        try {
            Calendar calendar=Calendar.getInstance();
            date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
            time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
            currenttime=date+":"+time;
            chatDatabase.insertData(Integer.valueOf(userid), Integer.valueOf(selecteduserid), selecteduserid
                    , imageurl, imageid, filetype,filename, String.valueOf(System.currentTimeMillis()),time, 0,0);
            conn.publish(publishtopic, encodedPayload, 2, false, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("imagepublish", "success" + message + "topic " + publishtopic + "|" + msgtype);
                    if (userid.equals(senderid)) {
                        chatDatabase.getmessage(selecteduserid);
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("imagepublish", "failure" + exception);
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("imagepublish", e.getLocalizedMessage());
        }


    }

    /**
     * Create the 3 dot menu on toolbar
     * @param menu Menu item
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        Log.d("is_blocked", "onCreateOptionsMenu: "+block_unblockuser.is_blocked);

        if(block_unblockuser.is_blocked!=null) {
            if (block_unblockuser.blocked.equals("true")) {
                MenuItem item1 = menu.findItem(R.id.block);
                item1.setTitle("Unblock");
            }
            else if(block_unblockuser.blocked.equals("false")) {
                    MenuItem item=menu.findItem(R.id.block);
                    item.setTitle("Block");
                }

        }
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Menu item select listener fuction handle here on the basis of menu item id
     * @param item Menu item differentiated by the the resource id
     * @return
     */
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
                                    chatDatabase.blockuserlocally(Integer.valueOf(selecteduserid));
                                    block_unblockuser.blockuser(userid+","+selecteduserid);

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
                                    chatDatabase.unblockuserlocally(Integer.valueOf(selecteduserid));
                                    block_unblockuser.blockuser(userid+","+selecteduserid);

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


    /**
     * This function publish the message in the form of json on mqtt server
     */
    public static void publishtopicfunc() {
            if (!conn.isConnected()){
                mc = MqttConnection.getInstance();
                conn = mc.conn;
                Log.d("publishtopicfunc", "connection lost "+conn);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        publishtopicfunc();
                    }
                },3000);
                return;
            }

                encodedPayload = new byte[0];
                JSONObject msgData = new JSONObject();
                try {
                    msgData.put("senderid",userid);
                    msgData.put("sendername", usernm);
                    msgData.put("msgtype", "text");
                    msgData.put("file_name","");
                    msgData.put("message", editText.getText());

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
                    msgtype=obj.getString("msgtype");


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("json exception", "exception" + "|" + e.getMessage());
                }


                try {
                    Calendar calendar=Calendar.getInstance();
                    date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
                    time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
                    currenttime=date+":"+time;
                    chatDatabase.insertData(Integer.valueOf(userid), Integer.valueOf(selecteduserid), selecteduserid, message, mqttMessage.getId(),msgtype,
                            "",String.valueOf(System.currentTimeMillis()), time, 0,0);
                    conn.publish(publishtopic, encodedPayload, 2, false, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d("messagepublish", "success" + message + "topic " + publishtopic + "|" + msgtype);
                            if (userid.equals(senderid)) {
                                /*Message message1=new Message(message,msgtype,String.valueOf(System.currentTimeMillis()),true,0,);
                                msglist.add(message1);*/
                                chatDatabase.getmessage(selecteduserid);
                                Log.d("clikcable", "onCreate: false");
                                editText.setText("");
                            }

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d("messagepublish", "failure" + exception);
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.d("messagepublish", e.getLocalizedMessage());
                }


        }


    /** This function refresh the chat screen everytime user receive a new message
     * @param message it include message recived by mqtt server
     * @throws ClassNotFoundException it will throws exception if class not found
     */
    public static void onTrigger(String message) throws ClassNotFoundException {
        // chatView.addMessage(new ChatMessage(message,System.currentTimeMillis(),ChatMessage.Type.RECEIVED));
        Log.d("connection", "onTrigger: "+st);
        editor.putString("inputtext", editText.getText().toString());
        editor.putInt("keyboardstate", keyboardstate);
        editor.commit();
        Intent intent = new Intent(context, Chat_View.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        activity.finish();


    }

    public void onResume() {
        super.onResume();
        block_unblockuser.checkblockeduser(selecteduserid);
        chatDatabase.getmessage(selecteduserid);

    }

    public void onPause() {
        super.onPause();
        Log.d("onpause", "onPause called: ");
        block_unblockuser.checkblockeduser(selecteduserid);
        lastseenclass.updatelastseen(String.valueOf(System.currentTimeMillis()));
        if(MessageAdapter.mediaPlayerr!=null) {
            MessageAdapter.mediaPlayerr.pause();
        }
        //typingstatus.setText( currenttime);
    }
    @Override
    public void onBackPressed(){
        if(MessageAdapter.mediaPlayerr!=null) {
            MessageAdapter.mediaPlayerr.stop();
        }
        finish();
        //moveTaskToBack(true);
    }

    public void onStop() {
        super.onStop();
        editor.clear();
        editor.commit();
        String date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        String currenttime=date+":"+time;
        lastseenclass.updatelastseen(currenttime);
        Log.d("onstop","onstop call " +currenttime);
        if(MessageAdapter.mediaPlayerr!=null) {
            MessageAdapter.mediaPlayerr.stop();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        editor.clear();
        editor.commit();
        String date=calendar.get(Calendar.DATE)+"-"+m+"-"+calendar.get(Calendar.YEAR);
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        String currenttime=date+":"+time;
        lastseenclass.updatelastseen(currenttime);
        if(MessageAdapter.mediaPlayerr!=null) {
            MessageAdapter.mediaPlayerr.stop();
        }
        finish();
    }


}

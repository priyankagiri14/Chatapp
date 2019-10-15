package com.example.chatroom.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.chatroom.MainActivity;
import com.example.chatroom.Users.Chat.Chat_View;
import com.example.chatroom.Users.Chat.MessageAdapter;
import com.example.chatroom.Users.model.Message;
import com.example.chatroom.Userslist;


import java.util.ArrayList;
import java.util.List;



public class ChatDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static List<String> msgcount;
    private static final String DATABASE_NAME = "chat_db";
    //MessageTable
    private static final String Message_TABLE_NAME = "message_table";
    private static final String ID = "id";
    private static final String Sender_id = "sender_id";
    private static final String Reciver_id = "reciver_id";
    private static final String Stream_id = "stream_id";
    private static final String Message = "message";
    private static final String Message_id = "message_id";
    private static final String Message_type = "message_type";
    private static final String Message_time = "message_time";
    private static final String Is_downloaded = "is_downloaded";
    private static final String File_name="file_name";
    private static final String Date_Time = "date_time";
    private static final String is_new = "is_new";

    //Stream table
    private static final String STREAM_TABLE_NAME = "stream_table";
    private static final String Inc_ID = "id";
    private static final String Stream_title = "stream_title";
    private static final String Stream_users = "stream_users";
    private static final String User_ID = "user_id";
    private static final String Block_Userid = "blockuser_id";
    private static final String IS_Blocked = "is_blocked";



    public static List<String> bln;
    public static List<String> msgytype;
    public MessageAdapter adapter;
    List<Message> msglist;
    public static Message message;
    public static String addmsg;
    public static Boolean msgboolean=false;
    private List<Message> mesglist;
    private List<Message> rcvmsglist;
    public  static Context context;
    public static SQLiteDatabase db1;
    public int blocked;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Message_TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT,sender_id INTEGER,reciver_id INTEGER," +
                "stream_id INTEGER,message TEXT, message_id INTEGER,message_type TEXT,file_name TEXT, date_time TEXT,message_time TEXT,is_new INTEGER,is_downloaded INTEGER)");

        db.execSQL("create table " + STREAM_TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT,stream_title TEXT,stream_users TEXT," +
                "user_id INTEGER , blockuser_id INTEGER,is_blocked INTEGER)");

    }


    /**
     *  Insert data in local database
     * @param sender_id Person who sends the message
     * @param recievr_id Person who recieve the message
     * @param stream_id stream_id
     * @param message message
     * @param message_id message or file id
     * @param message_type message_type text,image,document,audio
     * @param file_name filename
     * @param date_time date and time when the message is sent
     * @param message_time message receive time
     * @param isnew is set to 1 until the message is read by the user
     * @param isdownloaded is set to 1 until the file is downloade by the user
     * @return
     */
    public boolean insertData(Integer sender_id,Integer recievr_id, String stream_id,String message,Integer message_id,String message_type,String file_name,String date_time,String message_time,Integer isnew, Integer isdownloaded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Sender_id,sender_id);
        contentValues.put(Reciver_id,recievr_id);
        contentValues.put(Stream_id,stream_id);
        contentValues.put(Message,message);
        contentValues.put(Message_id,message_id);
        contentValues.put(Message_type,message_type);
        contentValues.put(File_name,file_name);
        contentValues.put(Date_Time,date_time);
        contentValues.put(Message_time,message_time);
        contentValues.put(is_new,isnew);
        contentValues.put(Is_downloaded,isdownloaded);
        long result = db.insert(Message_TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            Log.d("chat_database",String.valueOf(result));
            return true;
    }


    /** Fetch messages from the database
     * @param strmid using other user id
     */
    public void getmessage( String strmid) {
        /*message=new Message(addmsg,msgboolean);*/
        mesglist=new ArrayList<>();
        adapter=new MessageAdapter(mesglist,MessageAdapter.context);
        Chat_View.msglistView.setAdapter(adapter);
        List<String> sendmessagelist = new ArrayList<>();
        List<String> receivemsglist = new ArrayList<>();
        db1 = this.getReadableDatabase();
        bln=new ArrayList<>();
        msgytype=new ArrayList<>();
        Cursor cursor = db1.rawQuery("SELECT message ,sender_id ,reciver_id, message_time, message_type,file_name, is_downloaded , message_id FROM " + Message_TABLE_NAME + " WHERE " + Stream_id + "=" + strmid +" ORDER BY date_time ASC", null);
        db1.execSQL("UPDATE message_table SET is_new=0 WHERE stream_id="+strmid);
        if (cursor!=null) {

            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String rcvr_id=cursor.getString(cursor.getColumnIndex("reciver_id"));
                String time=cursor.getString(cursor.getColumnIndex("message_time"));
                String sender_id=cursor.getString(cursor.getColumnIndex("sender_id"));
                String msgtyp= cursor.getString(cursor.getColumnIndex("message_type"));
                String msg=cursor.getString(cursor.getColumnIndex("message"));
                Integer downloaded=cursor.getInt(cursor.getColumnIndex("is_downloaded"));
                Integer messageid=cursor.getInt(cursor.getColumnIndex("message_id"));
                String filename=cursor.getString(cursor.getColumnIndex("file_name"));
                 Log.d("sender_id", "getmessage: "+msg);
                if(sender_id.equals(strmid) && rcvr_id.equals(MainActivity.id)){
                    bln.add("false");
                    msgytype.add(msgtyp);
                    message=new Message(msg,msgtyp,filename,time,false,downloaded,messageid);
                    mesglist.add(message);
                    //adapter.notifyDataSetChanged();
                    Log.d("receiver", "getmessage: "+msg);
                    Log.d("connection", "mesglist " +message.isBelongsToCurrentUser());
                    receivemsglist.add(cursor.getString(cursor.getColumnIndex("message")));
                    adapter.notifyDataSetChanged();

                }
                else if(sender_id.equals(MainActivity.id)){
                    bln.add("true");
                    msgytype.add(msgtyp);
                    message=new Message(msg,msgtyp,filename,time,true,downloaded,messageid);
                    mesglist.add(message);
                    sendmessagelist.add(cursor.getString(cursor.getColumnIndex("message")));
                    //adapter.notifyDataSetChanged();
                    Log.d("sender", "getmessage: "+msg);
                    Log.d("connection", "mesglist " +message.isBelongsToCurrentUser());
                    adapter.notifyDataSetChanged();



                }
                cursor.moveToNext();
            }
            Chat_View.msglistView.setSelection( Chat_View.msglistView.getCount()-1);
            Log.d("connection", "sendmsglist " + sendmessagelist.size());
            Log.d("connection", "receiverid " + receivemsglist.toString());
            Log.d("connection", "msgtype " + msgytype.toString());

        }
    }

    /** Update the field is_downloaded in database when the file is downloaded by the user
     * @param msgid Message or file id
     */
    public static void updateisdownloaded(Integer msgid){
        db1.execSQL("UPDATE message_table SET is_downloaded =0  WHERE message_id = " +msgid);
    }


    /** Get unread message count
     * @param id Another user id
     */
    public void  messagecount(String id){

     SQLiteDatabase db = this.getReadableDatabase();
     Cursor cursor= db.rawQuery("SELECT is_new FROM "+Message_TABLE_NAME +" WHERE is_new = 1 AND "+ Stream_id+"="+id, null);
     Cursor cursor1= db.rawQuery("SELECT is_new FROM "+Message_TABLE_NAME , null);
     if(cursor.getCount()!=0){
         msgcount.add(" "+cursor.getCount() +cursor.getCount());
         Log.d("getsendmessage","moveToFirst "+msgcount.toString());


     }
     else if(cursor.getCount()==0){
       msgcount.add("");
     }
     cursor.close();
    }

    /** Check in the local database if stream aready existed or not
     * @param uid User own id
     * @param other_uid other user id
     * @return
     */
    public int checkstream(String uid, String other_uid){
        db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("SELECT stream_users FROM " + STREAM_TABLE_NAME + " WHERE  user_id =" + uid+" AND blockuser_id ="+other_uid, null);
        Log.d("chkstrm", "checkstream: "+cursor);
        if(cursor.getCount()>=1){
            return 1;
        }
        else return 0;
    }

    /**
     *  Update user stream locally
     * @param stream_title stream title if its a group chat
     * @param stream_users users in the stream
     * @param user_id user own id
     * @param block_userid another user id
     * @param is_blocked stores boolean value either he is blocked or not
     */
    public void updatestream(String stream_title, String stream_users, String user_id, Integer block_userid, Integer is_blocked){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Stream_title,stream_title);
        contentValues.put(Stream_users,stream_users);
        contentValues.put(User_ID,user_id);
        contentValues.put(Block_Userid,block_userid);
        contentValues.put(IS_Blocked,is_blocked);

        long result = db.insert(STREAM_TABLE_NAME,null ,contentValues);
        if(result == -1){
            Log.d("chat_database if",String.valueOf(result));
        }

        else
            Log.d("chat_database else",String.valueOf(result));


    }

    /** Block user in local database
     * @param id other user id
     */
    public void blockuserlocally(Integer id){
        db1.execSQL("UPDATE stream_table SET is_blocked =1  WHERE blockuser_id = " +id);
    }

    /** Unblock user in loacl database
     * @param id other user id
     */
    public void unblockuserlocally(Integer id){
        db1.execSQL("UPDATE stream_table SET is_blocked =0  WHERE blockuser_id = " +id);
    }


    /** Check user block status is in local database
     * @param id other user id
     */
        public void checkuserblockstatus(Integer id){
            Cursor cursor = db1.rawQuery("SELECT is_blocked FROM " + STREAM_TABLE_NAME + " WHERE  blockuser_id =" + id, null);
            if (cursor.getCount()>=1) {
                cursor.moveToFirst();
                Integer is_blocked=cursor.getInt(cursor.getColumnIndex("is_blocked"));
                if(is_blocked==1)
                    blocked=1;
                else
                    blocked=0;
                Log.d("is_blocked","loaclly"+blocked);
            }

        }


    public ChatDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

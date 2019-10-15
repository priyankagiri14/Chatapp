package com.example.chatroom.WebServices;

import com.example.chatroom.Users.Loginresponse;
import com.example.chatroom.Users.Signupresponse;
import com.example.chatroom.Users.model.Fileupload;
import com.example.chatroom.Users.model.Refreshtoken;
import com.example.chatroom.Users.model.Streamresponse;
import com.example.chatroom.Users.model.Updateprofilepic;
import com.example.chatroom.Users.model.Userslistmodel;

import java.util.List;

import io.netty.handler.codec.http.multipart.FileUpload;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Web_Interface {
    @Headers("Content-Type: application/json")
    @POST("signup")
    Call<Signupresponse> registration(@Body RequestBody signup);

    @Headers("Content-Type: application/json")
    @POST("login")
    Call<Loginresponse> login(@Body RequestBody login);

    @Headers({"Content-Type: application/json"})
    @GET("users/{id}")
    Call<Userslistmodel>userslist(@SuppressWarnings("deprecation") @Path("id")String id  ,@Header("x-access-code") String token );

    @Headers("Content-Type: application/json")
    @GET("lastseen/{id}")
    Call<ResponseBody>lastseen(@Path("id")String id);

    @Headers("Content-Type: application/json")
    @POST("updatelastseen/{id}")
    Call<ResponseBody>updatelastseen(@Path("id") String id, @Body RequestBody body);

    @Headers("Content-Type: application/json")
    @POST("stream")
    Call<Streamresponse> stream(@Body RequestBody stream);

    @Headers("Content-Type: application/json")
    @PUT("/block")
    Call<ResponseBody>block(@Body RequestBody block);

    @Headers("Content-Type: application/json")
    @GET("/status/{user}")
    Call<ResponseBody>statusofblock(@Path("user") String id);


    @GET("/refreshtoken")
    Call<Refreshtoken>refreshtoken(@Header("refresh-token") String token );

    @Multipart
    @POST("/file/upload")
    Call<Fileupload> uploadfile(@Part List<MultipartBody.Part> file,@Header("x-access-code") String token );

    @PUT("/file/profile_pic")
    Call<ResponseBody> updateprofilepic(@Body RequestBody body,@Header("x-access-code") String token);

    @GET("/file/set_profile_pic/{id}")
    Call<ResponseBody> setprofilepic(@Path("id")String id,@Header("x-access-code") String token);
}

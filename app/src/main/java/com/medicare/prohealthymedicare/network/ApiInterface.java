package com.medicare.prohealthymedicare.network;



import com.medicare.prohealthymedicare.model.ResponseBodyAuth;
import com.medicare.prohealthymedicare.model.ResponseBodyDokter;
import com.medicare.prohealthymedicare.model.ResponseJenis;
import com.medicare.prohealthymedicare.model.ResponsePesan;
import com.medicare.prohealthymedicare.model.ResponsePost;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @Multipart
    @POST("tambahdokter")
    Call<ResponsePost> tambahdokter(
            @Part MultipartBody.Part foto,
            @Part("nama") String nama,
            @Part("jenis") String jenis,
            @Part("hari") String hari,
            @Part("jam") String jam,
            @Part("cp") String cp
    );

    @Multipart
    @POST("updatefoto/{uid}")
    Call<ResponsePost> updatefoto(
            @Path("uid") String uid,
            @Part MultipartBody.Part foto
    );

    //========Autentifikasi========
    @GET("detail")
    Call<ResponseBodyAuth> getuserdetail(
            @Query("uid") String uid
    );

    @GET("getdokter")
    Call<ResponseBodyDokter> getdokter();

    @POST("register")
    @FormUrlEncoded
    Call<ResponsePost> registerusers(
            @Field("uid") String uid,
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("nohp") String nohp,
            @Field("password") String password
    );

    @POST("updateauth")
    @FormUrlEncoded
    Call<ResponsePost> updateauth(
            @Field("uid") String uid,
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("password") String password,
            @Field("nohp") String nohp
    );

    @GET("getjenis")
    Call<ResponseJenis> getjenis();

    //pesan dokter
    @POST("pesandokter")
    @FormUrlEncoded
    Call<ResponsePost> pesandokter(
            @Field("nama") String nama,
            @Field("hari") String hari,
            @Field("jam") String jam,
            @Field("jenis") String jenis,
            @Field("uid") String uid,
            @Field("keluhan") String keluhan
    );

    //get pesan dokter
    @GET("readdokter/{uid}")
    Call<ResponsePesan> readdokter(
            @Path("uid") String uid
    );

    //update pesan dokter
    @POST("updatepesan")
    @FormUrlEncoded
    Call<ResponsePost> updatepesan(
            @Field("hari") String hari,
            @Field("jam") String jam,
            @Field("jenis") String jenis,
            @Field("uid") String uid,
            @Field("keluhan") String keluhan
    );

    //update pesan dokter
    @POST("deletedokter")
    @FormUrlEncoded
    Call<ResponsePost> hapusdokter(
            @Field("id") Integer id
    );
    //edit data dokter
    @Multipart
    @POST("updatedokter")
    Call<ResponsePost> updatedokter(
            @Part MultipartBody.Part foto,
            @Part("nama") String nama,
            @Part("jenis") String jenis,
            @Part("hari") String hari,
            @Part("jam") String jam,
            @Part("cp") String cp,
            @Part("id") Integer id
    );

    //login
    @POST("loginadmin")
    @FormUrlEncoded
    Call<ResponsePost> loginadmin(
            @Field("username") String username,
            @Field("password") String password

    );

}
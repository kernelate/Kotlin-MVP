package com.cherry.home.data.api

import com.cherry.home.data.model.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface UserApi {

//    @FormUrlEncoded
//    @POST("cherry_home/login.php")
//    fun loginUser(@Field("email") email: String, @Field("password") password: String): Single<LoginResponse>
//
//    @GET("todos/1")
//    fun getValue() : Single<ResponseBody>
//
//    @FormUrlEncoded
//    @POST("cherry_home/register.php")
//    fun registerUser(@Field("email") email: String, @Field("password") password: String): Single<ServerResponse>

    @FormUrlEncoded
    //local
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun setMode(@Query("mode") mode : String, @Field("email") email: String, @Field("password") password: String) : Single<ServerResponse>

    @FormUrlEncoded
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun activate(@Query("mode") mode : String, @Field("email") email: String) : Single<ResponseBody>

    @FormUrlEncoded
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun addFamily(@Query("mode") mode : String, @Field("uid") uid :String, @Field("fname") fname : String) : Single<ServerResponse>

    @FormUrlEncoded
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun sendValidationCode(@Query("mode") mode : String, @Query("proc") proc :String, @Field("code") code : String, @Field("email") email: String) : Single<ChangePasswordResponse>

    @FormUrlEncoded
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun getVerificationCode(@Query("mode") mode : String, @Query("proc") proc: String, @Field("email") email: String) : Single<ChangePasswordResponse>

    @FormUrlEncoded
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun sendNewPassword(@Query("mode") mode : String, @Query("proc") proc :String, @Field("code") code : String, @Field("email") email: String, @Field("password") password: String) : Single<ChangePasswordResponse>

    @FormUrlEncoded
//    @POST("cherry_home/index.php")
    @POST("index.php")
    fun getSigned(@Query("mode") mode : String, @Field("client_id") clientId : String, @Field("access_token") token : String?,  @Field("secret") secret : String, @Field("timestamp") timestamp : Long) : Single<GetSignedResponse>

    @GET
    fun getToken(@Url url : String, @Query("grant_type") type : Int, @HeaderMap headers: Map<String,String>) : Single<GetTokenResponse>

    @GET
    fun refreshToken(@Url url : String, @HeaderMap headers: Map<String,String>) : Single<GetTokenResponse>

    @POST
    fun registerToTuya(@Url url : String, @HeaderMap headers: Map<String,String>, @Body user : TuyaUser) : Single<UserRegistrationResponse>
}
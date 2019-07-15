package com.cherry.home.data;

import com.cherry.home.data.api.UserApi
import io.reactivex.Single
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton
import com.cherry.home.data.model.*
import retrofit2.http.*

@Singleton
class DataManager @Inject
constructor(private val userApi: UserApi) {

//    fun loginUser(email: String, password: String) : Single<LoginResponse> {
//        return userApi.loginUser(email, password)
//    }
//
//    fun registerUser(email : String, password: String) : Single<ServerResponse>{
//        return userApi.registerUser(email, password)
//    }
//
//    fun getValue() : Single<ResponseBody>{
//        return userApi.getValue()
//    }

    fun setMode(@Query("mode") mode : String, email: String, password: String) : Single<ServerResponse>{
        return userApi.setMode(mode, email, password)
    }

    fun activate(@Query("mode") mode : String, @Field("email") email: String) : Single<ResponseBody>{
        return userApi.activate(mode, email)
    }

    fun addFamily(@Query("mode") mode : String, @Field("uid") uid :String, @Field("fname") fname : String) : Single<ServerResponse>{
        return userApi.addFamily(mode, uid, fname)
    }

    fun getVerificationCode(@Query("mode") mode : String, @Query("proc") proc: String, @Field("email") email: String) : Single<ChangePasswordResponse>{
        return userApi.getVerificationCode(mode, proc, email)
    }

    fun sendValidationCode(@Query("mode") mode : String, @Query("proc") proc :String, @Field("code") code : String, @Field("email") email: String) : Single<ChangePasswordResponse>{
        return userApi.sendValidationCode(mode, proc, code, email)
    }

    fun sendNewPassword(@Query("mode") mode : String, @Query("proc") proc :String, @Field("code") code : String, @Field("email") email: String, @Field("password") password: String) : Single<ChangePasswordResponse>{
        return userApi.sendNewPassword(mode, proc, code, email, password)
    }

    fun getSigned(@Query("mode") mode : String, @Field("client_id") clientId : String, @Field("access_token") token : String?, @Field("secret") secret : String, @Field("timestamp") timestamp : Long) : Single<GetSignedResponse>{
        return userApi.getSigned(mode, clientId, token, secret, timestamp)
    }

    fun getToken(@Url url : String, @Query("grant_type") type : Int, @HeaderMap headers: Map<String,String>) : Single<GetTokenResponse>{
        return userApi.getToken(url, type, headers)
    }

    fun refreshToken(@Url url : String, @HeaderMap headers: Map<String,String>) : Single<GetTokenResponse>{
        return userApi.refreshToken(url, headers)
    }

    fun registerToTuya(@Url url : String,  @HeaderMap headers: Map<String,String>, @Body user : TuyaUser) : Single<UserRegistrationResponse> {
        return userApi.registerToTuya(url, headers, user)
    }
}
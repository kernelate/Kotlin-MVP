package com.cherry.home.data.model

import com.squareup.moshi.Json

data class UserRegistrationResponse(val code: Int?, val msg : String?, val success : Boolean, @Json(name = "t") val t : Long?, val result : GetToken?)
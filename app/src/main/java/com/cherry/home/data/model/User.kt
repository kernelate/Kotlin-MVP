package com.cherry.home.data.model

import com.squareup.moshi.Json

data class User(
        @Json(name = "email") val email: String,
        @Json(name = "uid") val uid : String
)
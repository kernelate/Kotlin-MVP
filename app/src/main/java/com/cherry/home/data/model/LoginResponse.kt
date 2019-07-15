package com.cherry.home.data.model

import com.squareup.moshi.Json

data class LoginResponse(val code: String, val error_msg: String, val status: String, val user: User)
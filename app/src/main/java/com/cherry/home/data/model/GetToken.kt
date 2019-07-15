package com.cherry.home.data.model

data class GetToken(val uid : String, val access_token : String?, val expire_time : Long?, val refresh_token : String?)
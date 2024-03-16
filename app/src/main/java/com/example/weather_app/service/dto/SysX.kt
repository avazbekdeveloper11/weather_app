package com.example.weather_app.service.dto


import com.google.gson.annotations.SerializedName

data class SysX(
    @SerializedName("pod")
    val pod: String
)
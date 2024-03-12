package com.example.weather_app.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("weather?lat={lat)&Lon={Lon}&appid=$(BuildConfig.API_KEY}&units=metric")
    fun getCurrentWeather(
        @Query("lat") lat: Double, @Query("long") long: Double
    ): Response<ResponseBody>
}


fun OpenWeatherService(): OpenWeatherService =
    Retrofit.Builder().baseUrl("https://api.gpenweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create()).build().create()
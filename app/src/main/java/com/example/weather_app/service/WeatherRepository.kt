package com.example.weather_app.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.example.weather_app.service.dto.Forecast
import com.example.weather_app.service.dto.ForecastResponse
import com.example.weather_app.service.dto.WeatherResponse
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date


@Suppress("DEPRECATION")
class WeatherRepository {
    private val service = OpenWeatherService()


    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun currentLocationWeather(context: Context): Flow<WeatherResponse?> {
        return locationFlow(context).map {
            service.getCurrentWeather(
                it.latitude, it.longitude, "256ac14bfcb4feb683054de7c4e625e5",
            ).body()
        }
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun weatherForecast(context: Context): Flow<List<Forecast>> {
        return locationFlow(context).map {
            service.getWeatherForecast(
                it.latitude, it.longitude, "256ac14bfcb4feb683054de7c4e625e5"
            ).body()
        }.filterNotNull().map { extractDailyForecast(it) }
    }

//    @RequiresPermission(ACCESS_FINE_LOCATION)
//    fun currentLocationForecast(context: Context): Flow<ForecastResponse?> {
//        return locationFlow(context).map {
//            service.getWeatherForecast(
//                it.latitude, it.longitude, "256ac14bfcb4feb683054de7c4e625e5",
//            ).body()
//        }
//    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow(context: Context) = channelFlow<Location> {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation!!)
            }
        }
        val request = LocationRequest.create().setInterval(10_000).setFastestInterval(5_000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(170f)
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose {
            client.removeLocationUpdates(callback)
        }

    }


    private fun extractDailyForecast(response: ForecastResponse): List<Forecast> {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val future = response.list.filter {
            val cal = Calendar.getInstance().apply {
                time = Date(it.dt * 1000)
            }
            cal.get(Calendar.DAY_OF_MONTH) > today
        }

        val dailyForecast = future.groupBy {
            Calendar.getInstance().apply {
                time = Date(it.dt * 1000)
            }.get(Calendar.DAY_OF_MONTH)
        }


        return dailyForecast.mapValues { (_, forecast) ->
            forecast.getOrNull(4) ?: forecast.first()
        }.mapNotNull { (_, forecast) ->
            forecast
        }
    }
}
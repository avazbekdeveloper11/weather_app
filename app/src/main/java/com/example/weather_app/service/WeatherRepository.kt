package com.example.weather_app.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import androidx.annotation.RequiresPermission
import com.example.weather_app.service.dto.Forecast
import com.example.weather_app.service.dto.ForecastResponse
import com.example.weather_app.service.dto.WeatherResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date


class WeatherRepository {
    private val service = OpenWeatherService()


    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun currentLocationWeather(): Flow<WeatherResponse?> {
        return locationFlow().map {
            service.getCurrentWeather(
                it.latitude, it.longitude, "256ac14bfcb4feb683054de7c4e625e5",
            ).body()
        }
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun weatherForecast(): Flow<List<Forecast>> {
        return locationFlow().map {
            service.getWeatherForecast(
                it.latitude, it.longitude, "256ac14bfcb4feb683054de7c4e625e5"
            ).body()
        }.filterNotNull().map { extractDailyForecast(it) }
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow<Location> {
        delay(DEFAULT_LOCATION_TIMEOUT)
        trySend(DEFAULT_LOCATION)
    }

    companion object {
        private const val DEFAULT_TASHKENT_LATITUDE = 41.2995
        private const val DEFAULT_TASHKENT_LONGITUDE = 69.2401
        private const val DEFAULT_LOCATION_TIMEOUT = 10_000L // 10 seconds
        private val DEFAULT_LOCATION = Location("").apply {
            latitude = DEFAULT_TASHKENT_LATITUDE
            longitude = DEFAULT_TASHKENT_LONGITUDE
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
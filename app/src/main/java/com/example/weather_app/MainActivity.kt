package com.example.weather_app

import MainViewModel
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_app.service.dto.Forecast
import com.example.weather_app.service.dto.Weather
import com.example.weather_app.service.dto.WeatherResponse
import com.example.weather_app.ui.theme.CloudyBlue
import com.example.weather_app.ui.theme.RainyBlue
import com.example.weather_app.ui.theme.SunGreen
import com.example.weather_app.ui.theme.WeatherAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                val permission =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
                PermissionRequired(permissionState = permission, permissionNotGrantedContent = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        Text("Allow the location")
                        Button(onClick = { permission.launchPermissionRequest() }) { Text("Give permission") }
                    }
                }, permissionNotAvailableContent = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        Text("Allow the location")
                        Button(onClick = { permission.launchPermissionRequest() }) { Text("Give permission") }
                    }
                }) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun weatherBackgroundColor(conditions: String): Color {
    return when {
        conditions.contains("cloud", ignoreCase = true) -> CloudyBlue
        conditions.contains("rain", ignoreCase = true) -> RainyBlue
        else -> SunGreen
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val weather by viewModel.weather.collectAsState(null)
    val forecast by viewModel.forecast.collectAsState(emptyList())
    val backgroundColor = weather?.weather?.firstOrNull()?.main?.let { weatherBackgroundColor(it) }

    rememberSystemUiController().setStatusBarColor(
        backgroundColor ?: SunGreen
    )

    Column(Modifier.fillMaxSize()) {
        weather?.let {
            WeatherSummary(weather = it)
            TemperatureSummary(weather = it)
            HorizontalDivider()
        }
        Box(
            modifier = Modifier
                .background(backgroundColor ?: Color.White)
                .fillMaxSize()
        ) {
            if (weather == null || forecast.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center), color = Color(0xFF47AB2F)
                )
            } else {
                FiveDayForecast(forecast = forecast)
            }
        }
    }
}

@Composable
fun FiveDayForecast(forecast: List<Forecast?>) {
    LazyColumn {
        items(forecast) { dayForecast ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
            ) {
                Text(
                    SimpleDateFormat(
                        "EEEE", Locale.getDefault()
                    ).format(Date((dayForecast?.dt ?: 0) * 1_000)),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Icon(
                    painter = painterResource(
                        dayForecast?.forecastIcon() ?: R.drawable.forest_sunny
                    ),
                    tint = Color.White,
                    contentDescription = "Forecast icon",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                )
                Text(
                    formatTemperature(dayForecast?.main?.temp ?: 0.0),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun TemperatureSummary(weather: WeatherResponse?) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(weather?.weather?.backgroundColor() ?: Color.Transparent)
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatTemperature(weather?.main?.tempMin ?: 0.0),
                fontSize = 20.sp,
                color = Color.White

            )
            Text(
                text = "Min", color = Color.White
            )

        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatTemperature(weather?.main?.temp ?: 0.0), fontSize = 20.sp, color = Color.White

            )
            Text(
                text = "Now", color = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatTemperature(weather?.main?.tempMax ?: 0.0),
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = "Max", color = Color.White
            )
        }
    }
}

@Composable
private fun WeatherResponse.getWeatherConditions(): String? {
    return weather.firstOrNull()?.main
}

@Composable
private fun WeatherSummary(weather: WeatherResponse?) {
    Box {
        weather?.let {
            Image(
                painter = painterResource(it.background()),
                contentDescription = "Background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF47AB2F))
            )
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    formatTemperature(it.main.temp),
                    fontSize = 48.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 48.dp),
                )
                Text(
                    it.getWeatherConditions().toString(),
                    fontSize = 24.sp,
                    color = Color.White,
                )
                Text(
                    it.name.toString(),
                    fontSize = 18.sp,
                    color = Color.White,
                )
            }
        }
    }
}

private fun formatTemperature(temp: Double): String {
    return (temp).roundToInt().toString() + "°"
}

@Composable
private fun WeatherResponse.background(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.forest_cloudy
        conditions.contains("rain", ignoreCase = true) -> R.drawable.forest_rainy
        else -> R.drawable.forest_sunny
    }

}


@Composable
private fun List<Weather>.backgroundColor(): Color {
    val conditions = first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> CloudyBlue
        conditions.contains("rain", ignoreCase = true) -> RainyBlue
        else -> SunGreen
    }
}

@Composable
private fun WeatherResponse.backgroundColor(): Color {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> CloudyBlue
        conditions.contains("rain", ignoreCase = true) -> RainyBlue
        else -> SunGreen
    }
}

@Composable
private fun Forecast.forecastIcon(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.partlysunny
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rain
        else -> R.drawable.clear
    }
}




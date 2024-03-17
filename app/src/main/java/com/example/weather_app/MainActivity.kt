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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
            TemperatureSummary(weather = it, backgroundColor ?: Color.White)
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
                FiveDayForecast(forecast = forecast, backgroundColor ?: Color.White)
            }
        }
    }
}

@Composable
fun FiveDayForecast(forecast: List<Forecast?>, backgroundColor: Color) {
    LazyColumn(
        modifier = Modifier.background(Color(0xFF536DFE)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(forecast) { dayForecast ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                RoundedCornerShape(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = SimpleDateFormat(
                            "EEEE",
                            Locale.getDefault()
                        ).format(Date((dayForecast?.dt ?: 0) * 1_000)),
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(
                            dayForecast?.forecastIcon() ?: R.drawable.forest_sunny
                        ),
                        contentDescription = "Forecast icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = formatTemperature(dayForecast?.main?.temp ?: 0.0),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun TemperatureSummary(weather: WeatherResponse?, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 28.dp, vertical = 8.dp)
            .background(
                color = Color(0xFF536DFE), shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp), contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemperatureItem(
                label = "Min", temperature = formatTemperature(weather?.main?.tempMin ?: 0.0)
            )
            TemperatureItem(
                label = "Now", temperature = formatTemperature(weather?.main?.temp ?: 0.0)
            )
            TemperatureItem(
                label = "Max", temperature = formatTemperature(weather?.main?.tempMax ?: 0.0)
            )
        }
    }
}

@Composable
private fun TemperatureItem(label: String, temperature: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = temperature, fontSize = 20.sp, color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f)
        )
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
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTemperature(it.main.temp),
                    fontSize = 48.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = it.getWeatherConditions().toString(),
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = it.name,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
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
private fun Forecast.forecastIcon(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.partlysunny
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rain
        else -> R.drawable.clear
    }
}

package com.example.weather_app

import MainViewModel
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.weather_app.service.dto.WeatherResponse
import com.example.weather_app.ui.theme.CloudyBlue
import com.example.weather_app.ui.theme.RainyBlue
import com.example.weather_app.ui.theme.SunGreen
import com.example.weather_app.ui.theme.WeatherAppTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            //todo fetch current location granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                val weather by viewModel.weather.collectAsState(null)
                Column(Modifier.fillMaxSize()) {
                    weather?.let {
                        WeatherSummary(weather = weather)
                        TemperatureSummary(weather = weather)
                        HorizontalDivider()
                    }
                    Box(
                        modifier = Modifier
                            .background(weather?.backgroundColor() ?: Color.White)
                            .fillMaxSize()
                    ) {
                        if (weather == null) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color(0xFF47AB2F)
                            )
                        } else {
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemperatureSummary(weather: WeatherResponse?)  {
    Row(
        Modifier
            .fillMaxWidth()
            .background(weather?.backgroundColor() ?: Color.Transparent)
            .padding(horizontal = 28 .dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatTemperature(weather?.main?.tempMin ?: 0.0),
                fontSize = 20.sp,
                color = Color.White

            )
            Text(
                text = "Min",
                color = Color.White
            )

        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatTemperature(weather?.main?.temp ?: 0.0),
                fontSize = 20.sp,
                color = Color.White

            )
            Text(
                text = "Now",
                color = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatTemperature(weather?.main?.tempMax ?: 0.0),
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = "Max",
                color = Color.White
            )
        }
    }
}

@Composable
fun WeatherSummary(weather: WeatherResponse?) {
    Box {
        if (weather != null) {
            Image(
                painter = painterResource(weather.background()),
                contentDescription = "Background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF47AB2F))
            )
        }
        Column(
            Modifier.align(Alignment.TopCenter), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                formatTemperature(weather?.main?.temp ?: 0.0) ?: "",
                fontSize = 48.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 48.dp),
            )
            Text(
                weather?.weather?.first()?.main.toString(),
                fontSize = 24.sp,
                color = Color.White,
            )
            Text(
                weather?.name.toString(),
                fontSize = 18.sp,
                color = Color.White,
            )
        }
    }
}

private fun formatTemperature(temp: Double): String {
    return (temp).roundToInt().toString() + "°"
}

@Composable
private fun WeatherResponse.background(): Int {
    return if (weather.first().main.contains("cloud", ignoreCase = true)) {
        R.drawable.forest_cloudy
    } else if (weather.first().main.contains("rain", ignoreCase = true)) {
        R.drawable.forest_rainy
    } else {
        R.drawable.forest_sunny
    }
}


@Composable
private fun WeatherResponse.backgroundColor(): Color {
    return if (weather.first().main.contains("cloud", ignoreCase = true)) {
        CloudyBlue
    } else if (weather.first().main.contains("rain", ignoreCase = true)) {
        RainyBlue
    } else {
        SunGreen
    }
}




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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.ui.theme.Weather_appTheme

class MainActivity : ComponentActivity() {
    private  val viewModel : MainViewModel by viewModels()
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) viewModel.onPermissionGranted()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Weather_appTheme {
                Column(Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.forest_sunny),
                        contentDescription = "Background",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF47AB2F ))
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF47AB2F))
                            .fillMaxSize()
                    ) {
                        Text("Mavlonov Avazbek")
                    }
                }
            }
        }
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        ViewModelProvider(this)[MainViewModel::class.java]
    }
}
 
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.weather_app.service.WeatherRepository
import com.example.weather_app.service.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow


@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WeatherRepository()

    val weather: Flow<WeatherResponse?> = repo.currentLocationWeather(getApplication() as Context)

//    fun onPermissionGranted(): Flow<WeatherResponse?> {
//        return repo.currentLocationWeather(getApplication() as Context)
//        viewModelScope.launch {
//            WeatherRepository().getWeather().collect {
//                println(it)
//            }
//
//        }
//        val client = LocationServices.getFusedLocationProviderClient(getApplication() as Context)
//        val request =
//            LocationRequest.create()
//                .setInterval(10_000)
//                .setFastestInterval(5_000)
//                .setPriority(
//                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//                ).setSmallestDisplacement(170f)
//
//        client.requestLocationUpdates(
//            request,
//            object : LocationCallback() {
//                override fun onLocationResult(result: LocationResult) {
//                    lasLocation = result.lastLocation
//                    GlobalScope.launch {
//                        println(lasLocation?.latitude)
//                        println(lasLocation?.longitude)
//                        val response = OpenWeatherService().getCurrentWeather(
//                            lasLocation?.latitude ?: 0.0,
//                            lasLocation?.longitude ?: 0.0,
//                            "94a46a17e969389e327ded622595fa39"
//                        )
//                        println(response.body())
//                    }
//                }
//
//                override fun onLocationAvailability(p0: LocationAvailability) = Unit
//            },
//            Looper.getMainLooper(),
//        )
//    }
}


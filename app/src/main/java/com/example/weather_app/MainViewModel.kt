import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.example.weather_app.service.OpenWeatherService
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@OptIn(DelicateCoroutinesApi::class)
@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var lasLocation: Location? = null

    fun onPermissionGranted() {
        val client = LocationServices.getFusedLocationProviderClient(getApplication() as Context)
        val request =
            LocationRequest.create()
                .setInterval(10_000)
                .setFastestInterval(5_000)
                .setPriority(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                ).setSmallestDisplacement(170f)

        client.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    lasLocation = result.lastLocation
                    GlobalScope.launch {
                        println(lasLocation?.latitude)
                        println(lasLocation?.longitude)
                        val response = OpenWeatherService().getCurrentWeather(
                            lasLocation?.latitude ?: 0.0,
                            lasLocation?.longitude ?: 0.0,
                            "94a46a17e969389e327ded622595fa39"
                        )
                        println(response.body())
                    }
                }

                override fun onLocationAvailability(p0: LocationAvailability) = Unit
            },
            Looper.getMainLooper(),
        )
    }
}


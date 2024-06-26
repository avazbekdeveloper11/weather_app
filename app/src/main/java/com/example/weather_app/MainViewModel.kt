import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.weather_app.service.WeatherRepository
import com.example.weather_app.service.dto.Forecast
import com.example.weather_app.service.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow


@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = WeatherRepository()

    val weather: Flow<WeatherResponse?> = repo.currentLocationWeather()

    val forecast: Flow<List<Forecast?>> = repo.weatherForecast()
}

package almansa.es.klima

import almansa.es.klima.data.Request
import almansa.es.klima.data.Response
import almansa.es.klima.db.AppDatabase
import almansa.es.klima.db.entities.Location
import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.arch.persistence.room.Room
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainActivity.db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "klima")
                .build()

        btKlima.setOnClickListener({ thisView -> obtainWeatherByCityAndCountryCode(thisView) })
        btLastKlima.setOnClickListener({ thisView -> obtainWeatherFromLastCall(thisView) })
        btClear.setOnClickListener({thisView -> clearUiData(thisView) })
        btGps.setOnClickListener({thisView -> getGPSPositionWeather(thisView) })

    }

    private fun getGPSPositionWeather(thisView: View?) {
        var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        val locationListener : LocationListener = object : LocationListener{

            override fun onLocationChanged(location: android.location.Location?) {
                perfomAsyncGPSApiCall(location!!.latitude, location.longitude)
            }

            override fun onProviderDisabled(provider: String?) { }

            override fun onProviderEnabled(provider: String?) { }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }

        }

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

            }
        }

        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, locationListener)

    }

    private fun obtainWeatherByCityAndCountryCode(view: View){

        val cityName = etCity.text.toString()
        val countryCode = etCountry.text.toString()

        if(cityNameAndCountryCodeAreCorrect(cityName, countryCode)) {

            inserCityAndCountryCodeInDb(cityName, countryCode)
            perfomAsyncApiCall(cityName, countryCode)

        }else{
            longToast("City Name and Country Code are mandatory")
        }

    }

    private fun perfomAsyncApiCall(cityName: String, countryCode: String) {
        doAsync {
            val request = Request(cityName, countryCode)
            processResponse(request)
        }
    }

    private fun perfomAsyncGPSApiCall(lat: Double, long: Double) {
        doAsync {
            val request = Request()

            lateinit var response : Response

            try {
                response = request.makeGpsApiCall(lat, long)

            } catch (e: Exception) {
                uiThread {
                    longToast("City not found")
                }
                return@doAsync
            }

            uiThread {
                fillUIInformation(response)
            }
        }
    }

    private fun AnkoAsyncContext<MainActivity>.processResponse(request: Request) {
        lateinit var response : Response

        try {
            response = request.makeApiCall()

        } catch (e: Exception) {
            uiThread {
                longToast("City not found")
            }
            return
        }

        uiThread {
            fillUIInformation(response)
        }
    }

    private fun fillUIInformation(response: Response) {
        toast("Showing weather...")

        tvEditCity.text = response.name
        tvEditCountry.text = response.sys.country
        tvEditDesc.text = response.weather.get(0).description
        tvEditTemp.text = "${response.main.temp} ºC"
        tvEditMaxTemp.text = "${response.main.temp_max} ºC"
        tvEditMinTemp.text = "${response.main.temp_min} ºC"
        tvEditWind.text = "${response.wind.speed} m/s"
    }

    private fun obtainWeatherFromLastCall(thisView: View?) {
        toast("Loading weather from Last Call")
        getCityAndCountryFromDb()
    }

    private fun inserCityAndCountryCodeInDb(city: String, countryCode: String){
        doAsync {
            MainActivity.db.locationDao().insert(Location(city, countryCode))
        }
    }

    private fun getCityAndCountryFromDb() {
        lateinit var locations: List<Location>

        doAsync {
            locations = MainActivity.db.locationDao().getAll()

            uiThread {
                if(locations.isNotEmpty()) {
                    val city = locations.last().cityName
                    val countryCode = locations.last().countryCode
                    perfomAsyncApiCall(city, countryCode)
                }else{
                    longToast("No preavious locations found")
                }
            }
        }

    }

    private fun clearUiData(thisView: View) {
        tvEditCity.text = "-"
        tvEditCountry.text = "-"
        tvEditDesc.text = "-"
        tvEditTemp.text = "- ºC"
        tvEditMaxTemp.text = "- ºC"
        tvEditMinTemp.text = "- ºC"
        tvEditWind.text = "- m/s"
    }

    private fun cityNameAndCountryCodeAreCorrect(cityName: String, countryCode: String): Boolean {
        return !TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(countryCode)
    }

}


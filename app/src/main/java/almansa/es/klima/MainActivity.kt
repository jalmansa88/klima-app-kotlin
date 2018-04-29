package almansa.es.klima

import almansa.es.klima.data.Request
import almansa.es.klima.data.Response
import almansa.es.klima.db.AppDatabase
import almansa.es.klima.db.entities.Location
import android.arch.persistence.room.Room
import android.os.Bundle
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


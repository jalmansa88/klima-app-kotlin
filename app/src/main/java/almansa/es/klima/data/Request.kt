package almansa.es.klima.data

import com.google.gson.Gson
import java.net.URL

class Request(private val city: String, private val countryCode: String) {

    companion object {
        private val API_KEY = "bfe22390bdff0dd7c046f988832e09d2"
        private val API_URL = "http://api.openweathermap.org/data/2.5/weather?&units=metric"
        private val COMPLETE_URL = "$API_URL&APPID=$API_KEY&q="
    }

    fun makeApiCall(): Response{
        val jsonRequest = URL("$COMPLETE_URL$city,$countryCode").readText()

        return Gson().fromJson(jsonRequest, Response::class.java)
    }
}
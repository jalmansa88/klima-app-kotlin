package almansa.es.klima.data

import com.google.gson.Gson
import java.net.URL

class Request(private val city: String, private val countryCode: String) {

    constructor() : this("", "")

    companion object {
        private val API_KEY = "bfe22390bdff0dd7c046f988832e09d2"
        private val API_URL = "http://api.openweathermap.org/data/2.5/weather?&units=metric"
        private val URL_API = "$API_URL&APPID=$API_KEY"
    }

    fun makeApiCall(): Response {
        val jsonRequest = URL("$URL_API&q=$city,$countryCode").readText()

        return Gson().fromJson(jsonRequest, Response::class.java)
    }

    fun makeGpsApiCall(long: Double, lat: Double): Response {

        val jsonRequest = URL("$URL_API&lat=$lat&lon=$long").readText()

        return Gson().fromJson(jsonRequest, Response::class.java)
    }
}
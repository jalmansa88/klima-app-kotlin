package almansa.es.klima.data

data class Response(val name: String, val main: Main, val weather: List<Weather>, val wind: Wind, val sys: Sys)

data class Main(val temp: Float, val temp_min: Float, val temp_max: Float)
data class Weather(val description: String)
data class Wind(val speed: Float)
data class Sys(val country: String)
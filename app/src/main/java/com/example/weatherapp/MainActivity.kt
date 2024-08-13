package com.example.weatherapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//0f86346f649eebcb3228794899bafd00
class MainActivity : AppCompatActivity() {


    private val  binding:ActivityMainBinding  by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        searchCity()



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun searchCity() {
        val  searchView = binding.searchView
           searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
               android.widget.SearchView.OnQueryTextListener {
               override fun onQueryTextSubmit(query: String?): Boolean {
                   if (query != null) {
                       fetchWeatherData(query)
                   }
                   return true

               }

               override fun onQueryTextChange(newText: String?): Boolean {
                   return true
               }
           })


    }

    private  fun  fetchWeatherData(cityName:String){

        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val  response  = retrofit.getWeatherData(cityName,"0f86346f649eebcb3228794899bafd00","metric" )
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val tempreture = responseBody.main.temp.toString()
                    val  humidity  = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise =  responseBody.sys.sunrise.toLong()
                    val sunset  =  responseBody.sys.sunset.toLong()
                    val sealevel  =  responseBody.main.pressure

                    val condition  =  responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxtemp = responseBody.main.temp_max
                    val mintemp =responseBody.main.temp_min

                      binding.temp.text = "$tempreture °C"
                      binding.weather.text = condition
                      binding.maxTemp.text = "Max Temp $maxtemp °C"
                      binding.minTemp.text = "Min temp $mintemp °C"
                     binding.humidity.text = "$humidity %"
                     binding.windspeed.text = "$windSpeed m/s"
                     binding.seurise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sealevel.text = "$sealevel hPa"
                    binding.conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text =date()
                        binding.cityName.text = "$cityName"


                    //Log.d("TAG", "onResponse: $tempreture")

                    changeImageAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeImageAccordingToWeatherCondition(conditionS:String) {

        when(conditionS){
            "Clear Sky ", "Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.mainact)

            }
            "Partly Clouds", "Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloud)

            }

            "light Rain", "Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain)

            }
            "light Snow", "Blizzard","Moderate Snow","Heavy Snow" ->{
                binding.root.setBackgroundResource(R.drawable.snow)

            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.mainact)
            }
        }
    }

    private  fun date():String{

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }

    private  fun time(timestamp:Long):String{

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }

    fun  dayName(timestamp:Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}
package com.example.skycast

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.skycast.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//a9b68a90572ee1980bfd08abf4f398eb

class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        fetchWeatherData("jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
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


    private fun fetchWeatherData(cityName:String) {
        //Setting Retrofit
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName , "a9b68a90572ee1980bfd08abf4f398eb","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody!=null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text= "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text="Max Temp: $maxTemp °C"
                    binding.minTemp.text="Min Temp: $minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text= condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.cityName.text="$cityName"

                    changeWeatherAccordingToCondition(condition)


                    // Log.d("TAG","onResponse: $temperature")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeWeatherAccordingToCondition(conditions:String) {
        when(conditions){

            "Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.before_rain)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunnybg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light rain","Dizzle","Rain","Moderate Rain","Showers","Heavy Rain","Thunderstorm"->{
                binding.root.setBackgroundResource(R.drawable.rainbg)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snowbg)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunnybg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }



        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date() :String{
        val sdf =SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())

        return sdf.format(Date())
    }

    private fun time(timestamp: Long) :String{
        val sdf =SimpleDateFormat("HH:mm", Locale.getDefault())

        return sdf.format(Date(timestamp*1000))
    }


    fun dayName(timestamp: Long):String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())

        return sdf.format(Date())

    }

}

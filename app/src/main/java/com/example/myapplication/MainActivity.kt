package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var cityInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cityInput = findViewById(R.id.cityInput)
        searchButton = findViewById(R.id.searchButton)
        resultText = findViewById(R.id.resultText)

        searchButton.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) {
                getWeather(city)
            }

        }
    }
    private fun getWeatherEmoji(temp: Double): String {
        return when {
            temp >= 35 -> "🔥"
            temp >= 25 -> "☀️"
            temp >= 15 -> "🌤️"
            temp >= 5 -> "⛅"
            temp >= -5 -> "🌨️"
            else -> "❄️"
        }
    }
    private fun getWeather(city: String) {
        val apiKey = "09e05e906cc0b55dd71eb7fd39330ab7"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=en"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(url).readText()
                val jsonObject = JSONObject(response)

                val cityName = jsonObject.getString("name")
                val temp = jsonObject.getJSONObject("main").getDouble("temp")
                val humidity = jsonObject.getJSONObject("main").getInt("humidity")
                val description = jsonObject.getJSONArray("weather")
                    .getJSONObject(0).getString("description")
                val emoji = getWeatherEmoji(temp)
                val result = """
                    City: $cityName
                    Temperature: ${temp.toInt()}°C
                    Condition: $description
                    Humidity: $humidity%
                    $emoji
                """.trimIndent()

                withContext(Dispatchers.Main) {
                    resultText.text = result
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultText.text = "Error: ${e.message}"
                }
            }
        }
    }
}
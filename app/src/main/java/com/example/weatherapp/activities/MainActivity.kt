package com.example.weatherapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.R
import com.example.weatherapp.model.Weather
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    lateinit var locationTextView: TextView
    lateinit var timeTextView: TextView
    lateinit var icon: ImageView
    lateinit var tempTextView: TextView
    lateinit var precipitationTextView: TextView
    lateinit var windTextView: TextView
    lateinit var windImageView: ImageView
    lateinit var windSpeedTextView: TextView


    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        locationTextView = findViewById(R.id.locationTextView)
        timeTextView = findViewById(R.id.timeTextView)
        icon = findViewById(R.id.iconImageView)
        tempTextView = findViewById(R.id.tempTextView)
        precipitationTextView = findViewById(R.id.precipitationTextView)
        windTextView = findViewById(R.id.windTextView)
        windImageView = findViewById(R.id.windImageView)
        windSpeedTextView = findViewById(R.id.windSpeedTextView)

        requestQueue = Volley.newRequestQueue(this)
        getWeather()
    }

    private fun getWeather() {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=moscow,ru&appid=e34e4c19711deb09f38f50619aa775c1"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url, null, { response ->
                try {
                    val jsonArray = response.getJSONArray("main")
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val temp = jsonObject.getString("temp")
                        val weather = Weather()
                        weather.temp = temp

                    }
                   /* movieAdapter = MovieAdapter(
                        this@MainActivity,
                        movies!!
                    )*/
                    //movieAdapter!!.setOnItemClickListener(this@MainActivity)
                    //recyclerView!!.adapter = movieAdapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        requestQueue!!.add(request)
    }
}
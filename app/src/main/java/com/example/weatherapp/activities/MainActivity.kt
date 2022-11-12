package com.example.weatherapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.BuildConfig.DEBUG
import com.example.weatherapp.R
import com.example.weatherapp.model.Weather
import org.json.JSONException
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var locationTextView: TextView
    lateinit var timeTextView: TextView
    lateinit var icon: ImageView
    lateinit var tempTextView: TextView
    lateinit var precipitationTextView: TextView
    lateinit var windTextView: TextView
    lateinit var windImageView: ImageView
    lateinit var windSpeedTextView: TextView
    lateinit var restartImageButton: ImageButton

    val weather = Weather()

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
        restartImageButton = findViewById(R.id.restartImageButton)

        requestQueue = Volley.newRequestQueue(this)

        getWeather()

        restartImageButton.setOnClickListener(View.OnClickListener {
            getWeather()
            updateUi()
        })




    }

    private fun getWeather() {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=moscow,ru&appid=e34e4c19711deb09f38f50619aa775c1"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url, null, { response ->
                try {
                    val jsonObjectTemp = response.getJSONObject("main")
                    val temp = (jsonObjectTemp.getString("temp").toFloat() - 273.15).toInt().toString()
                    weather.temp = temp

                    val jsonObjectPrecipitation = response.getJSONObject("wind")
                    val windSpeed = (jsonObjectPrecipitation.getString("speed")).toFloat().toInt().toString()
                    weather.windSpeed = windSpeed


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        requestQueue!!.add(request)

    }


    private fun updateUi() {

        tempTextView!!.text = weather.temp.toString()
        //locationTextView!!.text = "" + weather.location.toString()
        windSpeedTextView!!.text = weather.windSpeed.toString() + " m/s"
    }

}
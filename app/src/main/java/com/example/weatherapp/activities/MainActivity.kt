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
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&q=Yekaterinburg,ru&appid=e34e4c19711deb09f38f50619aa775c1"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url, null, { response ->
                try {
                    //вынести в отдельный код посторяющиеся части
                    val jsonArray = response.getJSONArray("list")
                    val jsonObjectTemp = jsonArray.getJSONObject(0).getJSONObject("main")
                    val temp =
                        (jsonObjectTemp.getString("temp").toFloat() - 273.15).toInt().toString()
                    weather.temp = temp


                    val jsonObjectLocation = response.getJSONObject("city").getString("name")
                    val location = jsonObjectLocation.toString()
                    weather.location = location

                    //привести в нормальный формат даты
                    val jsonObjectTime = jsonArray.getJSONObject(0).getString("dt_txt")
                    val time = jsonObjectTime.toString().split(" ")[0]
                    weather.time = time


                    val jsonObjectPrecipitation = jsonArray.getJSONObject(0)
                        .getJSONArray("weather").getJSONObject(0)
                    val precipitation = jsonObjectPrecipitation.getString("main").toString()
                    weather.precipitation = precipitation

                    val jsonObjectWindSpeed = jsonArray.getJSONObject(0)
                        .getJSONObject("wind").getString("speed")
                    Log.d("jsonObjectWindSpeed:", jsonObjectWindSpeed.toString())
                    val windSpeed = jsonObjectWindSpeed.toFloat().toInt().toString()
                    weather.windSpeed = windSpeed

                    /*for (i in 1 until 7) {
                        val jsonObjectTemp = jsonArray.getJSONObject(i).getJSONObject("main")
                        Log.d("getJSONObject", jsonObjectTemp.toString())
                        val temp =
                            (jsonObjectTemp.getString("temp").toFloat() - 273.15).toInt().toString()
                        weather.temp = temp

                        /*val jsonObjectPrecipitation = response.getJSONObject("wind")
                        val windSpeed =
                            (jsonObjectPrecipitation.getString("speed")).toFloat().toInt()
                                .toString()
                        weather.windSpeed = windSpeed*/
                    }*/

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        requestQueue!!.add(request)

    }


    private fun updateUi() {

        tempTextView!!.text = weather.temp.toString() + "°"
        locationTextView!!.text = weather.location.toString()
        windSpeedTextView!!.text = weather.windSpeed + " m/s"
        timeTextView!!.text = weather.time.toString()
        precipitationTextView!!.text = weather.precipitation.toString()
    }

}
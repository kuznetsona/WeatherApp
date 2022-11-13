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
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var locationTextView: TextView
    lateinit var timeTextView: TextView
    lateinit var icon: ImageView
    private lateinit var tempTextView: TextView
    lateinit var precipitationTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var windImageView: ImageView
    lateinit var windSpeedTextView: TextView
    lateinit var restartImageButton: ImageButton

    //_______________________________________
    lateinit var todayDate: TextView
    lateinit var tomorrowDate: TextView
    lateinit var thirdDate: TextView
    lateinit var fourthDate: TextView
    lateinit var fifthDate: TextView
    lateinit var sixthDate: TextView
    lateinit var seventhDate: TextView

    lateinit var todayPrecipitation: ImageView
    lateinit var tomorrowPrecipitation: ImageView
    lateinit var thirdPrecipitation: ImageView
    lateinit var fourthPrecipitation: ImageView
    lateinit var fifthPrecipitation: ImageView
    lateinit var sixthPrecipitation: ImageView
    lateinit var seventhPrecipitation: ImageView

    lateinit var todayTemp: TextView
    lateinit var tomorrowTemp: TextView
    lateinit var thirdTemp: TextView
    lateinit var fourthTemp: TextView
    lateinit var fifthTemp: TextView
    lateinit var sixthTemp: TextView
    lateinit var seventhTemp: TextView
    //_______________________________________

    private val weather = Weather()

    private var requestQueue: RequestQueue? = null

    private var tempArray: ArrayList<String> = arrayListOf()
    var precipitationArray: ArrayList<String> = arrayListOf()
    var dateArray: ArrayList<String> = arrayListOf()

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

    //__________________________________________________________________________
        todayDate = findViewById(R.id.today_date)
        tomorrowDate = findViewById(R.id.tomorrow_date)
        thirdDate = findViewById(R.id.third_date)
        fourthDate = findViewById(R.id.fourth_date)
        fifthDate = findViewById(R.id.fifth_date)
        sixthDate = findViewById(R.id.sixth_date)
        seventhDate = findViewById(R.id.seventh_date)

        todayPrecipitation = findViewById(R.id.today_precipitation)
        tomorrowPrecipitation = findViewById(R.id.tomorrow_precipitation)
        thirdPrecipitation = findViewById(R.id.third_precipitation)
        fourthPrecipitation = findViewById(R.id.fourth_precipitation)
        fifthPrecipitation = findViewById(R.id.fifth_precipitation)
        sixthPrecipitation = findViewById(R.id.sixth_precipitation)
        seventhPrecipitation = findViewById(R.id.seventh_precipitation)

        todayTemp = findViewById(R.id.today_temp)
        tomorrowTemp = findViewById(R.id.tomorrow_temp)
        thirdTemp = findViewById(R.id.third_temp)
        fourthTemp = findViewById(R.id.fourth_temp)
        fifthTemp = findViewById(R.id.fifth_temp)
        sixthTemp = findViewById(R.id.sixth_temp)
        seventhTemp = findViewById(R.id.seventh_temp)
    //__________________________________________________________________________



        requestQueue = Volley.newRequestQueue(this)

        getWeather()
        //updateUi()

        restartImageButton.setOnClickListener(View.OnClickListener {
            tempArray.clear()
            getWeather()
            updateUi()
        })




    }

    private fun getWeather() {
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat={lat}" +
                "&q=moscow,ru&appid=e34e4c19711deb09f38f50619aa775c1"

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

                    for (i in 0 until 7) {

                        val jsonObjectTempWeek = jsonArray.getJSONObject(i).getJSONObject("main")
                        val tempWeek = (jsonObjectTempWeek.getString("temp")
                            .toFloat() - 273.15).toInt().toString()
                        tempArray.add(tempWeek)

                        val jsonObjectTimeWeek = jsonArray.getJSONObject(i).getString("dt_txt")
                        val timeWeek = jsonObjectTimeWeek.toString().split(" ")[0]
                        dateArray.add(timeWeek)
                        Log.d("timeWeek", timeWeek)

                    }

                    //_________________________________________
                    weather.todayTemp = tempArray[0]
                    weather.tomorrowTemp = tempArray[1]
                    weather.thirdTemp = tempArray[2]
                    weather.fourthTemp = tempArray[3]
                    weather.fifthTemp = tempArray[4]
                    weather.sixthTemp = tempArray[5]
                    weather.seventhTemp = tempArray[6]

                    weather.todayDate = dateArray[0]
                    weather.tomorrowDate = dateArray[1]
                    weather.thirdDate = dateArray[2]
                    weather.fourthDate = dateArray[3]
                    weather.fifthDate = dateArray[4]
                    weather.sixthDate = dateArray[5]
                    weather.seventhDate = dateArray[6]
                    //_________________________________________


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

        //_________________________________________
        todayTemp!!.text = weather.todayTemp  + "°"
        tomorrowTemp!!.text = weather.tomorrowTemp  + "°"
        thirdTemp!!.text = weather.thirdTemp  + "°"
        fourthTemp!!.text = weather.fourthTemp  + "°"
        fifthTemp!!.text = weather.fifthTemp  + "°"
        sixthTemp!!.text = weather.sixthTemp  + "°"
        seventhTemp!!.text = weather.seventhTemp  + "°"

        todayDate!!.text = weather.todayDate
        tomorrowDate!!.text = weather.tomorrowDate
        thirdDate!!.text = weather.thirdDate
        fourthDate!!.text = weather.fourthDate
        fifthDate!!.text = weather.fifthDate
        sixthDate!!.text = weather.sixthDate
        seventhDate!!.text = weather.seventhDate
        //_________________________________________


    }

}
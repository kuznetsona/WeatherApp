package com.example.weatherapp.activities

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Movie
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.*
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.WeatherData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val CHECK_SETTINGS_CODE = 111
        private const val REQUEST_LOCATION_PERMISSION = 222
    }

    private lateinit var locationTextView: TextView
    lateinit var timeTextView: TextView
    lateinit var iconImageView: ImageView
    private lateinit var tempTextView: TextView
    lateinit var precipitationTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var windImageView: ImageView
    lateinit var windSpeedTextView: TextView
    lateinit var restartImageButton: ImageButton
    lateinit var searchImageButton: ImageButton


    lateinit var recyclerView: RecyclerView
    lateinit var weatherAdapter: WeatherAdapter
    var weatherData: ArrayList<WeatherData> = arrayListOf()


    /*
    lateinit var todayDate: TextView
    lateinit var tomorrowDate: TextView
    lateinit var thirdDate: TextView
    lateinit var fourthDate: TextView
    lateinit var fifthDate: TextView

    lateinit var todayPrecipitation: ImageView
    lateinit var tomorrowPrecipitation: ImageView
    lateinit var thirdPrecipitation: ImageView
    lateinit var fourthPrecipitation: ImageView
    lateinit var fifthPrecipitation: ImageView

    lateinit var todayTemp: TextView
    lateinit var tomorrowTemp: TextView
    lateinit var thirdTemp: TextView
    lateinit var fourthTemp: TextView
    lateinit var fifthTemp: TextView
    */

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var settingsClient: SettingsClient? = null

    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null

    private var currentLocation: Location? = null


    lateinit var url: String

    private var requestQueue: RequestQueue? = null


    lateinit var latThis: String
    lateinit var lonThis: String

    var locationCity = ""
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)

        buildLocationRequest()
        buildLocationCallBack()
        buildLocationSettingsRequest()
        startLocationUpdates()


        locationTextView = findViewById(R.id.locationTextView)
        timeTextView = findViewById(R.id.timeTextView)

        iconImageView = findViewById(R.id.iconImageView)

        tempTextView = findViewById(R.id.tempTextView)
        precipitationTextView = findViewById(R.id.precipitationTextView)
        windTextView = findViewById(R.id.windTextView)
        windImageView = findViewById(R.id.windImageView)
        windSpeedTextView = findViewById(R.id.windSpeedTextView)
        restartImageButton = findViewById(R.id.restartImageButton)
        searchImageButton = findViewById(R.id.searchImageButton)



        recyclerView = binding.weekRecyclerView
        //weatherAdapter = WeatherAdapter()
        //recyclerView.adapter = weatherAdapter

        /*
        todayDate = findViewById(R.id.today_date)
        tomorrowDate = findViewById(R.id.tomorrow_date)
        thirdDate = findViewById(R.id.third_date)
        fourthDate = findViewById(R.id.fourth_date)
        fifthDate = findViewById(R.id.fifth_date)

        todayPrecipitation = findViewById(R.id.today_precipitation)
        tomorrowPrecipitation = findViewById(R.id.tomorrow_precipitation)
        thirdPrecipitation = findViewById(R.id.third_precipitation)
        fourthPrecipitation = findViewById(R.id.fourth_precipitation)
        fifthPrecipitation = findViewById(R.id.fifth_precipitation)

        todayTemp = findViewById(R.id.today_temp)
        tomorrowTemp = findViewById(R.id.tomorrow_temp)
        thirdTemp = findViewById(R.id.third_temp)
        fourthTemp = findViewById(R.id.fourth_temp)
        fifthTemp = findViewById(R.id.fifth_temp)
        */

        requestQueue = Volley.newRequestQueue(this)

        restartImageButton.setOnClickListener {
            weatherData.clear()
            defineCity()

        }

        searchImageButton.setOnClickListener {
            weatherData.clear()
            DialogManager.searchByNameDialog(this,
                object : DialogManager.Listener {
                    override fun onClick(name: String?) {
                        if (name != null) {
                            locationCity = name
                            parseDayWeather()
                        }
                    }

                })

        }


    }

    private fun defineCity() {
        val urlDefineCity = "https://api.openweathermap.org/geo/1.0/reverse?" +
                "lat=" +
                latThis +
                "&" +
                "lon=" +
                lonThis +
                "&appid=e34e4c19711deb09f38f50619aa775c1"

        val requestCity = JsonArrayRequest(
            Request.Method.GET,
            urlDefineCity, null, { response ->
                try {
                    locationCity = response.getJSONObject(0).getString("name").toString()
                    Log.d("locationCity_defineCity", locationCity)

                    parseDayWeather()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        requestQueue!!.add(requestCity)

    }

    private fun parseDayWeather() {
        url = "https://api.openweathermap.org/data/2.5/forecast?" +
                "q=" +
                locationCity +
                "&units=metric" +
                "&appid=e34e4c19711deb09f38f50619aa775c1"


        // выделить в разные функции парсирование и все остальное
        val request = JsonObjectRequest(
            Request.Method.GET,
            url, null, { response ->
                try {
                    val jsonArray = response.getJSONArray("list")
                    Log.d("list", jsonArray.toString())
                    for (i in 0 until jsonArray.length() step 7) {

                        val item = WeatherData(
                            response.getJSONObject("city").getString("name"),
                            jsonArray.getJSONObject(i).getString("dt_txt")
                                .toString().split(" ")[0],
                            jsonArray.getJSONObject(i).getJSONArray("weather")
                                .getJSONObject(0).getString("main").toString(),
                            jsonArray.getJSONObject(i).getJSONArray("weather")
                                .getJSONObject(0).getString("icon"),

                            jsonArray.getJSONObject(i).getJSONObject("main")
                                .getString("temp").toFloat().toInt().toString(),

                            jsonArray.getJSONObject(i).getJSONObject("main")
                                .getString("temp_min").toFloat().toInt().toString(),

                            jsonArray.getJSONObject(i).getJSONObject("main")
                                .getString("temp_max").toFloat().toInt().toString(),

                            response.getJSONObject("city").getJSONObject("coord")
                                .getString("lat").toString(),
                            response.getJSONObject("city").getJSONObject("coord")
                                .getString("lon").toString(),
                            jsonArray.getJSONObject(i).getJSONObject("wind")
                                .getString("speed").toFloat().toInt().toString(),
                            jsonArray.getJSONObject(i).getJSONArray("weather")
                                .getJSONObject(0).getString("icon")
                        )

                        Log.d("weatherData_item", item.toString())
                        weatherData.add(item)

                    }

                    weatherAdapter = WeatherAdapter()

                    recyclerView.adapter = weatherAdapter
                    weatherAdapter.setList(weatherData)
                    //weatherAdapter!!.setOnItemClickListener(WeatherAdapter, recyclerView,)

                    updateDayWeatherUi(0)
                    //updateUi()


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }

        requestQueue!!.add(request)
        Log.d("weatherData", locationCity)
        Log.d("weatherData", weatherData.toString())

    }

    private fun updateDayWeatherUi(i: Int) {
        tempTextView!!.text = weatherData[i].currentTemp + "°"
        locationTextView!!.text = weatherData[i].city
        windSpeedTextView!!.text = weatherData[i].windSpeed + " m/s"
        timeTextView!!.text = updateData(weatherData[i].time)
        precipitationTextView!!.text = weatherData[i].precipitation
        updateIconUi(i, iconImageView)

    }

/*
    private fun updateUi() {
        todayTemp!!.text = weatherData[0].maxTemp + "° / " + weatherData[0].minTemp + "°"
        tomorrowTemp!!.text = weatherData[1].maxTemp + "° / " + weatherData[1].minTemp + "°"
        thirdTemp!!.text = weatherData[2].maxTemp + "° / " + weatherData[2].minTemp + "°"
        fourthTemp!!.text = weatherData[3].maxTemp + "° / " + weatherData[3].minTemp + "°"
        fifthTemp!!.text = weatherData[4].maxTemp + "° / " + weatherData[4].minTemp + "°"


        todayDate!!.text = updateData(weatherData[0].time)
        tomorrowDate!!.text = updateData(weatherData[1].time)
        thirdDate!!.text = updateData(weatherData[2].time)
        fourthDate!!.text = updateData(weatherData[3].time)
        fifthDate!!.text = updateData(weatherData[4].time)

        updateIconUi(0, todayPrecipitation)
        updateIconUi(1, tomorrowPrecipitation)
        updateIconUi(2, thirdPrecipitation)
        updateIconUi(3, fourthPrecipitation)
        updateIconUi(4, fifthPrecipitation)


        //_________________________________________


    }
*/
    private fun updateIconUi(i: Int, icon: ImageView) {
        Picasso.get().load(
            "https://openweathermap.org/img/wn/"
                    + weatherData[i].icon + "@2x.png"
        )
            .resize(100, 100).into(icon)
    }

    private fun updateData(date: String): String {
        val splitDate = date.toString().split("-")
        val month = date(splitDate[1].toInt() - 1)
        return month + ", " + splitDate[2]
    }

    //сделать кардВью, чтобы не указывать все функции отдельно
    fun makeToday(view: View) {
        updateDayWeatherUi(0)
    }

    fun makeTomorrow(view: View) {
        updateDayWeatherUi(1)
    }

    fun makeThird(view: View) {
        updateDayWeatherUi(2)
    }

    fun makeFourth(view: View) {
        updateDayWeatherUi(3)
    }

    fun makeFifth(view: View) {
        updateDayWeatherUi(4)
    }


    private fun startLocationUpdates() {
        settingsClient!!.checkLocationSettings(locationSettingsRequest!!)
            .addOnSuccessListener(this,
                OnSuccessListener {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat
                            .checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnSuccessListener
                    }
                    fusedLocationClient!!.requestLocationUpdates(
                        locationRequest!!,
                        locationCallback!!,
                        Looper.myLooper()
                    )

                    if (currentLocation != null) {
                        latThis = currentLocation!!.latitude.toString()
                        lonThis = currentLocation!!.longitude.toString()
                    }

                })
            .addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this@MainActivity,
                            CHECK_SETTINGS_CODE
                        )
                    } catch (sie: IntentSender.SendIntentException) {
                        sie.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val message = "Adjust location settings on your device"
                        Toast.makeText(
                            this@MainActivity, message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                if (currentLocation != null) {
                    latThis = currentLocation!!.latitude.toString()
                    lonThis = currentLocation!!.longitude.toString()
                }
            }

        fusedLocationClient!!.removeLocationUpdates(locationCallback!!)


    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)
        locationSettingsRequest = builder.build()
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
                //updateLocationUi()

                latThis = currentLocation!!.latitude.toString()
                lonThis = currentLocation!!.longitude.toString()

                Log.d("cord_my", latThis + " / " + lonThis)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CHECK_SETTINGS_CODE -> when (resultCode) {
                RESULT_OK -> {
                    Log.d(
                        "MainActivity", "User has agreed to change location" +
                                "settings"
                    )
                    startLocationUpdates()
                }
                RESULT_CANCELED -> {
                    Log.d(
                        "MainActivity", "User has not agreed to change location" +
                                "settings"
                    )
                    //updateUi()
                    //weather.lat = currentLocation!!.latitude
                    //weather.lon = currentLocation!!.longitude
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission()) {
            startLocationUpdates()
        } else if (!checkLocationPermission()) {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (shouldProvideRationale) {
            showSnackBar(
                "Location permission is needed for app functionality", "OK"
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun showSnackBar(mainText: String, action: String, listener: View.OnClickListener) {
        Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_INDEFINITE)
            .setAction(action, listener).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isEmpty()) {
                Log.d(
                    "onRequestPermissions",
                    "Request was cancelled"
                )
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()

            } else {
                showSnackBar(
                    "Turn on location on settings",
                    "Settings"
                ) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID,
                        null
                    )
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
    }


    private fun checkLocation() {
        if (checkLocationPermission()) {
            startLocationUpdates()
        } else {
            DialogManager.openDialog(this,
                object : DialogManager.Listener {

                    override fun onClick(name: String?) {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
        }
    }
}


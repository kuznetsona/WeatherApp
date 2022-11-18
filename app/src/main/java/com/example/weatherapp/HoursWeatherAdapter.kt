package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.activities.MainActivity
import com.example.weatherapp.model.WeatherData
import kotlinx.android.synthetic.main.day_item.view.*

class HoursWeatherAdapter(private val listener: MainActivity)
    : RecyclerView.Adapter<HoursWeatherAdapter.HoursWeatherViewHolder>() {
        lateinit var weather: ArrayList<WeatherData>
        class HoursWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            fun bind(weather: WeatherData, listener: MainActivity){

                itemView.day_date.text = updateData(weather.time)
                itemView.day_temp!!.text = weather.maxTemp + "° / " + weather.minTemp  + "°"

                itemView.setOnClickListener {
                    listener.onClick(weather, layoutPosition)

                }

            }


            private fun updateData(date : String): String {
                val splitDate = date.split(" ")[1].split(":")
                return splitDate[0] + ":" + splitDate[1]
            }


        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HoursWeatherViewHolder {
            val view = LayoutInflater
                .from(viewGroup.context).inflate(
                    R.layout.day_recycler_view,
                    viewGroup, false
                )
            return HoursWeatherViewHolder(view)
        }

        override fun onBindViewHolder(hoursWeatherViewHolder: HoursWeatherViewHolder, i: Int) {
            hoursWeatherViewHolder.bind(weather[i], listener)

        }

        interface Listener {
            fun onClick(weatherData: WeatherData, i: Int)
        }


        override fun getItemCount(): Int {
            return 5
        }

        fun setList(list: ArrayList<WeatherData>){
            weather = list

            notifyDataSetChanged()
        }


    }

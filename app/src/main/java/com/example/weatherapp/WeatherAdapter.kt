package com.example.weatherapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.activities.MainActivity
import com.example.weatherapp.model.WeatherData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.day_item.view.*
import java.util.ArrayList

class WeatherAdapter()
    : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    private lateinit var weather: ArrayList<WeatherData>
    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    lateinit var dayDate: TextView
   // lateinit var dayPrecipitation: ImageButton
    lateinit var dayTemp: TextView

    private var listener: AdapterView.OnItemClickListener? = null



    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    /*inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayDate: TextView
        var dayPrecipitation: ImageButton
        var dayTemp: TextView

        init {
            dayDate = itemView.findViewById(R.id.day_date)
            dayPrecipitation = itemView.findViewById(R.id.day_precipitation)
            dayTemp = itemView.findViewById(R.id.day_temp)
            itemView.setOnClickListener {
/*
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(position)
                    }
                }*/
            }
        }
    }*/

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): WeatherViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context).inflate(
            R.layout.day_item,
            viewGroup, false
        )
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(weatherViewHolder: WeatherViewHolder, i: Int) {
        //val currentDay = weather[i]
        //val dayDate = currentDay.time
        //val dayPrecipitation = currentDay.icon
        //val dayTempMin = currentDay.minTemp
        //val dayTempMax = currentDay.maxTemp

        weatherViewHolder.itemView.day_date.text = updateData(weather[i].time)
        weatherViewHolder.itemView.day_temp!!.text = weather[i].maxTemp+
                "° / " + weather[i].minTemp  + "°"

        Picasso.get().load("https://openweathermap.org/img/wn/"
                + weather[i].icon + "@2x.png")
            .into(weatherViewHolder.itemView.day_precipitation)
    }

    override fun getItemCount(): Int {
        return 5
    }

    private fun updateData(date : String): String {
        val splitDate = date.toString().split("-")
        val month =  date(splitDate[1].toInt() - 1)
        return month + ", " + splitDate[2]
    }

    interface OnItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            TODO("Not yet implemented")
        }



    }

    fun setList(list: ArrayList<WeatherData>){
        weather = list

        notifyDataSetChanged()
    }



}




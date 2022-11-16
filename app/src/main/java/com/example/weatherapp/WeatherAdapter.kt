package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.model.WeatherData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.day_item.view.*

class WeatherAdapter(val listener: Listener)
    : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    lateinit var weather: ArrayList<WeatherData>
    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(weather: WeatherData, listener: Listener){

            itemView.day_date.text = updateData(weather.time)
            itemView.day_temp!!.text = weather.maxTemp+
                    "° / " + weather.minTemp  + "°"


            Picasso.get().load("https://openweathermap.org/img/wn/"
                    + weather.icon + "@2x.png")
                .into(itemView.day_precipitation)

            itemView.setOnClickListener {
                listener.onClick(weather, layoutPosition)

            }

        }


        private fun updateData(date : String): String {
            val splitDate = date.toString().split("-")
            val month =  date(splitDate[1].toInt() - 1)
            return month + ", " + splitDate[2]
        }


    }

    lateinit var dayDate: TextView
   // lateinit var dayPrecipitation: ImageButton
    lateinit var dayTemp: TextView

    //private var listener: AdapterView.OnItemClickListener? = null

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
        weatherViewHolder.bind(weather[i], listener)

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




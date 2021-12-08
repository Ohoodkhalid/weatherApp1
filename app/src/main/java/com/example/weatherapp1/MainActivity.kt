package com.example.weatherapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var address: TextView
    lateinit var updated_at: TextView
    lateinit var status: TextView
    lateinit var temp: TextView
    lateinit var temp_min: TextView
    lateinit var temp_max: TextView
    lateinit var sunrise_At: TextView
    lateinit var sunset_At: TextView
    lateinit var wind: TextView
    lateinit var pressure: TextView
    lateinit var humidity: TextView
    lateinit var refresh :TextView
    var zipCode = 10001
    var apiKey = "46b80ade8848e5b52c88402578b7f41a"
    var userInput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        address = findViewById(R.id.address)
        updated_at = findViewById(R.id.updated_at)
        status = findViewById(R.id.status)
        temp = findViewById(R.id.temp)
        temp_min = findViewById(R.id.temp_min)
        temp_max = findViewById(R.id.temp_max)
        sunrise_At = findViewById(R.id.sunrise_At)
        sunset_At = findViewById(R.id.sunset_At)
        wind = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        humidity = findViewById(R.id.humidity)
       refresh = findViewById(R.id.refresh)

        requestAPI()
        address.setOnClickListener {
            showDialog()

        }

      refresh.setOnClickListener{
          requestAPI()

      }
    }


    private fun requestAPI() {

        CoroutineScope(Dispatchers.IO).launch {
            // we fetch the data
            val data = async { fetchData() }.await()

            if (data.isNotEmpty()) {
                getData(data)
                Log.d("able", "requestAPI: ")
            } else {
                Log.d("MAIN", "Unable to get data")
            }
        }
    }

    private fun fetchData(): String {

        var response = ""
        try {
            response =
                URL("http://api.openweathermap.org/data/2.5/weather?zip=$zipCode&appid=46b80ade8848e5b52c88402578b7f41a").readText()
        } catch (e: Exception) {
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string and returned
        return response
    }

    private suspend fun getData(result: String) {
        withContext(Dispatchers.Main) {


            val jsonObject = JSONObject(result)

            Log.d("suss", "getData: ")


            val main = jsonObject.getJSONObject("main")
            val sys = jsonObject.getJSONObject("sys")
            val weather = jsonObject.getJSONArray("weather").getJSONObject(0)

            val addresss = jsonObject.getString("name") + ", " + sys.getString("country")
            val updatedAt = jsonObject.getLong("dt")
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val weatherDescription = weather.getString("description")
            val temperature = main.getDouble("temp")
            var tem1 = (temperature - 273.15).toInt()
            Log.d("$tem1", "deg: ")
            val temMin =  main.getDouble("temp_min")
            val temMin1 = (temMin - 273.15).toInt()
            val temMax =  main.getDouble("temp_max")
            val temMax1 = (temMax - 273.15).toInt()

            val wind1 = jsonObject.getJSONObject("wind")
            val windSpeed = wind1.getString("speed")


            var sunset = sys.getLong("sunset")
            val sunrise = sys.getLong("sunrise")

            val pressure1 = main.getString("pressure")
            val humidity1 = main.getString("humidity")



            address.text = addresss
            updated_at.text = updatedAtText
            status.text = weatherDescription
            temp.text = tem1.toString()+"°C"
            temp_max.text ="high: " +  temMax1+"°C"
            temp_min.text ="low: " +  temMin1+"°C"


         wind.text = windSpeed.toString()
            sunset_At.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            sunrise_At.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))

            pressure.text = pressure1
            humidity.text = humidity1


        }
    }

    fun showDialog() {

        val builder = AlertDialog.Builder(this,R.style.nightDilogTheme)
        //  set title for alert dialog
        builder.setTitle("search using zip code ")

        val input = EditText(this)
        input.hint = "Enter the zip code  "
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)


        //performing positive action
        builder.setPositiveButton("submit") { dialogInterface, which ->
          Log.i("button","show button")
           userInput = input.text.toString()


            if (userInput.isNotEmpty()){
                zipCode = userInput.toInt()
                requestAPI()
            }
            else {
                Toast.makeText(this, "please enter the zip code ", Toast.LENGTH_SHORT).show()
            }


        }
        builder.setNegativeButton("CANCEL"){dialogInterface, which ->}
       /// builder.show()
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        // alertDialog.setCancelable(false)
        alertDialog.show()
    }

}
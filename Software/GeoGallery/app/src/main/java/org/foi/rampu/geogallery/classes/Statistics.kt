package org.foi.rampu.geogallery.classes

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.foi.rampu.geogallery.R

object Statistics {

    var numberPhotos : Int = 0
    var numberVideos : Int = 0

    var countriesPhotos : MutableMap<String,Int> = mutableMapOf()
    var citiesPhotos : MutableMap<String,Int> = mutableMapOf()
    var countriesVideos : MutableMap<String,Int> = mutableMapOf()
    var citiesVideos : MutableMap<String,Int> = mutableMapOf()


    fun showDialog(context : Context)
    {
        val newTaskDialogView = LayoutInflater
            .from(context)
            .inflate(R.layout.statistics_dialog, null)

        Log.i("STAT", numberPhotos.toString() + " " + numberVideos.toString())

        val tvNumberVideos = newTaskDialogView.findViewById<TextView>(R.id.numberVideos)
        val tvNumberPhotos = newTaskDialogView.findViewById<TextView>(R.id.numberPhotos)
        val tvCountriesVideos = newTaskDialogView.findViewById<TextView>(R.id.countriesVideos)
        val tvCountriesPhotos = newTaskDialogView.findViewById<TextView>(R.id.countriesPhotos)
        val tvCitiesVideos = newTaskDialogView.findViewById<TextView>(R.id.citiesVideos)
        val tvCitiesPhotos = newTaskDialogView.findViewById<TextView>(R.id.citiesPhotos)
        val tvCitiesMedia = newTaskDialogView.findViewById<TextView>(R.id.citiesMedia)
        val tvCountriesMedia = newTaskDialogView.findViewById<TextView>(R.id.countriesMedia)

        tvNumberVideos.text = numberVideos.toString()
        tvNumberPhotos.text = numberPhotos.toString()

        findItemWithMax(tvCountriesVideos, countriesVideos, "videos")
        findItemWithMax(tvCountriesPhotos, countriesPhotos, "images")
        findItemWithMax(tvCountriesMedia,
            (countriesPhotos + countriesVideos) as MutableMap<String, Int>, "media")

        findItemWithMax(tvCitiesVideos, citiesVideos, "videos")
        findItemWithMax(tvCitiesPhotos, citiesPhotos, "images")

        var citiesMedia = (citiesPhotos.toList() + citiesVideos.toList())
            .groupBy({ it.first }, { it.second })
            .map { (key, values) -> key to values.sum() }
            .toMap()

        if (citiesMedia.isNotEmpty())
            findItemWithMax(tvCitiesMedia, citiesMedia as MutableMap<String, Int>, "media")
        else
            tvCitiesMedia.text = "No media taken yet"

        var countriesMedia = (countriesPhotos.toList() + countriesVideos.toList())
            .groupBy({ it.first }, { it.second })
            .map { (key, values) -> key to values.sum() }
            .toMap()

        if (citiesMedia.isNotEmpty())
            findItemWithMax(tvCountriesMedia, countriesMedia as MutableMap<String, Int>, "media")
        else
            tvCitiesMedia.text = "No media taken yet"


        AlertDialog.Builder(context)
            .setView(newTaskDialogView)
            .setTitle("Statistics")
            .show()


    }

    private fun findItemWithMax(tv : TextView, map : MutableMap<String,Int>, media : String)
    {
        try {
            tv.text = map.maxBy { it.value }.key
        }
        catch (e : java.util.NoSuchElementException)
        {
            tv.text = "No $media taken yet"
        }

    }

    fun updateStatistics(media : String)
    {
        val country = CurrentLocationInfo.locationInfo.value?.get("country")
        val city = CurrentLocationInfo.locationInfo.value?.get("city")
        Log.i("STAT", country.toString())

        if (media == "video")
        {
            updateStatisticsVideo(country!!, city!!)
        }
        else if (media == "photo")
        {
            updateStatisticsPhoto(country!!, city!!)
        }

    }


    fun updateStatisticsVideo(country : String, city : String)
    {
        Statistics.numberVideos++

        if (Statistics.countriesVideos.containsKey(country))
        {
            var value = Statistics.countriesVideos[country.toString()]
            if (value != null) {
                Statistics.countriesVideos[country.toString()] = value + 1
            }
        }
        else
            Statistics.countriesVideos[country.toString()] = 1


        if (Statistics.citiesVideos.containsKey(city))
        {
            var value = Statistics.citiesVideos[city.toString()]
            if (value != null) {
                Statistics.citiesVideos[city.toString()] = value + 1
            }
        }
        else
            Statistics.citiesVideos[city.toString()] = 1
    }

    fun updateStatisticsPhoto(country : String, city : String)
    {
        Statistics.numberPhotos++

        if (Statistics.countriesPhotos.containsKey(country))
        {
            var value = Statistics.countriesPhotos[country.toString()]
            if (value != null) {
                Statistics.countriesPhotos[country.toString()] = value + 1
            }
        }
        else
            Statistics.countriesPhotos[country.toString()] = 1

        if (Statistics.citiesPhotos.containsKey(city))
        {
            var value = Statistics.citiesPhotos[city.toString()]
            if (value != null) {
                Statistics.citiesPhotos[city.toString()] = value + 1
            }
        }
        else
            Statistics.citiesPhotos[city.toString()] = 1

    }

    fun getStatisticsFromSharedPrefs(context: Context)
    {
        context?.getSharedPreferences("statistics_preferences", Context.MODE_PRIVATE)?.apply {
            val statisticsString = getString("statistics_maps", "Nema")
            var statistics : Map<String,Map<String,Int>> = mapOf<String,Map<String,Int>>()
            if (statisticsString != "Nema")
                statistics = Json.decodeFromString<Map<String,Map<String,Int>>>(statisticsString!!)

            if (statistics.isNotEmpty())
            {
                countriesPhotos = statistics.get("countriesPhotos") as MutableMap<String, Int>
                countriesVideos = statistics.get("countriesVideos") as MutableMap<String, Int>
                citiesPhotos = statistics.get("citiesPhotos") as MutableMap<String, Int>
                citiesVideos = statistics.get("citiesVideos") as MutableMap<String, Int>
                Log.i("STAT", "get shared prefs " + countriesPhotos.toString() + " " + countriesVideos.toString()
                + " " + citiesVideos.toString() + " " + citiesPhotos.toString())
            }
            else Log.i("STAT", "get shared prefs :(")

            val statisticsString2 = getString("statistics_numbers", "Nema")
            var statistics2 : Map<String,Int> = mapOf<String,Int>()
            if (statisticsString2 != "Nema")
                statistics2 = Json.decodeFromString<Map<String,Int>>(statisticsString2!!)

            if (statistics2.isNotEmpty())
            {
                numberPhotos = statistics2.get("numberPhotos")!!
                numberVideos = statistics2.get("numberVideos")!!

                Log.i("STAT", "get shared prefs $numberVideos $numberPhotos")
            }
            else Log.i("STAT", "get shared prefs2 :(")
        }
    }

    fun saveStatisticsToSharedPrefs(context: Context)
    {
        context?.getSharedPreferences("statistics_preferences", Context.MODE_PRIVATE)?.apply {

            val mapMaps = mapOf<String, Map<String,Int>>(
                "countriesPhotos" to countriesPhotos,
                "countriesVideos" to countriesVideos,
                "citiesPhotos" to citiesPhotos,
                "citiesVideos" to citiesVideos
            )
            val mapNumbers = mapOf<String, Int>(
                "numberPhotos" to numberPhotos,
                "numberVideos" to numberVideos
            )

            edit().putString("statistics_maps", Json.encodeToString(mapMaps)).apply()
            edit().putString("statistics_numbers", Json.encodeToString(mapNumbers)).apply()

            Log.i("STAT", "map maps " + getString("statistics_maps", "Nema" ))
            Log.i("STAT", "map numbers " + getString("statistics_numbers", "Nema"))

        }
    }


}
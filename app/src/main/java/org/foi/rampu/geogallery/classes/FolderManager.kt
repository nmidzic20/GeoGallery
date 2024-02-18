package org.foi.rampu.geogallery.classes

import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.foi.rampu.geogallery.GalleryActivity
import org.foi.rampu.geogallery.HomeActivity
import org.foi.rampu.geogallery.R

class FolderManager(val activity: HomeActivity) {

    enum class ColourType(val color: Int) {
        RED(R.color.red),
        BLUE(R.color.blue),
        YELLOW(R.color.yellow),
        GREEN(R.color.green),
        ORANGE(R.color.orange),
        PURPLE(R.color.purple_200)
    }

    fun createFolderIconsCountries()
    {
        activity.viewBinding.ibtnFolderBack.visibility = View.GONE
        activity.viewBinding.ibtnAudio.visibility = View.VISIBLE
        activity.viewBinding.ibtnCamera.visibility = View.VISIBLE
        activity.viewBinding.ibtnLocation.visibility = View.VISIBLE
        activity.viewBinding.ibtnGoogleMaps.visibility = View.VISIBLE

        //prevent country folder duplication for e.g. items like {Croatia, Osijek, ...} and {Croatia, Zagreb, ...}
        //which are distinct items in MutableSet, but would create two folders Croatia since here we care only
        //about country names
        val locationsList = mutableListOf<String>()
        AllLocationsInfo.savedLocationInfo.forEach { locationsList.add(it.country) }
        val locationsSet = locationsList.toSet()

        Log.i("DATA", "locationsSet $locationsSet")

        for (country in locationsSet)
        {
            val (iv, tv) = createFolderIcon(country, LocationCategory.COUNTRY)
            iv.setOnClickListener {

                activity.viewBinding.gridLayout.removeAllViews()

                createFolderIconCities(country)
            }
        }
    }

    fun createFolderIconCities(country : String)
    {
        activity.viewBinding.ibtnFolderBack.visibility = View.VISIBLE
        activity.viewBinding.ibtnAudio.visibility = View.GONE
        activity.viewBinding.ibtnCamera.visibility = View.GONE
        activity.viewBinding.ibtnLocation.visibility = View.GONE
        activity.viewBinding.ibtnGoogleMaps.visibility = View.GONE

        activity.viewBinding.ibtnFolderBack.setOnClickListener {

            //pobrisat foldere imena gradova
            activity.viewBinding.gridLayout.removeAllViews()

            createFolderIconsCountries()
        }

        val locationsOfCountryList = AllLocationsInfo.savedLocationInfo.filter { it.country == country }
        val locationsOfCountry = locationsOfCountryList.toSet()

        //now that we've found all cities belonging to this country, filter so that no duplicate cities
        //in case we took media in different streets of same city, which is still two distinct locations in set
        //but here we care only about city names
        val locationsList = mutableListOf<String>()
        locationsOfCountry.forEach { locationsList.add(it.city) }
        val locationsSet = locationsList.toSet()

        for (city in locationsSet)
        {
            val (iv, tv) = createFolderIcon(city, LocationCategory.CITY)
            iv.setOnClickListener {

                activity.viewBinding.gridLayout.removeAllViews()

                createFolderIconStreets(city, country)
            }
        }

    }

    fun createFolderIconStreets(city: String, country : String)
    {
        activity.viewBinding.ibtnFolderBack.setOnClickListener {

            //pobrisat foldere imena ulica
            activity.viewBinding.gridLayout.removeAllViews()

            createFolderIconCities(country)
        }

        val locationsOfCityList = AllLocationsInfo.savedLocationInfo.filter { it.city == city }
        val locationsOfCity = locationsOfCityList.toSet()

        for (location in locationsOfCity)
        {
            val (iv, tv) = createFolderIcon(location.street, LocationCategory.STREET)
            iv.setOnClickListener {

                //from the street folder open media gallery, but include city name into message
                //so that media can be sorted by folder (street) name, while wiki information will use city name
                //to show info about that city
                val intent = Intent(activity, GalleryActivity::class.java)
                val poruka : String = tv.text.toString() + "_" + city
                intent.putExtra("FOLDER_NAME", poruka)
                activity.startActivity(intent)

            }
        }
    }


    fun createFolderIcon(locationName: String, locationCategory: LocationCategory) : Pair<ImageView, TextView>
    {
        val layout = activity.viewBinding.gridLayout as ViewGroup
        val linearLayout = setLayout()

        val ivFolder = createFolderImage()
        val tvFolderName = createLocationName(locationName)

        ivFolder.contentDescription = locationCategory.toString()//tvFolderName.text.toString()
        tvFolderName.contentDescription = locationCategory.toString()//tvFolderName.text.toString()
        tvFolderName.height = 100


        linearLayout.addView(ivFolder)
        linearLayout.addView(tvFolderName)

        layout.addView(linearLayout)

        return ivFolder to tvFolderName
    }

    fun setLayout(): LinearLayout
    {
        val linearLayout = LinearLayout(activity)
        linearLayout.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        linearLayout.orientation = LinearLayout.VERTICAL

        return linearLayout
    }

    fun createFolderImage() : ImageView
    {
        val ivFolder = ImageView(activity)
        ivFolder.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        ivFolder.layoutParams.height = 200
        ivFolder.layoutParams.width = 250
        ivFolder.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_baseline_folder))

        colourFolder(ivFolder)

        return ivFolder
    }

    fun createLocationName(locationName : String): TextView
    {
        val tvFolderName = TextView(activity)
        tvFolderName.text = locationName
        tvFolderName.gravity = Gravity.CENTER
        tvFolderName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
        return tvFolderName
    }

    fun colourFolder(ivFolder : ImageView)
    {
        var index = getFolderIconsCount() % ColourType.values().size

        var colorId = ColourType.values()[index].color
        val color = activity.resources.getColor(colorId)

        ivFolder.setColorFilter(color)
    }

    fun getFolderIconsCount() : Int
    {
        return activity.viewBinding.gridLayout.childCount
    }

}
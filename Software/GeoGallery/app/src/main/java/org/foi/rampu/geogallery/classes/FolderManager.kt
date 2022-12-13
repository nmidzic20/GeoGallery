package org.foi.rampu.geogallery.classes

import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AbsListView
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


    fun createFolderIcon(locationName: String)
    {
        val layout = activity.viewBinding.gridLayout as ViewGroup
        val linearLayout = setLayout()

        val ivFolder = createFolderImage()
        val tvFolderName = createLocationName(locationName)

        ivFolder.tag = tvFolderName.text.toString()

        ivFolder.setOnClickListener {
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra("FOLDER_NAME", tvFolderName.text)
            activity.startActivity(intent)
        }

        linearLayout.addView(ivFolder)
        linearLayout.addView(tvFolderName)

        layout.addView(linearLayout)
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

    fun checkIfFolderIconExistsForLocation(location : String): Boolean
    {
        val folderIcon : ImageView? = activity.viewBinding.gridLayout.findViewWithTag(location)
        return folderIcon != null
    }

    fun getFolderIconsCount() : Int
    {
        return activity.viewBinding.gridLayout.childCount
    }

}
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

    fun createFolderIcon(index : Int)
    {
        val layout = activity.viewBinding.gridLayout as ViewGroup

        val linearLayout = LinearLayout(activity)
        linearLayout.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        linearLayout.orientation = LinearLayout.VERTICAL

        val ivFolder = createFolderImage(index)
        val tvFolderName = createLocationName()

        ivFolder.tag = tvFolderName.text.toString()

        linearLayout.addView(ivFolder)
        linearLayout.addView(tvFolderName)

        layout.addView(linearLayout)
    }

    fun createFolderImage(index : Int) : ImageView
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

        ivFolder.setOnClickListener {
            val intent = Intent(activity, GalleryActivity::class.java)
            activity.startActivity(intent)
        }

        colourFolder(ivFolder, index)

        return ivFolder
    }

    fun createLocationName(): TextView
    {
        val tvFolderName = TextView(activity)
        tvFolderName.text = "Lokacija"
        tvFolderName.gravity = Gravity.CENTER
        tvFolderName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
        return tvFolderName
    }

    fun colourFolder(ivFolder : ImageView, i : Int)
    {
        var index = i % ColourType.values().size

        var colorId = ColourType.values()[index].color
        val color = activity.resources.getColor(colorId)

        ivFolder.setColorFilter(color)
    }

    fun checkIfFolderIconExistsForLocation(location : String): Boolean
    {
        val folderIcon : ImageView? = activity.viewBinding.gridLayout.findViewWithTag(location)
        return folderIcon != null
    }
}
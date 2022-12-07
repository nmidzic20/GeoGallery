package org.foi.rampu.geogallery.classes

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val layout = activity.findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val ivFolder = ImageView(activity)
        ivFolder.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        ivFolder.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_baseline_folder))

        colourFolder(ivFolder, index)

        ivFolder.layoutParams.height = 200
        ivFolder.layoutParams.width = 200

        ivFolder.setOnClickListener {
            val intent = Intent(activity, GalleryActivity::class.java)
            activity.startActivity(intent)
        }

        layout.addView(ivFolder)

    }

    fun colourFolder(ivFolder : ImageView, i : Int)
    {
        var index = i % ColourType.values().size

        var colorId = ColourType.values()[index].color
        val color = activity.resources.getColor(colorId)

        ivFolder.setColorFilter(color)
    }
}
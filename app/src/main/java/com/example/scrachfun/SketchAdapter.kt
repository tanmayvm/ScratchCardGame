package com.example.scrachfun

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.io.IOException

class SketchAdapter(private val context: Context, private val sketches: List<String>) : BaseAdapter() {

    override fun getCount(): Int = sketches.size

    override fun getItem(position: Int): Any = sketches[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.sketch_grid_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val sketchPath = sketches[position]

        if (sketchPath == "None") {
            viewHolder.sketchImage.setImageResource(android.R.drawable.ic_menu_gallery)
        } else {
            try {
                val thumbnail = decodeSampledBitmapFromAssets(context, sketchPath, 100, 100)
                viewHolder.sketchImage.setImageBitmap(thumbnail)
            } catch (e: IOException) {
                e.printStackTrace()
                viewHolder.sketchImage.setImageResource(android.R.drawable.ic_dialog_alert)
            }
        }

        return view
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    @Throws(IOException::class)
    private fun decodeSampledBitmapFromAssets(context: Context, assetPath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        context.assets.open(assetPath).use { inputStream ->
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false

            context.assets.open(assetPath).use { secondInputStream ->
                return BitmapFactory.decodeStream(secondInputStream, null, options)
            }
        }
    }

    private class ViewHolder(view: View) {
        val sketchImage: ImageView = view.findViewById(R.id.sketch_image)
    }
}

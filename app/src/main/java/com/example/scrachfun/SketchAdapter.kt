package com.example.scrachfun

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
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

        val sketchName = sketches[position]
        viewHolder.sketchName.text = sketchName.substringBeforeLast(".") // Remove extension

        if (sketchName == "None") {
            viewHolder.sketchImage.setImageResource(R.drawable.ic_cat) // A placeholder for "None"
        } else {
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open("sketches/$sketchName")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                viewHolder.sketchImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                // Optionally set a default error image
                viewHolder.sketchImage.setImageResource(R.drawable.ic_cat) // Placeholder
            }
        }

        return view
    }

    private class ViewHolder(view: View) {
        val sketchImage: ImageView = view.findViewById(R.id.sketch_image)
        val sketchName: TextView = view.findViewById(R.id.sketch_name)
    }
}
package com.example.scrachfun

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ColorAdapter(private val context: Context, private val colors: List<String>) : BaseAdapter() {

    override fun getCount(): Int = colors.size

    override fun getItem(position: Int): Any = colors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(85, 85)
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        val colorString = colors[position]
        val color = Color.parseColor(colorString)

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setStroke(2, Color.BLACK)
        }
        imageView.setImageDrawable(drawable)
        imageView.tag = colorString

        return imageView
    }
}
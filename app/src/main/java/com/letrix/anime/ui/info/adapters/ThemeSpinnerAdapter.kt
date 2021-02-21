package com.letrix.anime.ui.info.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.letrix.anime.data.AnimeThemes
import com.letrix.anime.utils.Util


class ThemeSpinnerAdapter(context: Context, list: List<AnimeThemes.Theme.Entry.Video>) :
    ArrayAdapter<AnimeThemes.Theme.Entry.Video>(context, android.R.layout.simple_spinner_dropdown_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowView(convertView, position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowView(convertView, position, parent)
    }

    private fun rowView(convertView: View?, position: Int, parent: ViewGroup): View {
        val item = getItem(position)
        val holder: ItemHolder
        var view = convertView
        if (view == null) {
            holder = ItemHolder()
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
            holder.txtTitle = view.findViewById<View>(android.R.id.text1) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ItemHolder
        }
        if (item != null) {
            holder.txtTitle?.text = Util.getVideoTitle(item)
        }
        return view!!
    }

    private inner class ItemHolder {
        var txtTitle: TextView? = null
    }
}
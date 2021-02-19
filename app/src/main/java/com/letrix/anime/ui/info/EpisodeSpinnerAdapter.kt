package com.letrix.anime.ui.info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.letrix.anime.R


class EpisodeSpinnerAdapter(context: Context, list: List<InfoFragment.EpisodeList>) :
    ArrayAdapter<InfoFragment.EpisodeList?>(context, android.R.layout.simple_spinner_dropdown_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowView(convertView, position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowView(convertView, position, parent)
    }

    private fun rowView(convertView: View?, position: Int, parent: ViewGroup): View {
        val item: InfoFragment.EpisodeList? = getItem(position)
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
            holder.txtTitle?.text = context.getString(R.string.episodes_selector, item.list.first().episode, item.list.last().episode)
        }
        return view!!
    }

    private inner class ItemHolder {
        var txtTitle: TextView? = null
    }
}
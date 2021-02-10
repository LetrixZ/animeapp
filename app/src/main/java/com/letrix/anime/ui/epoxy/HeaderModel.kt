package com.letrix.anime.ui.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.letrix.anime.R
import com.letrix.anime.utils.KotlinHolder

@EpoxyModelClass(layout = R.layout.item_title)
abstract class HeaderModel : EpoxyModelWithHolder<HeaderModel.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    override fun bind(holder: Holder) {
        holder.titleView.text = title
    }

    override fun shouldSaveViewState(): Boolean = true

    inner class Holder : KotlinHolder() {

        val titleView by bind<TextView>(R.id.item_header)

        /*var titleView: TextView? = null

        override fun bindView(itemView: View) {
            titleView = itemView.findViewById(R.id.item_header)
        }*/

    }
}
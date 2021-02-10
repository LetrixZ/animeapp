package com.letrix.anime.ui.epoxy

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.imageview.ShapeableImageView
import com.letrix.anime.R
import com.letrix.anime.utils.ImageLoader
import com.letrix.anime.utils.KotlinHolder


@EpoxyModelClass(layout = R.layout.item_anime, useLayoutOverloads = true)
abstract class AnimeModel : EpoxyModelWithHolder<AnimeModel.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    var extra: String? = ""

    @EpoxyAttribute
    lateinit var poster: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: View.OnClickListener

    override fun shouldSaveViewState(): Boolean = true

    override fun bind(holder: Holder) {
        holder.titleView.text = title
        ImageLoader.loadImage(poster, holder.posterView)
        holder.rootView.setOnClickListener(clickListener)
        if (extra?.isNotEmpty() == true) {
            holder.extraView?.text = extra
            holder.extraView?.isVisible = true
        }
    }

    class Holder : KotlinHolder() {
        val rootView by bind<ConstraintLayout>(R.id.clickable_layout)
        val titleView by bind<TextView>(R.id.title)
        val posterView by bind<ShapeableImageView>(R.id.poster)

        var extraView: TextView? = null

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            extraView = itemView.findViewById(R.id.extra)
        }
    }

}
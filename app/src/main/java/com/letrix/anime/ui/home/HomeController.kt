package com.letrix.anime.ui.home

import android.content.Context
import android.net.Uri
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.ui.AnimeAdapter
import com.letrix.anime.ui.epoxy.AnimeModel_
import com.letrix.anime.ui.epoxy.header
import com.letrix.anime.utils.Util
import kotlin.math.ln
import kotlin.math.pow

class HomeController(private val context: Context, private val clickListener: AnimeAdapter.ItemClickListener) :
    TypedEpoxyController<List<Anime.List>>() {

    override fun buildModels(data: List<Anime.List>?) {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
        data?.forEachIndexed { index, list ->
            header {
                id(list.title)
                title(Util.getTitle(list.title, context))
            }
            carousel {
                id(list.title + "_car")
                models(list.list.map { anime ->
                    when (index) {
                        0 -> AnimeModel_()
                            .withEpisodeThumbnailLayout()
                            .id(anime.id)
                            .title(anime.title)
                            .extra(context.getString(R.string.episode_n, anime.latestEpisode))
                            .poster(Uri.parse(anime.poster))
                            .clickListener { _ ->
                                clickListener.onClick(anime)
                            }
                        1 -> AnimeModel_()
                            .id(anime.id)
                            .title(anime.title)
                            .extra(context.getString(R.string.episode_n, anime.latestEpisode))
                            .poster(Uri.parse(anime.poster))
                            .clickListener { _ ->
                                clickListener.onClick(anime)
                            }
                        2 -> AnimeModel_()
                            .id(anime.id)
                            .title(anime.title)
                            .extra(context.getString(R.string.likes_n, getFormattedNumber(anime.likes!!)))
                            .poster(Uri.parse(anime.poster))
                            .clickListener { _ ->
                                clickListener.onClick(anime)
                            }
                        else -> AnimeModel_()
                            .id(anime.id)
                            .title(anime.title)
                            .poster(Uri.parse(anime.poster))
                            .clickListener { _ ->
                                clickListener.onClick(anime)
                            }
                    }
                })

            }
        }
    }

    private fun getFormattedNumber(count: Int): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1]).replace(",", ".")
    }


}
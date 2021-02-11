package com.letrix.anime.ui.home

import android.content.Context
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.carousel
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.ui.epoxy.AnimeModel_
import com.letrix.anime.ui.epoxy.header
import com.letrix.anime.utils.Util
import kotlin.math.ln
import kotlin.math.pow

class HomeController(private val context: Context, private val clickListener: ItemClickListener) :
    Typed2EpoxyController<List<Anime.List>, Anime.List>() {

    private fun thumbnailModel(anime: Anime) = AnimeModel_()
        .withEpisodeThumbnailLayout()
        .id(anime.id)
        .title(anime.title)
        .extra(context.getString(R.string.episode_n, anime.latestEpisode))
        .poster(anime.poster)
        .clickListener { _ ->
            clickListener.onEpisode(anime, anime.latestEpisode!!)

        }

    private fun episodeModel(anime: Anime) = AnimeModel_()
        .id(anime.id)
        .title(anime.title)
        .extra(context.getString(R.string.episode_n, anime.latestEpisode))
        .poster(anime.poster)
        .clickListener { _ ->
            clickListener.onEpisode(anime, anime.latestEpisode!!)
        }

    private fun likesModel(anime: Anime) = AnimeModel_()
        .id(anime.id)
        .title(anime.title)
        /*.extra(context.getString(R.string.likes_n, getFormattedNumber(anime.likes)))*/
        .poster(anime.poster)
        .clickListener { _ ->
            clickListener.onAnime(anime)
        }

    private fun regularModel(anime: Anime) = AnimeModel_()
        .id(anime.id)
        .title(anime.title)
        .poster(anime.poster)
        .clickListener { _ ->
            clickListener.onAnime(anime)
        }

    override fun buildModels(data1: List<Anime.List>?, data2: Anime.List?) {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
        if (data2 != null) {
            val newList = data1 as ArrayList
            newList.add(0, data2)
        }
        data1?.forEachIndexed { index, list ->
            header {
                id(list.title)
                title(Util.getTitle(list.title, context))
            }
            carousel {
                id(list.title + "_car")
                if (data2 != null) {
                    models(list.list.map { anime ->
                        when (index) {
                            0 -> episodeModel(anime)
                            1 -> thumbnailModel(anime)
                            2 -> episodeModel(anime)
                            3 -> likesModel(anime)
                            else -> regularModel(anime)
                        }
                    })
                } else
                    models(list.list.map { anime ->
                        when (index) {
                            0 -> thumbnailModel(anime)
                            1 -> episodeModel(anime)
                            2 -> likesModel(anime)
                            else -> regularModel(anime)
                        }
                    })
            }
        }
    }


    interface ItemClickListener {
        fun onAnime(item: Anime)
        fun onEpisode(item: Anime, episode: Int)
    }

}
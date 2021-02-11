package com.letrix.anime.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
import com.letrix.anime.ui.RoomViewModel
import com.letrix.anime.ui.info.ServerBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ServerBottomSheet.ItemClickListener {

    private val roomViewModel by viewModels<RoomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onItemClick(serverList: List<Server>, server: Int, anime: Anime, episode: Int) {
        roomViewModel.getEpisode("${anime.id}-$episode").observe(this, {
            var episodeDb = it
            if (episodeDb == null) {
                episodeDb = com.letrix.anime.room.Anime.Episode(
                    animeId = anime.id,
                    episode = episode,
                    progress = 0,
                    duration = 1000
                )
            }
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.playerFragment,
                bundleOf("servers" to serverList.toTypedArray(), "selected" to server, "anime" to anime, "episode" to episodeDb)
            )
        })
    }
}
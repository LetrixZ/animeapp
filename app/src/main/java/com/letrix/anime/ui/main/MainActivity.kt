package com.letrix.anime.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
import com.letrix.anime.ui.info.ServerBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ServerBottomSheet.ItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onItemClick(serverList: List<Server>, server: Int, anime: Anime, episode: Int) {
        findNavController(R.id.nav_host_fragment).navigate(
            R.id.playerFragment,
            bundleOf("servers" to serverList.toTypedArray(), "selected" to server, "anime" to anime, "episode" to episode)
        )
    }
}
package com.letrix.anime.ui.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
import com.letrix.anime.databinding.BottomSheetServerBinding
import com.letrix.anime.network.RestConfig
import com.letrix.anime.utils.Status.*
import com.letrix.anime.utils.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import timber.log.Timber.d
import java.io.*

@AndroidEntryPoint
class ServerBottomSheet : BottomSheetDialogFragment(), ServerAdapter.ServerClickListener {

    private lateinit var binding: BottomSheetServerBinding
    private var serverList: List<Server>? = null
    private val args by navArgs<ServerBottomSheetArgs>()
    private val viewModel by viewModels<InfoViewModel>()

    override fun onStart() {
        super.onStart() //this forces the sheet to appear at max height even on landscape
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_server, container, false)
    }

    private val serverAdapter = ServerAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = BottomSheetServerBinding.bind(view)

        binding.recyclerView.apply {
            adapter = serverAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        if (savedInstanceState == null) {
            getContent()
        } else {
            serverList = Gson().fromJson(savedInstanceState.getString("list"), object : TypeToken<List<Server>>() {}.type) as List<Server>
            setView(serverList)
        }
    }

    private fun getContent() {
        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            val list: List<Server>? = getServers()?.map { // is a shorter way to write IntRange(0, 10)
                async { // async means "concurrently", context goes here
                    when (it.name) {
                        "Okru" -> getOkru(it.mirrors[0].url)
                        "Fembed" -> getFembed(it.mirrors[0].url)
                        else -> it
                    }
                }
            }?.awaitAll()?.filterNotNull()
            serverList = list
            setView(list)
        }
    }

    private fun setView(list: List<Server?>?) {
        serverAdapter.submitList(list)
        binding.progressBar.isVisible = false
        if (list != null) {
            binding.noServers.isVisible = list.isEmpty()
        } else {
            binding.noServers.isVisible = true
            binding.noServers.text = getString(R.string.server_error)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("list", Gson().toJson(serverList))
    }

    override fun onServer(server: Int, mirror: Int) {
        serverList?.get(server)?.index = mirror
        clickListener?.onItemClick(serverList!!, server, args.anime, args.episode)
        dismiss()
    }

    private var clickListener: ItemClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            clickListener = context as ItemClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }

    interface ItemClickListener {
        fun onItemClick(serverList: List<Server>, server: Int, anime: Anime, episode: Int)
    }

    private suspend fun getServers(): List<Server>? {
        return withContext(IO) {
            val response = viewModel.servers(args.anime.id, args.episode)
            return@withContext when (response.status) {
                SUCCESS -> {
                    response.data
                }
                ERROR -> {
                    d(response.message)
                    null
                }
                else -> null
            }
        }
    }

    private suspend fun getFembed(url: String): Server? {
        return withContext(IO) {
            var count = 0
            val maxTries = 3
            var server: Server? = null
            while (count < maxTries) {
                try {
                    val sourceResponse = Jsoup.connect(url)
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19")
                        .ignoreContentType(true)
                        .post().body().text()
                    val apiResponse = Jsoup.connect(RestConfig.FEMBED_API)
                        .timeout(30000)
                        .data("source", Util.encodeResponse(sourceResponse))
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute().body()
                    server = Gson().fromJson(apiResponse, Server::class.java)
                    break
                } catch (e: Exception) {
                    d(e)
                    server = null
                    count++
                }
            }
            return@withContext server
        }
    }

    private suspend fun getOkru(url: String): Server? {
        return withContext(IO) {
            var count = 0
            val maxTries = 3
            var server: Server? = null
            while (count < maxTries) {
                try {
                    val sourceResponse = Jsoup.connect(url)
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19")
                        .ignoreContentType(true)
                        .parser(Parser.htmlParser()).get().toString()
                    val apiResponse = Jsoup.connect(RestConfig.OKRU_API)
                        .timeout(30000)
                        .data("source", Util.encodeResponse(sourceResponse))
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute().body()
                    server = Gson().fromJson(apiResponse, Server::class.java)
                    break
                } catch (e: Exception) {
                    d(e)
                    d(e)
                    server = null
                    count++
                }
            }
            return@withContext server
        }
    }

}
package com.letrix.anime.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentHomeBinding
import com.letrix.anime.ui.info.ServerBottomSheet
import com.letrix.anime.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HomeController.ItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()
    private var controller: HomeController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        controller = HomeController(requireActivity(), this)
        controller?.onRestoreInstanceState(viewModel.bundle)

        setupObserver()

        binding.search.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionFragmentHomeToSearchFragment())
        }
    }


    private fun setupEpoxy(lists: List<Anime.List>) {
        binding.epoxyRecyclerView.setController(controller!!)
        controller?.setData(lists)
    }

    private fun setupObserver() {
        viewModel.home.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.isVisible = false
                    if (it.data != null) {
                        /*setupRecycler(it.data)*/
                        setupEpoxy(it.data)
                    }
                }
                Status.LOADING -> {
                    binding.progressBar.isVisible = true
                }
                Status.ERROR -> {
                    binding.progressBar.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = it.message
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.bundle == null) viewModel.bundle = Bundle()
        controller?.onSaveInstanceState(viewModel.bundle!!)
    }

    override fun onAnime(item: Anime) {
        findNavController().navigate(HomeFragmentDirections.actionFragmentHomeToInfoFragment(item.id))
    }

    override fun onEpisode(item: Anime, episode: Int) {
        ServerBottomSheet().also {
            it.arguments = bundleOf("anime" to item, "episode" to episode)
            it.show(childFragmentManager, null)
        }
    }

}
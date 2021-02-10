package com.letrix.anime.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.letrix.anime.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _info: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            run {
                d("${destination.label} id: ${destination.id} $arguments, _info = $_info, backStack.count = ${navController.backStack.count()}")
                if (destination.label == "InfoFragment" && navController.backStack.count() <= 4) _info = destination.id
            }
        }*/
    }

    override fun onBackPressed() {
        /*val navController = findNavController(R.id.nav_host_fragment)
        if (navController.currentDestination?.label == "InfoFragment" && navController.previousBackStackEntry?.destination?.label == "GenreFragment") {
            navController.popBackStack()
            while (navController.currentDestination?.label != "InfoFragment" && navController.previousBackStackEntry?.destination?.label != "GenreFragment") {
                navController.popBackStack()
            }
            navController.popBackStack(R.id.infoFragment, false)
        } else {
            super.onBackPressed()
        }*/
        super.onBackPressed()
    }
}
package com.letrix.anime

import android.app.Application
import com.letrix.anime.BuildConfig
import com.letrix.anime.utils.TimberTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(TimberTree())
        }
    }
}
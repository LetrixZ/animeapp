package com.letrix.anime.di

import com.google.gson.GsonBuilder
import com.letrix.anime.network.JkService
import com.letrix.anime.network.RestConfig
import com.letrix.anime.network.jikan.JikanService
import com.letrix.anime.network.miranime.MiranimeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideJkService(client: OkHttpClient): JkService {
        return Retrofit.Builder()
            .baseUrl(RestConfig.JKANIME_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(JkService::class.java)
    }

    @Singleton
    @Provides
    fun provideMiranimeService(client: OkHttpClient): MiranimeService {
        return Retrofit.Builder()
            .baseUrl(RestConfig.MIRANIME_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(MiranimeService::class.java)
    }

    @Singleton
    @Provides
    fun provideJikanService(client: OkHttpClient): JikanService {
        return Retrofit.Builder()
            .baseUrl(RestConfig.JIKAN_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(JikanService::class.java)
    }

    interface OnConnectionTimeoutListener {
        fun onConnectionTimeout()
    }

}
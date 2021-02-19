package com.letrix.anime.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    /*@Singleton
    @Provides
    fun provideAppDb(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            )
            .fallbackToDestructiveMigration()
            .build()
    }*/

}
package com.anonymous.appilogue.di

import android.content.Context
import androidx.room.Room
import com.anonymous.appilogue.persistence.AppDatabase
import com.anonymous.appilogue.persistence.InstalledAppDao
import com.anonymous.appilogue.persistence.converter.BitmapTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DBModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        bitmapTypeConverter: BitmapTypeConverter
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "Appilogue.db")
            .fallbackToDestructiveMigration()
            .addTypeConverter(bitmapTypeConverter)
            .build()

    @Provides
    @Singleton
    fun provideInstalledAppDao(appDatabase: AppDatabase): InstalledAppDao =
        appDatabase.installedAppDao()

    @Provides
    @Singleton
    fun provideBitmapTypeConverter(): BitmapTypeConverter = BitmapTypeConverter()
}
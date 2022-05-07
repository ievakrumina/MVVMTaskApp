package com.ik.mvvmtaskapp.di

import android.content.Context
import androidx.room.Room
import com.ik.mvvmtaskapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object TestAppModule {

  @Provides
  @Singleton
  @Named("test_db")
  fun provideInMemoryDb(@ApplicationContext context: Context) =
    Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
    .allowMainThreadQueries()
    .build()
}
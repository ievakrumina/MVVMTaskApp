package com.ik.mvvmtaskapp.di

import android.app.Application
import androidx.room.Room
import com.ik.mvvmtaskapp.data.TaskDao
import com.ik.mvvmtaskapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton
import com.ik.mvvmtaskapp.data.TaskRepository as TaskRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideDatabase(
    app: Application,
    callback: TaskDatabase.Callback
  ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
    .fallbackToDestructiveMigration()
    .addCallback(callback)
    .build()

  @Provides
  @Singleton
  fun provideTaskDao(db: TaskDatabase) = db.taskDao()

  @ApplicationScope
  @Provides
  @Singleton
  fun provideApplicationScope() = CoroutineScope(SupervisorJob())

  @Provides
  @Singleton
  fun provideTaskRepository(dao: TaskDao) = TaskRepository(dao)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
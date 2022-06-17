package com.ik.mvvmtaskapp.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Rule sample from https://developer.android.com/kotlin/coroutines/test
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
  private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(), ) : TestWatcher() {
  override fun starting(description: Description) {
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description) {
    Dispatchers.resetMain()
  }
}
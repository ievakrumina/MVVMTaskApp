package com.ik.mvvmtaskapp.ui.addedittasks

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AddEditFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testNewTaskState() {
       // TODO
    }

    @Test
    fun testEditTaskState() {
        // TODO
    }
}
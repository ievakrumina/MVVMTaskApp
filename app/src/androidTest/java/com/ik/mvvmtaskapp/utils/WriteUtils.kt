package com.ik.mvvmtaskapp.utils

import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId

@VisibleForTesting
fun Int.typeText(text: String) {
    onView(withId(this)).perform(
        click(),
        replaceText(text),
        closeSoftKeyboard()
    )
}
package com.ik.mvvmtaskapp.utils

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

@VisibleForTesting
fun Int.clickWithId(parent: Matcher<View>? = null) {
    if (parent !=null) {
       onView(allOf(withId(this), isDescendantOfA(parent))).perform(click())
    } else {
        onView(withId(this)).perform(click())
    }
}

@VisibleForTesting
fun Int.clickOnMenuItem(textId: Int) {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    onView(allOf(withText(textId), isDescendantOfA(withId(this)))).perform(click())
}

@VisibleForTesting
fun Int.clickOnItemAt(position: Int): ViewInteraction =
    onView(UiInteractionUtils().withIndex(allOf(withId(this)), position)).perform(click())

@VisibleForTesting
fun Int.swipeToLeft(): ViewInteraction = onView(withId(this)).perform(swipeLeft())

@VisibleForTesting
fun Int.swipeToLeftAtPosition(position: Int): ViewInteraction =
    onView(UiInteractionUtils().withIndex(allOf(withId(this)),position))
        .perform(swipeLeft())
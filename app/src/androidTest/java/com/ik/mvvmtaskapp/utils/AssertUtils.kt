package com.ik.mvvmtaskapp.utils

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher

@VisibleForTesting
fun Int.assertVisibility(parentView: Matcher<View>? = null, visible: Boolean = true) {
    when (parentView) {
        null -> {
            if (visible) {
                onView(withId(this)).check(matches(isDisplayed()))
            } else {
                onView(withId(this)).check(matches(not(isDisplayed())))
            }
        }
        else -> {
            if (visible) {
                onView(
                    allOf(
                        withId(this),
                        isDescendantOfA(parentView)
                    )
                ).check(matches(isDisplayed()))
            } else {
                onView(allOf(withId(this), isDescendantOfA(parentView))).check(matches(not(isDisplayed())))
            }
        }
    }
}

@VisibleForTesting
fun Int.assertVisibilityAtPosition(position: Int, visibility: Boolean = true) {
    when(visibility) {
        true -> {
            onView(UiInteractionUtils().withIndex(allOf(withId(this)),position))
                .check(matches(isDisplayed()))
        }
        false -> {
            onView(UiInteractionUtils().withIndex(allOf(withId(this)),position))
                .check(matches(not(isDisplayed())))
        }
    }
}

@VisibleForTesting
fun Int.assertNotExists(): ViewInteraction = onView(withId(this)).check(doesNotExist())

@VisibleForTesting
fun Int.assertContainsText(textId: Int): ViewInteraction =
    onView(allOf(withId(this), withText(textId))).check(matches(isDisplayed()))

@VisibleForTesting
fun Int.assertContainsText(text: String): ViewInteraction =
    onView(allOf(withId(this), withText(text))).check(matches(isDisplayed()))

@VisibleForTesting
fun Int.assertContainsTextAtPosition(textId: Int, position: Int): ViewInteraction =
    onView(UiInteractionUtils().withIndex(allOf(withId(this), withText(textId)), position)).check(
        matches(isDisplayed()))
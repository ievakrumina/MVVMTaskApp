package com.ik.mvvmtaskapp.utils

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher

@VisibleForTesting
open class AssertUtils {

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
          onView(allOf(withId(this), isDescendantOfA(parentView))).check(matches(isDisplayed()))
        } else {
          onView(allOf(withId(this), isDescendantOfA(parentView))).check(matches(not(isDisplayed())))
        }
      }
    }
  }

}
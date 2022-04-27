package com.ik.mvvmtaskapp.ui.tasks.robots

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.ik.mvvmtaskapp.R
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

object TaskListRobot {

  fun assertAddTaskButtonIsDisplayed() = R.id.fab_add_task.assertVisibility()

  fun Int.assertVisibility(parentView: Matcher<View>? = null, visible: Boolean = true) {
    when (parentView) {
      null -> {
        if (visible) {
          Espresso.onView(ViewMatchers.withId(this))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        } else {
          Espresso.onView(ViewMatchers.withId(this))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        }
      }
      else -> {
        if (visible) {
          Espresso.onView(
            CoreMatchers.allOf(
              ViewMatchers.withId(this),
              ViewMatchers.isDescendantOfA(parentView)
            )
          ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        } else {
          Espresso.onView(
            CoreMatchers.allOf(
              ViewMatchers.withId(this),
              ViewMatchers.isDescendantOfA(parentView)
            )
          ).check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        }
      }
    }
  }
}
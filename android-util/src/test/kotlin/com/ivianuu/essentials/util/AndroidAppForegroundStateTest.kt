/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.ComponentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AndroidAppForegroundStateTest {
  @Test fun testAndroidAppForegroundState() = runCancellingBlockingTest {
    val activities = MutableStateFlow<ComponentActivity?>(null)
    val collector = androidAppForegroundState(activities).testCollectIn(this)

    activities.emit(mockk())
    activities.emit(null)
    activities.emit(mockk())
    activities.emit(null)

    collector.values.shouldContainExactly(
      AppForegroundState.BACKGROUND,
      AppForegroundState.FOREGROUND,
      AppForegroundState.BACKGROUND,
      AppForegroundState.FOREGROUND,
      AppForegroundState.BACKGROUND
    )
  }
}

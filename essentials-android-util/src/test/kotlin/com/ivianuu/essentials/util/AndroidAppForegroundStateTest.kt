/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.test.*
import io.mockk.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AndroidAppForegroundStateTest {
  @Test fun testAndroidAppForegroundState() = runCancellingBlockingTest {
    val activities = MutableStateFlow<ComponentActivity?>(null)
    val collector = androidAppForegroundState(activities).testCollect(this)

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

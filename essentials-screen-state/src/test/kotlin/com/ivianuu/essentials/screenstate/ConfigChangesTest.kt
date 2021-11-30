/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.content.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
import io.mockk.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class ConfigChangesTest {
  @Test fun testConfigChanges() = runCancellingBlockingTest {
    lateinit var callback: ComponentCallbacks
    val appContext = mockk<AppContext> {
      every { registerComponentCallbacks(any()) } answers {
        callback = arg(0)
        Unit
      }
      every { unregisterComponentCallbacks(any()) } returns Unit
    }
    val collector = configChanges(appContext, dispatcher)
      .testCollect(this)

    callback.onConfigurationChanged(mockk())
    callback.onConfigurationChanged(mockk())
    callback.onConfigurationChanged(mockk())

    collector.values.shouldHaveSize(3)
  }
}

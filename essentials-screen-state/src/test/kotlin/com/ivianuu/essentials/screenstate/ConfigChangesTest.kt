/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.content.ComponentCallbacks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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

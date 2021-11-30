/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

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
class BroadcastsTest {
  @Test fun testBroadcasts() = runCancellingBlockingTest {
    lateinit var receiver: BroadcastReceiver
    val appContext = mockk<AppContext> {
      every { registerReceiver(any(), any()) } answers {
        receiver = arg(0)
        null
      }
      every { unregisterComponentCallbacks(any()) } returns Unit
    }
    val collector = broadcastsFactory(appContext, dispatcher)
      .invoke("action").testCollect(this)

    receiver.onReceive(appContext, Intent("action"))
    receiver.onReceive(appContext, Intent("action"))

    collector.values.shouldHaveSize(2)
  }
}

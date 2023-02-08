/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.BroadcastReceiver
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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
      .invoke("action").testCollectIn(this)

    receiver.onReceive(appContext, Intent("action"))
    receiver.onReceive(appContext, Intent("action"))

    collector.values.shouldHaveSize(2)
  }
}

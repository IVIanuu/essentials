/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForegroundActivityProviderStateTest {
  @Test fun testForegroundActivityState() = runCancellingBlockingTest {
    val foregroundState = foregroundActivityState
    lateinit var lifecycle: LifecycleRegistry
    val activity = object : ComponentActivity(), ForegroundActivityMarker {
      private var _lifecycle: Lifecycle? = null
      override fun getLifecycle(): Lifecycle {
        if (_lifecycle == null) {
          _lifecycle = LifecycleRegistry(this)
            .also { lifecycle = it }
        }
        return _lifecycle!!
      }

      override fun getViewModelStore(): ViewModelStore = ViewModelStore()
    }
    launch { foregroundActivityStateWorker(activity, dispatcher, foregroundState)() }
    val collector = foregroundState.testCollect(this)
    advanceUntilIdle()

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

    collector.values.shouldContainExactly(
      null,
      activity,
      null,
      activity,
      null
    )
  }
}

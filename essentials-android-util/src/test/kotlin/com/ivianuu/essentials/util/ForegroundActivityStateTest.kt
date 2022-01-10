/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.*
import androidx.lifecycle.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.test.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ForegroundActivityStateTest {
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

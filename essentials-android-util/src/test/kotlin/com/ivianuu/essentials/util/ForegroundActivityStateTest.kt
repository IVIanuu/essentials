/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import androidx.activity.*
import androidx.lifecycle.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
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

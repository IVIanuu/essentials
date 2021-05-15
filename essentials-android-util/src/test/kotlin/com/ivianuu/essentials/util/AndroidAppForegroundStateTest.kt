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
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
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

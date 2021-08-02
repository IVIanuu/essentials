/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.android.settings

import android.content.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AndroidSettingsStateTest {
  @Test fun testAndroidSettingsState() = runCancellingBlockingTest {
    var value = 0
    val contentChanges = EventFlow<Unit>()
    val adapter = object : AndroidSettingAdapter<Int> {
      override fun get(
        contentResolver: ContentResolver,
        name: String,
        type: AndroidSettingsType,
        defaultValue: Int
      ): Int = value

      override fun set(
        contentResolver: ContentResolver,
        name: String,
        type: AndroidSettingsType,
        _value: Int
      ) {
        value = _value
        contentChanges.tryEmit(Unit)
      }
    }
    val module = AndroidSettingModule<Int, Int>(
      "name",
      AndroidSettingsType.GLOBAL,
      value
    )
    val setting = module.dataStore(
      scope = this,
      adapter = adapter,
      dispatcher = coroutineContext.get(CoroutineDispatcher.Key)!!,
      contentChangesFactory = { contentChanges },
      contentResolver = mockk()
    )

    value shouldBe 0

    val stateCollector = setting.data.testCollect(this)
    advanceUntilIdle()

    // initial state
    stateCollector.values.shouldHaveSize(1)
    stateCollector.values[0] shouldBe 0

    // content changes
    value = 1
    contentChanges.tryEmit(Unit)
    stateCollector.values.shouldHaveSize(2)
    stateCollector.values[1] shouldBe 1

    // unrelated content changes
    contentChanges.tryEmit(Unit)
    stateCollector.values.shouldHaveSize(2)

    // updates
    setting.updateData { value + 1 }
    contentChanges.tryEmit(Unit)
    value shouldBe 2
    stateCollector.values.shouldHaveSize(3)
    stateCollector.values[2] shouldBe 2
  }
}
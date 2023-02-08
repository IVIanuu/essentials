/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.settings

import android.content.ContentResolver
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AndroidSettingsStateTest {
  @OptIn(ExperimentalStdlibApi::class)
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
      context = coroutineContext[CoroutineDispatcher.Key]!!,
      contentChangesFactory = { contentChanges },
      contentResolver = mockk()
    )

    value shouldBe 0

    val stateCollector = setting.data.testCollectIn(this)
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
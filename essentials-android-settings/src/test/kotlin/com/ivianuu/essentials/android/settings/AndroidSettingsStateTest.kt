package com.ivianuu.essentials.android.settings

import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AndroidSettingsStateTest {
  @Test
  fun testAndroidSettingsState() = runCancellingBlockingTest {
    var value = 0
    val contentChanges = EventFlow<Unit>()
    val adapter = object : AndroidSettingAdapter<Int> {
      override fun get(): Int = value
      override fun set(_value: Int) {
        value = _value
        contentChanges.tryEmit(Unit)
      }
    }
    val module = AndroidSettingModule<Int, Int>(
      "name",
      AndroidSettingsType.GLOBAL
    )
    val setting = module.dataStore(
      scope = this,
      adapter = adapter,
      dispatcher = coroutineContext.get(CoroutineDispatcher.Key) !!,
      contentChangesFactory = { contentChanges }
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
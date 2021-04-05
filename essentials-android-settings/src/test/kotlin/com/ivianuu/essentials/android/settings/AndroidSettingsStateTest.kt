package com.ivianuu.essentials.android.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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
        val actionCollector = module.collector(
            scope = this,
            adapter = adapter,
            dispatcher = coroutineContext.get(CoroutineDispatcher.Key)!!
        )
        val state = module.setting(
            scope = this,
            adapter = adapter,
            dispatcher = coroutineContext.get(CoroutineDispatcher.Key)!!,
            contentChangesFactory = { contentChanges }
        )

        value shouldBe 0

        val stateCollector = state.testCollect(this)
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

        // dispatched updates
        actionCollector.dispatchUpdate { value + 1 }
        contentChanges.tryEmit(Unit)
        value shouldBe 2
        stateCollector.values.shouldHaveSize(3)
        stateCollector.values[2] shouldBe 2

        // updates with result
        actionCollector.update { value - 1 } shouldBe 1
        contentChanges.tryEmit(Unit)
        value shouldBe 1
        stateCollector.values.shouldHaveSize(4)
        stateCollector.values[3] shouldBe 1
    }
}
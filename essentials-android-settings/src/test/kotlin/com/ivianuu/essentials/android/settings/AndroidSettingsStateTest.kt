package com.ivianuu.essentials.android.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.collector
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
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
        val adapter = object : AndroidSettingsAdapter<Int> {
            override fun get(): Int = value
            override fun set(_value: Int) {
                value = _value
                contentChanges.tryEmit(Unit)
            }
        }
        val actions = EventFlow<AndroidSettingAction<Int>>()
        val actionCollector = collector(actions)
        val ready = MutableStateFlow(false)
        val state = AndroidSettingStateModule<Int, Int>(
            "name",
            AndroidSettingsType.GLOBAL
        ).settingsState(
            scope = this,
            dispatcher = TestCoroutineDispatcher(),
            adapterFactory = { _, _, _ -> adapter },
            contentChangesFactory = { contentChanges },
            initial = value,
            actions = actions,
            ready = ready
        )
        advanceUntilIdle()

        ready.value shouldBe true
        value shouldBe 0

        val stateCollector = state.testCollect(this)

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
        advanceUntilIdle()
        value shouldBe 2
        stateCollector.values.shouldHaveSize(3)
        stateCollector.values[2] shouldBe 2

        // updates with result
        actionCollector.update { value - 1 } shouldBe 1
        value shouldBe 1
        stateCollector.values.shouldHaveSize(4)
        stateCollector.values[3] shouldBe 1
    }
}
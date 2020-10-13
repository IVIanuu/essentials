package com.ivianuu.essentials.recentapps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childScope
import com.ivianuu.essentials.util.NoopLogger
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.containsExactly

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class RecentAppsTest {

    @Test
    fun testRecentApps() = runBlockingTest {
        val recentAppsScopeDispatcher = TestCoroutineDispatcher()
        val recentAppsScope = childScope(recentAppsScopeDispatcher)
        val accessibilityEvents = EventFlow<AccessibilityEvent>()
        val services = object : AccessibilityServices {
            override val events: Flow<AccessibilityEvent>
                get() = accessibilityEvents
            override val isConnected: Flow<Boolean>
                get() = emptyFlow()

            override fun applyConfig(config: AccessibilityConfig): DisposableHandle {
                return object : DisposableHandle {
                    override fun dispose() {
                    }
                }
            }

            override suspend fun performGlobalAction(action: Int): Boolean = false
        }
        val recentAppsFlow = recentApps(recentAppsScope, NoopLogger, services)
        val recentApps = mutableListOf<List<String>>()
        val collectorJob = launch {
            recentAppsFlow.collect {
                recentApps += it
            }
        }
        recentAppsScopeDispatcher.runCurrent()

        accessibilityEvents.offer(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "a",
                null,
                true
            )
        )

        accessibilityEvents.offer(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "b",
                null,
                false
            )
        )

        accessibilityEvents.offer(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_ANNOUNCEMENT,
                "c",
                null,
                true
            )
        )

        accessibilityEvents.offer(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "b",
                null,
                true
            )
        )

        accessibilityEvents.offer(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "c",
                null,
                true
            )
        )

        expectThat(recentApps)
            .containsExactly(
                listOf(),
                listOf("a"),
                listOf("b", "a"),
                listOf("c", "b", "a")
            )

        collectorJob.cancelAndJoin()
        recentAppsScope.cancel()
    }

    @Test
    fun testCurrentApp() = runBlockingTest {
        val recentApps = EventFlow<List<String>>()
        val currentApps = mutableListOf<String?>()
        val collectorJob = launch {
            currentApp(recentApps)
                .collect { currentApps += it }
        }

        recentApps.offer(listOf("a", "b", "c"))
        recentApps.offer(listOf("a", "b", "c"))
        recentApps.offer(listOf("c", "a", "b"))
        recentApps.offer(listOf("a", "b", "c"))
        recentApps.offer(listOf("b", "c", "a"))

        expectThat(currentApps)
            .containsExactly(
                "a",
                "c",
                "a",
                "b"
            )

        collectorJob.cancelAndJoin()
    }

}
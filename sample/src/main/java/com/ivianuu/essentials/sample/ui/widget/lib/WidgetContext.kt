/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample.ui.widget.lib

import android.view.ViewGroup
import com.github.ajalt.timberkt.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WidgetContext(
    private val coroutineScope: CoroutineScope,
    private val rootViewProvider: () -> ViewGroup,
    private val buildWidgets: BuildContext.() -> Unit
) {

    private var root: RootWidget? = null

    private val generationTracker = GenerationTracker()

    fun invalidate() {
        coroutineScope.launch(Dispatchers.Default) {
            val runGeneration: Int

            synchronized(this@WidgetContext) {
                runGeneration = generationTracker.incrementAndGetNextScheduled()
            }

            val newRoot = RootWidget()

            with(WidgetBuildContext(this@WidgetContext, newRoot)) {
                buildWidgets()
            }

            withContext(Dispatchers.Main) {
                if (generationTracker.finishGeneration(runGeneration)) {
                    synchronized(this@WidgetContext) {
                        root = newRoot
                        val rootContainer = rootViewProvider()
                        var rootView = rootContainer
                            .findViewById<ViewGroup>(newRoot.viewId)
                        if (rootView == null) {
                            rootView = newRoot.createView(rootContainer)
                            rootContainer.addView(rootView)
                        }
                        d { "layout new root $newRoot" }
                        newRoot.layout(rootContainer)
                        d { "bind new root $newRoot" }
                        newRoot.bind(rootContainer)
                    }
                }
            }
        }
    }

    fun cancelAll() {
        generationTracker.finishMaxGeneration()
    }

    private class GenerationTracker {

        @Volatile
        private var maxScheduledGeneration: Int = 0
        @Volatile
        private var maxFinishedGeneration: Int = 0

        fun incrementAndGetNextScheduled(): Int = synchronized(this) {
            ++maxScheduledGeneration
        }

        fun finishMaxGeneration(): Boolean = synchronized(this) {
            val isInterrupting = hasUnfinishedGeneration()
            maxFinishedGeneration = maxScheduledGeneration
            return@finishMaxGeneration isInterrupting
        }

        fun hasUnfinishedGeneration(): Boolean = synchronized(this) {
            maxScheduledGeneration > maxFinishedGeneration
        }

        fun finishGeneration(runGeneration: Int): Boolean = synchronized(this) {
            val isLatestGeneration =
                maxScheduledGeneration == runGeneration && runGeneration > maxFinishedGeneration

            if (isLatestGeneration) {
                maxFinishedGeneration = runGeneration
            }

            return@synchronized isLatestGeneration
        }
    }

}
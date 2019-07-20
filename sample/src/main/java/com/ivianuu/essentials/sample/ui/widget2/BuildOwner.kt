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

package com.ivianuu.essentials.sample.ui.widget2

import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface BuildOwner {
    fun rebuild()
    fun clear()
}

class AndroidBuildOwner(
    private val coroutineScope: CoroutineScope,
    private val view: ViewGroup,
    private val child: Widget
) : BuildOwner {

    private var rootWidget: RootWidget? = null
    private var rootElement: RootElement? = null

    private var buildJob: Job? = null

    init {
        rebuild()
    }

    override fun clear() {
        buildJob?.cancel()
        buildJob = null

        rootElement?.let {
            it.detachView()
            it.unmount()
        }
        rootElement = null

        rootWidget = null
    }

    override fun rebuild() {
        if (buildJob != null) return
        buildJob = coroutineScope.launch(Dispatchers.Main) {
            buildJob = null
            build()
        }
    }

    private fun build() {
        rootElement?.let {
            it.detachView()
            it.unmount()
        }

        val rootWidget = RootWidget(view, child)
        this.rootWidget = rootWidget

        val rootElement = rootWidget.createElement()
        this.rootElement = rootElement

        rootElement.mount(view.context, null, null)
        rootElement.attachView()
    }
}
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface BuildOwner {
    fun scheduleBuildFor(element: Element)
    fun clear()
}

class AndroidBuildOwner(
    private val coroutineScope: CoroutineScope,
    private val view: ViewGroup,
    private val child: BuildContext.() -> Widget
) : BuildOwner {

    private var root: RootElement? = null

    private var buildJob: Job? = null

    private val dirtyElements = mutableListOf<Element>()

    init {
        firstFrame()
    }

    override fun scheduleBuildFor(element: Element) {
        dirtyElements.add(element)
        scheduleBuild()
    }

    override fun clear() {
        buildJob?.cancel()
        buildJob = null

        root?.let {
            it.detachView()
            it.unmount()
        }
        root = null
    }

    private fun scheduleBuild() {
        if (buildJob != null) return
        buildJob = coroutineScope.launch(Dispatchers.Main) {
            buildJob = null
            rebuildDirtyElements()
        }
    }

    private fun rebuildDirtyElements() {
        d { "rebuild dirty elements $dirtyElements" }
        dirtyElements.toList().forEach { it.rebuild() }
        dirtyElements.clear()
    }

    private fun firstFrame() {
        val widget = RootWidget(this, view) {
            AndroidContextAmbient.Provider(value = view.context, child = child(it))
        }
        val root = widget.createElement()
        this.root = root
        root.mount(null, null)
        scheduleBuildFor(root)
    }
}
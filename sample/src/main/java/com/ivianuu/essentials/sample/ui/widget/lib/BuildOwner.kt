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

import android.content.Context
import android.view.ViewGroup
import com.github.ajalt.timberkt.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface BuildOwner {
    fun scheduleBuildFor(element: Element)
    fun clear()

    fun setContainer(container: ViewGroup)
    fun removeContainer()
}

class AndroidBuildOwner(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val child: BuildContext.() -> Unit
) : BuildOwner {

    private var root: RootElement? = null

    private var buildJob: Job? = null

    private val dirtyElements = mutableSetOf<Element>()

    internal var _container: ViewGroup? = null

    init {
        mountRoot()
    }

    override fun setContainer(container: ViewGroup) {
        if (container != _container) {
            _container = container
            root!!.createView()
            root!!.attachView()
        }
    }

    override fun removeContainer() {
        if (_container != null) {
            root!!.detachView()
            root!!.destroyView()
            _container = null
        }
    }

    override fun scheduleBuildFor(element: Element) {
        dirtyElements.add(element)
        scheduleBuild()
    }

    override fun clear() {
        buildJob?.cancel()
        buildJob = null
        removeContainer()
        root?.unmount()
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
        d { "rebuild dirty elements ${dirtyElements.map { it.widget.key }}" }
        val elementsToRebuild = dirtyElements.toList()
        dirtyElements.clear()
        elementsToRebuild.forEach { it.rebuild() }
    }

    private fun mountRoot() {
        val widget = RootWidget(this) {
            +ContextAmbient.Provider(context) {
                child()
            }
        }
        val root = widget.createElement()
        this.root = root
        root.mount(null, null)
    }

}
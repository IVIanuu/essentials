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

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.ajalt.timberkt.d

internal class RootWidget(
    val owner: AndroidBuildOwner,
    val child: BuildContext.() -> Unit
) : ViewWidget<FrameLayout>() {

    override fun createElement() = RootElement(owner, this)

    override fun createView(container: ViewGroup): FrameLayout =
        FrameLayout(container.context)
}

internal class RootElement(
    val _owner: AndroidBuildOwner,
    widget: RootWidget
) : ViewElement<FrameLayout>(widget) {

    var child: Element? = null
    private var pendingChild: Widget? = null

    override fun mount(
        parent: Element?,
        slot: Int?
    ) {
        super.mount(parent, slot)
        owner = _owner
        widget<RootWidget>().child(this)
        this.child = pendingChild!!.createElement()
        pendingChild = null
        child!!.mount(this, null)
    }

    override fun add(child: Widget) {
        check(pendingChild == null) { "only one child allowed" }
        pendingChild = child
    }

    override fun insertChildView(view: View, slot: Int?) {
        requireView().addView(view)
    }

    override fun moveChildView(view: View, slot: Int?) {
        error("unsupported")
    }

    override fun removeChildView(view: View) {
        requireView().removeView(view)
    }

    override fun createView() {
        super.createView()
        child?.createView()
    }

    override fun attachView() {
        isAttached = true
        updateView()
        findContainerView().addView(requireView())
        child?.attachView()
    }

    override fun detachView() {
        isAttached = false
        findContainerView().removeView(requireView())
    }

    override fun destroyView() {
        child?.destroyView()
        super.destroyView()
    }

    override fun unmount() {
        child?.unmount()
        child = null
        super.unmount()
    }

    override fun performRebuild() {
        widget<RootWidget>().child(this)
        d { "${widget.id} perform rebuild built $pendingChild" }
        val built = pendingChild ?: error("expecting a child $widget ${widget.id}")
        pendingChild = null
        isDirty = false
        child = updateChild(child, built, slot)
    }

    override fun onEachChild(block: (Element) -> Unit) {
        child?.let { block(it) }
    }

    override fun findContainerView(): ViewGroup = _owner._container!!
}
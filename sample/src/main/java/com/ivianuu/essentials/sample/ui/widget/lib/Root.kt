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

class RootWidget(
    val owner: BuildOwner,
    val rootView: ViewGroup,
    val child: (BuildContext) -> Widget
) : ViewWidget<FrameLayout>() {

    override fun createElement() =
        RootElement(owner, rootView, this)

    override fun createView(context: BuildContext): FrameLayout =
        FrameLayout(rootView.context)
}

class RootElement(
    val _owner: BuildOwner,
    val _rootView: ViewGroup,
    widget: RootWidget
) : ViewElement<FrameLayout>(widget) {

    var child: Element? = null

    override fun mount(
        parent: Element?,
        slot: Int?
    ) {
        super.mount(parent, slot)
        owner = _owner
        val child = ContainerAmbient.Provider(
            value = requireView(),
            child = widget<RootWidget>().child(this)
        ).createElement()
        this.child = child
        child.mount(this, null)
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

    override fun attachView() {
        _rootView.addView(requireView())
        child?.attachView()
    }

    override fun detachView() {
        child?.detachView()
        _rootView.removeView(requireView())
    }

    override fun unmount() {
        child?.unmount()
        child = null
        super.unmount()
    }

    override fun performRebuild() {
        d { "${javaClass.simpleName} perform rebuild $widget" }
        this.child = updateChild(
            child, ContainerAmbient.Provider(
                value = requireView(),
                child = widget<RootWidget>().child(this)
            ), null
        )
        isDirty = false
    }

    override fun onEachChild(block: (Element) -> Unit) {
        child?.let { block(it) }
    }

}
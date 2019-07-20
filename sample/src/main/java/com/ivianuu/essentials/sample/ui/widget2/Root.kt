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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class RootWidget(
    val rootView: ViewGroup,
    val child: Widget
) : ViewWidget<FrameLayout>() {

    override fun createElement() = RootElement(rootView, this)

    override fun createView(context: BuildContext, androidContext: Context): FrameLayout =
        FrameLayout(androidContext)
}

class RootElement(
    val _rootView: ViewGroup,
    widget: RootWidget
) : ViewElement<FrameLayout>(widget) {

    var child: Element? = null

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        val child = widget<RootWidget>().child.createElement()
        this.child = child
        child.mount(context, this, null)
    }

    override fun insertChild(view: View, slot: Int?) {
        requireView().addView(view)
    }

    override fun moveChild(view: View, slot: Int) {
    }

    override fun removeChild(view: View) {
        requireView().removeView(view)
    }

    override fun attachView() {
        _rootView.addView(requireView())
        child!!.attachView()
    }

    override fun detachView() {
        child!!.detachView()
        _rootView.removeView(requireView())
    }

    override fun unmount() {
        child!!.unmount()
        super.unmount()
    }

    override fun performRebuild(context: Context) {
        this.child = updateChild(context, child, widget<RootWidget>().child, null)
    }

}
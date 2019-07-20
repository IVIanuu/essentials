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
import android.widget.TextView
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast
import com.ivianuu.essentials.util.viewLifecycleScope

class WidgetController2 : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_widget

    private var buildOwner: BuildOwner? = null

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        buildOwner = AndroidBuildOwner(
            viewLifecycleScope,
            view.cast(),
            MyDataWidget(System.currentTimeMillis().toInt(), MyWrappingWidget())
        )
    }

    override fun onDestroyView(view: View) {
        buildOwner?.clear()
        buildOwner = null
        super.onDestroyView(view)
    }

}

class MyWrappingWidget : StatelessWidget() {
    override fun build(context: BuildContext): HelloWorldWidget {
        val value = context.ancestorInheritedElementForWidgetOfExactType<MyDataWidget>()
            ?.data ?: error("no data")
        d { "build with data $value" }
        return HelloWorldWidget()
    }
}

class HelloWorldWidget : ViewWidget<HelloWorldElement, TextView>() {
    override fun createElement() = HelloWorldElement(this)
    override fun createView(element: HelloWorldElement, context: Context) =
        TextView(context).apply {
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline4)
        }

    override fun updateView(element: HelloWorldElement, view: TextView) {
        super.updateView(element, view)
        view.text = "Hello World"
    }
}

class HelloWorldElement(override val widget: ViewWidget<*, TextView>) : ViewElement<TextView>() {

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        d { "mount" }
    }

    override fun attachView() {
        super.attachView()
        d { "attach view" }
    }

    override fun detachView() {
        d { "detach view" }
        super.detachView()
    }

    override fun unmount() {
        super.unmount()
        d { "unmount" }
    }

    override fun insertChild(view: View, slot: Int?) {
    }

    override fun moveChild(view: View, slot: Int?) {
    }

    override fun removeChild(view: View) {
    }
}

class MyDataWidget(
    val data: Int,
    child: Widget
) : InheritedWidget(child) {
    override fun createElement() = MyDataElement(this)
}

class MyDataElement(widget: MyDataWidget) : InheritedElement(widget)
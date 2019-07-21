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

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget2.exp.Ambient
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContextContext
import com.ivianuu.essentials.sample.ui.widget2.layout.Column
import com.ivianuu.essentials.sample.ui.widget2.lib.AndroidBuildOwner
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildOwner
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast
import com.ivianuu.essentials.util.viewLifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WidgetController2 : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_widget

    private var buildOwner: BuildOwner? = null

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        var count = 0

        viewLifecycleScope.launch {
            delay(100)
            while (coroutineContext.isActive) {
                delay(1000)
                count += 1
                buildOwner?.rebuild()
            }
        }

        buildOwner = AndroidBuildOwner(
            viewLifecycleScope,
            view.cast()
        ) {
            Count.Provider(
                value = count,
                child = Column(
                    children = listOf(
                        SimpleTextToolbar(title = "Hello world")
                    ) + (1..2).map {
                        HelloWorldWidget(it.toString())
                    }
                )
            )
        }
    }

    override fun onDestroyView(view: View) {
        buildOwner?.clear()
        buildOwner = null
        super.onDestroyView(view)
    }

}

val Count = Ambient<Int>()

class HelloWorldWidget(val tag: String) : ViewWidget<TextView>(key = tag) {
    override fun createView(context: BuildContext): TextView {
        return AppCompatTextView(AndroidContextContext(context)).apply {
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline4)
            text = "Initial text $tag"
        }
    }

    override fun updateView(context: BuildContext, view: TextView) {
        view.text = "Tag: $tag value ${Count.of(context)}"
    }
}

class Text(val text: String) : ViewWidget<TextView>() {
    override fun createView(context: BuildContext): TextView =
        AppCompatTextView(AndroidContextContext(context))

    override fun updateView(context: BuildContext, view: TextView) {
        view.text = text
    }
}
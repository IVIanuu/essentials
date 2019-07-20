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
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget2.exp.Ambient
import com.ivianuu.essentials.sample.ui.widget2.lib.AndroidBuildOwner
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildOwner
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
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
            CountAmbient.Provider(
                value = count,
                child = Column(
                    children = listOf(
                        HelloWorldWidget("1"),
                        HelloWorldWidget("2"),
                        HelloWorldWidget("3")
                    )
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

val CountAmbient = Ambient<Int>()

class HelloWorldWidget(val tag: String) : ViewWidget<TextView>() {
    override fun createView(context: BuildContext, androidContext: Context): TextView {
        return TextView(androidContext).apply {
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline4)
        }
    }

    override fun updateView(context: BuildContext, view: TextView) {
        view.text = "Tag: $tag value ${CountAmbient.of(context)}"
    }
}

class Column(children: List<Widget>) : ViewGroupWidget<LinearLayout>(children) {

    override fun createView(
        context: BuildContext,
        androidContext: Context
    ): LinearLayout = LinearLayout(androidContext).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        orientation = VERTICAL
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
    }

}
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

package com.ivianuu.essentials.sample.ui.list

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.Ambient
import androidx.compose.ambient
import com.ivianuu.essentials.ui.compose.core.ViewGroupWidget
import com.ivianuu.essentials.ui.compose.core.Widget
import com.ivianuu.essentials.ui.compose.core.WidgetComposition
import com.ivianuu.essentials.ui.compose.director.ComposeController
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.injekt.Inject

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

val TestAmbient = Ambient.of<String>()

class Column : ViewGroupWidget() {

    override fun createViewGroup(container: ViewGroup): ViewGroup =
        LinearLayout(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(Color.BLUE)
        }

    override fun updateView(view: View) {
        super.updateView(view)
        if (view.background != null) {
            view.setBackgroundColor(Color.RED)
        }
    }

    override fun WidgetComposition.compose() {
        val haha = +ambient(TestAmbient)
        emit { Text("Hello") }
        emit { Text("World") }
    }
}

class Text(var text: String) : Widget() {

    override fun createView(container: ViewGroup): View = TextView(container.context).apply {
        text = this@Text.text
    }

}

@Inject
class ListController : ComposeController() {

    override fun WidgetComposition.build() {
        emit { Column() }
    }

}
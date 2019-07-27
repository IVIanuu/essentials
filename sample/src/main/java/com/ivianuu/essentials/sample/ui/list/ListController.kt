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
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import com.ivianuu.essentials.ui.compose.core.ViewGroupWidget
import com.ivianuu.essentials.ui.compose.core.Widget
import com.ivianuu.essentials.ui.compose.core.WidgetComposition
import com.ivianuu.essentials.ui.compose.core.sourceLocation
import com.ivianuu.essentials.ui.compose.director.ComposeController
import com.ivianuu.essentials.ui.compose.epoxy.RecyclerViewWidget
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.injekt.Inject

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

class Column : ViewGroupWidget<LinearLayout>() {

    override fun createViewGroup(container: ViewGroup): LinearLayout =
        LinearLayout(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(Color.BLUE)
        }

    override fun updateView(view: LinearLayout) {
        super.updateView(view)
        if (view.background != null) {
            view.setBackgroundColor(Color.RED)
        }
    }

    override fun WidgetComposition.compose() {
        emit { Text("Hello") }
        emit { Text("World") }
    }
}

data class Text(var text: String) : Widget<TextView>() {

    override fun createView(container: ViewGroup) = TextView(container.context)

    override fun updateView(view: TextView) {
        super.updateView(view)
        view.text = text
    }

}

@Inject
class ListController : ComposeController() {

    override fun WidgetComposition.build() {
        emit {
            RecyclerViewWidget {
                (1..100).forEach { i ->
                    emit(sourceLocation() to i) { Text("Title: $i") }
                }
            }
        }
    }

}
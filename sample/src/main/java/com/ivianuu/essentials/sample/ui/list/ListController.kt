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
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.compose.onActive
import androidx.compose.state
import androidx.core.view.updatePadding
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
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
import com.ivianuu.kommon.core.view.dp
import com.ivianuu.kommon.core.view.drawableAttr
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

class Column : ViewGroupWidget<LinearLayout>() {

    override fun createViewGroup(container: ViewGroup): LinearLayout =
        LinearLayout(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            orientation = VERTICAL
        }

}

data class Text(
    var text: String = "",
    var textAppearance: Int = R.style.TextAppearance_MaterialComponents_Subtitle1,
    var backgroundColor: Int = 0
) : Widget<TextView>() {

    override fun createView(container: ViewGroup) = TextView(container.context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, dp(48).toInt())
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        background = drawableAttr(R.attr.selectableItemBackground)
        updatePadding(left = dp(16).toInt(), right = dp(16).toInt())
        setOnClickListener {}
    }

    override fun updateView(view: TextView) {
        super.updateView(view)
        view.text = text
        view.setTextAppearance(textAppearance)
        view.setBackgroundColor(backgroundColor)
    }

}

@Inject
class ListController : ComposeController() {

    override fun WidgetComposition.build() {
        emit(
            ctor = { Column() },
            children = {
                val countState = +state { 0 }

                d { "compose count is ${countState.value}" }

                onActive {
                    val job = GlobalScope.launch {
                        while (coroutineContext.isActive) {
                            countState.value += 1
                        }
                        delay(1000)
                    }

                    onDispose { job.cancel() }
                }

                emit(
                    ctor = { Text() },
                    update = {
                        it.text = "Count $countState"
                        it.backgroundColor = Color.BLUE
                    }
                )

                emit(
                    ctor = { RecyclerViewWidget() },
                    update = {
                        (1..100).forEach { i ->
                            emit(
                                key = sourceLocation() to i,
                                ctor = { Text() },
                                update = { it.text = "Title $i" }
                            )
                        }
                    }
                )
            }
        )
    }

}
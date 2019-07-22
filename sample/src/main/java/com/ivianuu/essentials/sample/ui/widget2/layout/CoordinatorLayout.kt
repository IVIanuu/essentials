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

package com.ivianuu.essentials.sample.ui.widget2.layout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.ivianuu.essentials.sample.ui.widget2.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.util.cast

class CoordinatorLayoutWidget(
    children: List<Widget>,
    key: Any? = null
) : ViewGroupWidget<CoordinatorLayout>(children, key) {

    constructor(
        key: Any? = null,
        children: CoordinatorLayoutBuilder.() -> Unit
    ) : this(CoordinatorLayoutBuilder().apply(children).children, key)

    override fun createView(context: BuildContext) = CoordinatorLayout(
        AndroidContextAmbient(context)
    )

}

class CoordinatorLayoutParams(
    val width: Int? = null,
    val height: Int? = null,
    val gravity: Int? = null,
    val marginLeft: Int? = null,
    val marginTop: Int? = null,
    val marginRight: Int? = null,
    val marginBottom: Int? = null,
    child: Widget,
    key: Any?
) : ViewPropsWidget(child, key) {

    override fun applyViewProps(context: BuildContext, view: View) {
        val lp = view.layoutParams.cast<CoordinatorLayout.LayoutParams>()
        if (width != null) lp.width = width
        if (height != null) lp.height = height
        if (gravity != null) lp.gravity = gravity
        if (marginLeft != null) lp.leftMargin = marginLeft
        if (marginTop != null) lp.topMargin = marginTop
        if (marginRight != null) lp.rightMargin = marginRight
        if (marginBottom != null) lp.bottomMargin = marginBottom
        view.layoutParams = lp
    }

}

class CoordinatorLayoutBuilder {

    val children = mutableListOf<Widget>()

    fun add(
        child: Widget,

        width: Int? = null,
        height: Int? = null,

        marginLeft: Int? = null,
        marginTop: Int? = null,
        marginRight: Int? = null,
        marginBottom: Int? = null,

        gravity: Int? = null,

        key: Any? = null
    ) {
        children.add(
            CoordinatorLayoutParams(
                width,
                height,
                gravity,
                marginLeft,
                marginTop,
                marginRight,
                marginBottom,
                child,
                key
            )
        )
    }
}
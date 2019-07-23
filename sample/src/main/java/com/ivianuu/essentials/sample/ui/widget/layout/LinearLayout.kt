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

package com.ivianuu.essentials.sample.ui.widget.layout

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.util.cast

class LinearLayoutWidget(
    val orientation: Int = LinearLayout.VERTICAL,
    val gravity: Int = if (orientation == LinearLayout.VERTICAL) {
        Gravity.TOP or Gravity.CENTER_HORIZONTAL
    } else {
        Gravity.START or Gravity.CENTER_HORIZONTAL
    },
    children: List<Widget>,
    key: Any? = null
) : ViewGroupWidget<LinearLayout>(children, key) {

    constructor(
        orientation: Int = LinearLayout.VERTICAL,
        gravity: Int = if (orientation == LinearLayout.VERTICAL) {
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        } else {
            Gravity.START or Gravity.CENTER_HORIZONTAL
        },
        key: Any? = null,
        children: LinearLayoutBuilder.() -> Unit
    ) : this(orientation, gravity, LinearLayoutBuilder().apply(children).children, key)

    override fun createView(context: BuildContext): LinearLayout = LinearLayout(
        AndroidContextAmbient(context)
    )

    override fun updateView(context: BuildContext, view: LinearLayout) {
        super.updateView(context, view)
        view.orientation = orientation
        view.gravity = gravity
    }
}

class LinearLayoutParams(
    val width: Int? = null,
    val height: Int? = null,
    val gravity: Int? = null,
    val weight: Float? = null,
    val marginLeft: Int? = null,
    val marginTop: Int? = null,
    val marginRight: Int? = null,
    val marginBottom: Int? = null,
    child: Widget,
    key: Any?
) : ViewPropsWidget(child, key) {

    override fun applyViewProps(context: BuildContext, view: View) {
        val lp = view.layoutParams.cast<LinearLayout.LayoutParams>()
        if (width != null) lp.width = width
        if (height != null) lp.height = height
        if (gravity != null) lp.gravity = gravity
        if (weight != null) lp.weight = weight
        if (marginLeft != null) lp.leftMargin = marginLeft
        if (marginTop != null) lp.topMargin = marginTop
        if (marginRight != null) lp.rightMargin = marginRight
        if (marginBottom != null) lp.bottomMargin = marginBottom
        view.layoutParams = lp
    }

}

class LinearLayoutBuilder {

    val children = mutableListOf<Widget>()

    fun add(
        child: Widget,
        width: Int? = null,
        height: Int? = null,
        gravity: Int? = null,
        weight: Float? = null,
        marginLeft: Int? = null,
        marginTop: Int? = null,
        marginRight: Int? = null,
        marginBottom: Int? = null,
        key: Any? = null
    ) {
        children.add(
            LinearLayoutParams(
                width,
                height,
                gravity,
                weight,
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
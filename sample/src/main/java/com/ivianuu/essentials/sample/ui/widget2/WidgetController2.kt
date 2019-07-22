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
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget2.layout.Column
import com.ivianuu.essentials.sample.ui.widget2.layout.VerticalScroller
import com.ivianuu.essentials.sample.ui.widget2.lib.AndroidBuildOwner
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildOwner
import com.ivianuu.essentials.sample.ui.widget2.lib.Element
import com.ivianuu.essentials.sample.ui.widget2.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast
import com.ivianuu.essentials.util.viewLifecycleScope

class WidgetController2 : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_widget

    private var buildOwner: BuildOwner? = null

    private val selectedIndices = mutableSetOf<Int>()

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        buildOwner = AndroidBuildOwner(
            viewLifecycleScope,
            view.cast()
        ) {
            VerticalScroller(
                child = Column(
                    children = (1..100).map { i ->
                        StatelessWidget {
                            ListItem(
                                title = "Title $i",
                                text = "Text $i",
                                secondaryAction = Checkbox(
                                    value = selectedIndices.contains(i),
                                    onChange = {}
                                ),
                                onClick = {
                                    if (selectedIndices.contains(i)) {
                                        selectedIndices.remove(i)
                                    } else {
                                        selectedIndices.add(i)
                                    }

                                    it.cast<Element>().markNeedsBuild()
                                }
                            )
                        }
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

class Text(val text: String) : ViewWidget<TextView>() {
    override fun createView(context: BuildContext): TextView =
        AppCompatTextView(AndroidContextAmbient(context))

    override fun updateView(context: BuildContext, view: TextView) {
        view.text = text
    }
}
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

import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget2.exp.Background
import com.ivianuu.essentials.sample.ui.widget2.layout.Container
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.util.getPrimaryColor
import com.ivianuu.kommon.core.content.dp

class SimpleTextToolbar(val title: String) : StatelessWidget() {

    override fun build(context: BuildContext): Widget {
        return Background(
            color = AndroidContextAmbient(context).getPrimaryColor(),
            child = Container(
                child = Text(title),
                height = AndroidContextAmbient(context).dp(56).toInt(),
                gravity = Gravity.CENTER
            )
        )
    }

}


class Text(val text: String) : ViewWidget<TextView>() {
    override fun createView(context: BuildContext): TextView =
        AppCompatTextView(AndroidContextAmbient(context))

    override fun updateView(context: BuildContext, view: TextView) {
        view.text = text
    }
}
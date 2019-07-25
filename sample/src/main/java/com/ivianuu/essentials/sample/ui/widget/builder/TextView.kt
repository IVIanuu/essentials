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

package com.ivianuu.essentials.sample.ui.widget.builder

import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ambientOf
import com.ivianuu.essentials.sample.ui.widget.lib.effectOf

val TextStyleAmbient = ambientOf<Int?>("TextStyle")

inline fun withTextStyle(res: Int, crossinline block: () -> Unit) = effectOf<Unit> {
    with(context) {
        +TextStyleAmbient.Provider(res) {
            block()
        }
    }
}

inline fun BuildContext.TextView(block: BuildView<AppCompatTextView>) = View<AppCompatTextView> {
    (+TextStyleAmbient)?.let { textAppearance(it) }
    block()
}

fun <V : TextView> ViewWidgetBuilder<V>.text(
    text: String? = null,
    res: Int? = null
) {
    updateView {
        when {
            text != null -> it.text = text
            res != null -> it.setText(res)
            else -> it.text = null
        }
    }
}

fun <V : TextView> ViewWidgetBuilder<V>.textAppearance(textAppearanceRes: Int) {
    updateView { it.setTextAppearance(textAppearanceRes) }
}

fun <V : TextView> ViewWidgetBuilder<V>.textColor(color: Int) {
    updateView { it.setTextColor(color) }
}
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

package com.ivianuu.essentials.ui.compose

import android.view.View
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.layoutRes
import com.ivianuu.essentials.R
import kotlinx.android.synthetic.main.es_list_subheader.view.*

inline fun ComponentComposition.ListSubheader(
    text: String? = null,
    textRes: Int? = null
) {
    View<View> {
        layoutRes(R.layout.es_list_subheader)
        bindView {
            when {
                text != null -> es_list_header_title.text = text
                textRes != null -> es_list_header_title.setText(textRes)
                else -> error("you must specify one of text or textRes")
            }
        }
    }
}
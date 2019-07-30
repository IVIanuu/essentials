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

package com.ivianuu.essentials.ui.epoxy

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import kotlinx.android.synthetic.main.es_item_simple_text.*

/**
 * Simple text model
 */
fun EpoxyController.SimpleText(
    text: String? = null,
    textRes: Int? = null,
    id: Any? = text + textRes
) = model(
    id = id,
    layoutRes = R.layout.es_item_simple_text,
    state = arrayOf(text, textRes),
    bind = {
        when {
            text != null -> es_text.text = text
            textRes != null -> es_text.setText(textRes)
            else -> error("you must specify one of text or textRes")
        }
    }
)
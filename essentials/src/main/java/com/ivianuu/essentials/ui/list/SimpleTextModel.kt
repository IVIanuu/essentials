/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.ui.list

import com.ivianuu.list.annotations.Model
import kotlinx.android.synthetic.main.es_item_simple_text.es_text

/**
 * Simple text model
 */
@Model open class SimpleTextModel : SimpleListModel() {

    var text by property("text") { "" }
    var textRes by property("textRes") { 0 }

    override fun bind(holder: EsListHolder) {
        super.bind(holder)
        when {
            text.isNotEmpty() -> holder.es_text.text = text
            textRes != 0 -> holder.es_text.setText(textRes)
            else -> error("you must specify one of text or textRes")
        }
    }

}
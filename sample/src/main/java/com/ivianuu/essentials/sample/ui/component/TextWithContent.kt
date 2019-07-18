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

package com.ivianuu.essentials.sample.ui.component

import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.component.lib.BuildContext
import com.ivianuu.essentials.sample.ui.component.lib.UiComponent
import com.ivianuu.kommon.core.view.inflate
import kotlinx.android.synthetic.main.text_with_content.view.*

class TextWithContent(
    override val id: Any?,
    private val text: String,
    private val content: UiComponent<*>
) : UiComponent<View>() {

    override val viewId: Int
        get() = R.id.text_with_checkbox

    init {
        state(text, content)
    }

    override fun createView(container: ViewGroup) =
        container.inflate(R.layout.text_with_content)

    override fun bind(view: View) {
        super.bind(view)
        view.text.text = text
    }

    override fun BuildContext.children() {
        emit(content, R.id.content_container)
    }

}
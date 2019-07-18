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

package com.ivianuu.essentials.sample.ui.widget

import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.view.inflate

class Toolbar(
    private val title: String? = null,
    private val titleRes: Int? = null,
    private val subtitle: String? = null,
    private val subtitleRes: Int? = null,
    override val key: Any? = null
) : Widget<Toolbar>() {

    override fun createView(container: ViewGroup): Toolbar =
        container.inflate<Toolbar>(R.layout.es_view_toolbar)

    override fun bind(view: Toolbar) {
        super.bind(view)
        when {
            title != null -> view.title = title
            titleRes != null -> view.setTitle(titleRes)
        }
        when {
            subtitle != null -> view.subtitle = title
            subtitleRes != null -> view.setSubtitle(subtitleRes)
        }
    }
}
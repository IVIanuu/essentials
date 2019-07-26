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

import android.view.ViewGroup
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.UpdateView
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.view.inflate

fun <V : ViewGroup> BuildContext.InflateViewWidget(
    layoutRes: Int,
    key: Any? = null,
    updateView: UpdateView<V>? = null
): Widget = ViewWidget(
    key = key,
    createView = { it.inflate<V>(layoutRes) },
    updateView = updateView
)
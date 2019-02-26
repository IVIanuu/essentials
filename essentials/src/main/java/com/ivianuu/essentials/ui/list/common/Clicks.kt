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

package com.ivianuu.essentials.ui.list.common

import android.view.View
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.ui.list.addListener

fun <T : ListModel<*>> T.onClick(
    viewProvider: (View) -> View = { it },
    block: (T, View) -> Unit
) {
    val listener = View.OnClickListener { block(this, it) }
    addListener(
        postBind = { _, holder -> viewProvider(holder.view).setOnClickListener(listener) },
        preUnbind = { _, holder -> viewProvider(holder.view).setOnClickListener(null) }
    )
}

fun <T : ListModel<*>> T.onClick(
    viewId: Int,
    block: (T, View) -> Unit
) {
    onClick({ it.findViewById(viewId) }, block)
}

fun <T : ListModel<*>> T.onLongClick(
    viewProvider: (View) -> View = { it },
    block: (T, View) -> Boolean
) {
    val listener = View.OnLongClickListener { block(this, it) }
    addListener(
        postBind = { _, holder -> viewProvider(holder.view).setOnLongClickListener(listener) },
        preUnbind = { _, holder -> viewProvider(holder.view).setOnLongClickListener(null) }
    )
}

fun <T : ListModel<*>> T.onLongClick(
    viewId: Int,
    block: (T, View) -> Boolean
) {
    onLongClick({ it.findViewById(viewId) }, block)
}
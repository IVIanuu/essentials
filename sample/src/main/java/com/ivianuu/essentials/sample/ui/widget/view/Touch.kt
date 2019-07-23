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

package com.ivianuu.essentials.sample.ui.widget.view

import android.view.MotionEvent
import android.view.View
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget

fun Clickable(
    key: Any? = null,
    onClick: () -> Unit,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View.OnClickListener::class),
    key = key,
    applyViewProps = { it.setOnClickListener { onClick() } },
    child = child
)

fun LongClickable(
    key: Any? = null,
    onLongClick: () -> Unit,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View.OnLongClickListener::class),
    key = key,
    applyViewProps = { it.setOnLongClickListener { onLongClick(); true } },
    child = child
)

fun DisableTouch(
    key: Any? = null,
    child: BuildContext.() -> Unit
) = Touchable(
    onTouch = { true },
    key = key,
    child = child
)

fun Touchable(
    key: Any? = null,
    onTouch: (MotionEvent) -> Boolean,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View.OnTouchListener::class),
    key = key,
    applyViewProps = { it.setOnTouchListener { _, event -> onTouch(event) } },
    child = child
)
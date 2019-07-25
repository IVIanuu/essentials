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
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget

fun BuildContext.Clickable(
    onClick: () -> Unit,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    updateViewProps = { it.setOnClickListener { onClick() } },
    child = child
)

fun BuildContext.LongClickable(
    onLongClick: () -> Unit,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    updateViewProps = { it.setOnLongClickListener { onLongClick(); true } },
    child = child
)

fun BuildContext.DisableTouch(
    child: BuildContext.() -> Unit
) = Touchable(
    onTouch = { true },
    child = child
)

fun BuildContext.Touchable(
    onTouch: (MotionEvent) -> Boolean,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    updateViewProps = { it.setOnTouchListener { _, event -> onTouch(event) } },
    child = child
)
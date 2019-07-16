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

package com.ivianuu.essentials.ui.epoxy.material

import android.graphics.drawable.Drawable
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.epoxy.FunModelBuilder
import kotlinx.android.synthetic.main.es_list_widget_switch.*

fun EpoxyController.SwitchListItem(
    id: Any?,

    value: Boolean,
    onChange: ((Boolean) -> Unit)? = null,

    title: String? = null,
    titleRes: Int = 0,

    text: String? = null,
    textRes: Int = 0,

    icon: Drawable? = null,
    iconRes: Int = 0,

    avatar: Drawable? = null,
    avatarRes: Int = 0,

    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = CompoundButtonListItem(
    id = id,
    value = value,
    onChange = onChange,
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    builderBlock = builderBlock,
    widgetLayoutRes = R.layout.es_list_widget_switch,
    compoundButtonProvider = { es_list_widget_switch }
)
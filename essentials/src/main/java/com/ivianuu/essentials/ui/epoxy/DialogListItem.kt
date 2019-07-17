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

import android.graphics.drawable.Drawable
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.ui.dialog.DialogContext
import com.ivianuu.essentials.ui.dialog.dialogRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.android.getClosestComponent
import com.ivianuu.injekt.get

fun EpoxyController.DialogListItem(
    id: Any?,

    buildDialog: MaterialDialog.(DialogContext) -> Unit,

    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    icon: Drawable? = null,
    iconRes: Int? = null,

    avatar: Drawable? = null,
    avatarRes: Int? = null,

    enabled: Boolean = true,

    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = ListItem(
    id = id,
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    enabled = enabled,
    builderBlock = {
        bind {
            root.isEnabled = true
            root.setOnClickListener {
                it.getClosestComponent().get<Navigator>()
                    .push(dialogRoute(block = buildDialog))
            }
        }

        builderBlock?.invoke(this)
    }
)
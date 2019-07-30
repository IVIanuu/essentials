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

import android.content.ClipboardManager
import android.graphics.drawable.Drawable
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.StringProvider
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.android.getClosestComponent
import com.ivianuu.injekt.get

fun EpoxyController.ClipboardListItem(
    id: Any?,

    clip: String? = null,
    clipRes: Int? = null,
    clipMessage: String? = null,
    clipMessageRes: Int? = R.string.es_copied_to_clipboard,

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
        state(clipMessage, clipMessageRes)

        bind {
            root.isEnabled = true
            root.setOnClickListener {
                with(it.getClosestComponent()) {
                    get<ClipboardManager>().text = when {
                        clip != null -> clip
                        clipRes != null -> get<StringProvider>().getString(clipRes)
                        else -> error("no clip provided")
                    }
                    if (clipMessage != null || clipMessageRes != null) {
                        with(get<Toaster>()) {
                            when {
                                clipMessage != null -> toast(clipMessage)
                                clipMessageRes != null -> toast(clipMessageRes)
                            }
                        }
                    }
                }
            }
        }

        builderBlock?.invoke(this)
    }
)
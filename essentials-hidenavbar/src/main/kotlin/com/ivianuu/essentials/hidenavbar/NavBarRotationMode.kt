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

package com.ivianuu.essentials.hidenavbar

/**
 * Nav bar rotation behavior
 */
enum class NavBarRotationMode(
    override val value: String,
    val titleRes: Int
) : com.ivianuu.essentials.store.prefs.BoxValueHolder<String> {
    Marshmallow("marshmallow", R.string.es_nav_bar_rotation_mode_marshmallow),
    Nougat("nougat", R.string.es_nav_bar_rotation_mode_nougat),
    Tablet("tablet", R.string.es_nav_bar_rotation_mode_tablet)
}

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

@file:Suppress("NOTHING_TO_INLINE")

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.support.v7.widget.Toolbar
import android.view.View
import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.traveler.Router

inline fun Toolbar.exitOnNavigationClick(router: Router) {
    setNavigationOnClickListener { router.exit() }
}

fun View.navigateOnClick(router: Router, key: () -> Any) {
    setOnClickListener { router.navigateTo(key()) }
}

fun PreferenceModel.Builder.navigateOnClick(router: Router, key: () -> Any) {
    clickListener {
        router.navigateTo(key())
        true
    }
}
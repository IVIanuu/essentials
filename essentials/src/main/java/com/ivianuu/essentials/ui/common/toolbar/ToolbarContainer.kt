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

package com.ivianuu.essentials.ui.common.toolbar

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.isInBackstack
import android.support.v7.widget.Toolbar
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.common.LabeledScreen
import com.ivianuu.essentials.ui.traveler.RouterHolder

/**
 * A component which holds a toolbar
 */
interface ToolbarContainer {
    val providedToolbar: Toolbar?

    @SuppressLint("PrivateResource")
    fun setupToolbar() {
        if (this !is Fragment) return // fragment only for now

        val toolbar = providedToolbar ?: return

        if (this is LabeledScreen) {
            if (screenLabel != null) {
                toolbar.title = screenLabel
            } else if (screenLabelRes != 0) {
                toolbar.setTitle(screenLabelRes)
            }
        }

        if (this is RouterHolder && isInBackstack) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
            toolbar.setNavigationOnClickListener { router.exit() }
        }
    }
}
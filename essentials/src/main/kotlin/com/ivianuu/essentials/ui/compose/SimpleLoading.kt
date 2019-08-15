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

package com.ivianuu.essentials.ui.compose

import android.content.res.ColorStateList
import android.view.View
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.setBy
import com.ivianuu.essentials.R
import com.ivianuu.kommon.core.view.color
import kotlinx.android.synthetic.main.es_simple_loading.view.*

/**
 * Simple loading model
 */
fun ComponentComposition.SimpleLoading(
    progressColor: Int? = null,
    progressColorRes: Int? = null
) {
    ViewByLayoutRes<View>(layoutRes = R.layout.es_simple_loading) {
        setBy(progressColor, progressColorRes) {
            if (progressColor != null || progressColorRes != null) {
                with(es_progress_bar) {
                    val finalProgressColor = when {
                        progressColor != null -> progressColor
                        progressColorRes != null -> color(progressColorRes)
                        else -> 0
                    }

                    val colorStateList = ColorStateList.valueOf(finalProgressColor)
                    progressBackgroundTintList = colorStateList
                    secondaryProgressTintList = colorStateList
                    indeterminateTintList = colorStateList
                }
            }
        }
    }
}
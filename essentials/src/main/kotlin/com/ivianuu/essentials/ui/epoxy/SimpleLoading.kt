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

import android.content.res.ColorStateList
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import kotlinx.android.synthetic.main.es_item_simple_loading.es_progress_bar

/**
 * Simple loading model
 */
fun EpoxyController.SimpleLoading(
    id: Any?,
    progressColor: Int? = null,
    progressColorRes: Int? = null
) = model(
    id = id,
    layoutRes = R.layout.es_item_simple_loading,
    state = arrayOf(progressColor, progressColorRes),
    bind = {
        if (progressColor != null || progressColorRes != null) {
            val finalProgressColor = when {
                progressColor != null -> progressColor
                progressColorRes != null -> es_progress_bar.context.getColor(progressColorRes)
                else -> 0
            }

            val colorStateList = ColorStateList.valueOf(finalProgressColor)
            with(es_progress_bar) {
                progressBackgroundTintList = colorStateList
                secondaryProgressTintList = colorStateList
                indeterminateTintList = colorStateList
            }
        }
    }
)
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

package com.ivianuu.essentials.ui.common.changehandler

import android.support.v4.app.Fragment
import android.transition.Slide
import android.view.Gravity

/**
 * @author Manuel Wrage (IVIanuu)
 */
class HorizontalChangeHandler @JvmOverloads constructor(duration: Long = -1) : BaseChangeHandler(duration) {
    override fun apply(fragment: Fragment) {
        fragment.enterTransition = Slide(Gravity.END).apply {
            duration = this@HorizontalChangeHandler.duration
        }
        fragment.exitTransition = Slide(Gravity.START).apply {
            duration = this@HorizontalChangeHandler.duration
        }
    }
}
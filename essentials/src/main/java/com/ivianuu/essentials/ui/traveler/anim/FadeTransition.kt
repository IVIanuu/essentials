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

package com.ivianuu.essentials.ui.traveler.anim

import android.transition.Fade
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

/**
 * @author Manuel Wrage (IVIanuu)
 */
class FadeTransition(private val duration: Long) : FragmentTransition {
    override fun setup(
        transaction: FragmentTransaction,
        from: Fragment?,
        to: Fragment?,
        isPush: Boolean
    ) {
        if (from != null) {
            from.exitTransition = Fade().also {
                if (duration != -1L) it.duration = duration
            }
        }
        if (to != null) {
            to.enterTransition = Fade().also {
                if (duration != -1L) it.duration = duration
            }
        }
    }
}
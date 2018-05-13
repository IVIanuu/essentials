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

package com.ivianuu.essentials.ui.base

import android.support.v4.app.FragmentManager
import android.support.v4.app.isInBackstack
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.traveler.FragmentKeyNavigator
import com.ivianuu.essentials.util.ext.unsafeLazy

/**
 * Base activity for fragment activities
 */
abstract class BaseFragmentActivity : BaseActivity() {

    protected open val fragmentContainer = android.R.id.content

    override val navigator by unsafeLazy {
        FragmentKeyNavigator(this, supportFragmentManager, fragmentContainer)
    }

    override fun onBackPressed() {
        if (!recursivelyDispatchOnBackPressed(supportFragmentManager)) {
            super.onBackPressed()
        }
    }

    private fun recursivelyDispatchOnBackPressed(fm: FragmentManager): Boolean {
        if (fm.backStackEntryCount == 0)
            return false

        val reverseOrder = fm.fragments
            .filter {
                it is BackListener
                        && it.isInBackstack
                        && it.isVisible
            }
            .reversed()

        for (f in reverseOrder) {
            val handledByChildFragments = recursivelyDispatchOnBackPressed(f.childFragmentManager)
            if (handledByChildFragments) {
                return true
            }

            val backpressable = f as BackListener
            if (backpressable.handleBack()) {
                return true
            }
        }
        return false
    }
}
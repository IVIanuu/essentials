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

package com.ivianuu.essentials.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.key.FragmentClassKey
import com.ivianuu.essentials.ui.traveler.key.requireKey
import com.ivianuu.essentials.ui.traveler.router
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_child_navigation.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ChildNavigationFragment : BaseFragment() {

    override val screenName: String
        get() = "child navigation"

    override val layoutRes = R.layout.fragment_child_navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val key = requireKey<ChildNavigationKey>()

        title.text = "Container: ${key.index}, Count: ${key.count}"
        view.setBackgroundColor(COLORS[key.index])

        pop_to_root.setOnClickListener { router.backToRoot() }
        prev.setOnClickListener { router.exit() }
        next.setOnClickListener {
            router.navigateTo(ChildNavigationKey(key.index, key.count + 1))
        }
    }

    private companion object {
        private val COLORS =
                arrayOf(Color.RED, Color.BLUE, Color.MAGENTA)
    }
}

@Parcelize
data class ChildNavigationKey(val index: Int, val count: Int)
    : FragmentClassKey(ChildNavigationFragment::class), Parcelable
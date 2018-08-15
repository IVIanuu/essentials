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
import android.view.View
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.detour.HorizontalDetour
import com.ivianuu.essentials.util.ext.bindActivityViewModel
import com.ivianuu.essentials.util.ext.bindParentViewModel
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.localRouter
import kotlinx.android.synthetic.main.fragment_child_navigation.*

@Detour(HorizontalDetour::class)
@Destination(ChildNavigationFragment::class)
data class ChildNavigationDestination(val index: Int, val count: Int)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ChildNavigationFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_child_navigation

    private val parentViewModel by bindParentViewModel<ChildNavigationContainerViewModel>()
    private val activityViewModel by bindActivityViewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel
        d { "parent view model -> $parentViewModel" }
        activityViewModel
        d { "activity view model -> $activityViewModel" }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val destination = childNavigationDestination()

        title.text = "Container: ${destination.index}, Count: ${destination.count}"
        view.setBackgroundColor(COLORS[destination.index])

        pop_to_root.setOnClickListener { localRouter().backToRoot() }
        prev.setOnClickListener { localRouter().exit() }

        next.setOnClickListener {
            localRouter().navigateTo(
                ChildNavigationDestination(
                    destination.index, destination.count + 1
                )
            )
        }
    }

    private companion object {
        private val COLORS =
            arrayOf(Color.RED, Color.BLUE, Color.MAGENTA)
    }
}
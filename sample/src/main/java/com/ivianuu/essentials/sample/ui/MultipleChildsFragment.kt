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

import android.os.Bundle
import android.view.View
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.FragmentClassKey
import com.ivianuu.essentials.ui.traveler.KeyFragmentNavigator
import com.ivianuu.essentials.ui.traveler.setupRouter

/**
 * @author Manuel Wrage (IVIanuu)
 */
class MultipleChildsFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_multiple_childs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CONTAINER_IDS
            .forEachIndexed { index, containerId ->
                val navigator = KeyFragmentNavigator(childFragmentManager, containerId)
                val router = setupRouter(navigator, containerId)
                if (savedInstanceState == null) {
                    router.newRootScreen(ChildNavigationKey(index, 1))
                }
            }
    }

    private companion object {
        private val CONTAINER_IDS =
            arrayOf(R.id.container_0, R.id.container_1, R.id.container_2)
    }
}

object MultipleChildsKey : FragmentClassKey(MultipleChildsFragment::class)
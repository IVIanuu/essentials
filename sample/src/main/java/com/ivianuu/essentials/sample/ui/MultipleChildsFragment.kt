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

import android.content.Context
import android.os.Bundle
import android.view.View
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.essentials.injection.FragmentBindingModule
import com.ivianuu.essentials.injection.PerFragment
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.key.FragmentClassKey
import com.ivianuu.essentials.ui.traveler.setupKeyFragmentSwapperRouter
import com.ivianuu.essentials.util.FragmentLifecycleScopeProvider
import com.ivianuu.essentials.util.ext.d

/**
 * @author Manuel Wrage (IVIanuu)
 */
@FragmentBindingModule
@PerFragment
@AutoContribute
class MultipleChildsFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_multiple_childs

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        FragmentLifecycleScopeProvider.from(this).lifecycle()
            .subscribe { d { "on event $it" } }

        d { "on attach" }
        peek()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        d { "on create" }
        peek()
    }

    override fun onStart() {
        super.onStart()

        d { "on start" }
        peek()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        d { "on view created" }

        peek()

        CONTAINER_IDS.forEachIndexed { index, containerId ->
            val router = setupKeyFragmentSwapperRouter(containerId)

            if (savedInstanceState == null) {
                router.replaceScreen(ChildNavigationContainerKey(index))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        d { "on resume" }
        peek()
    }

    override fun onPause() {
        super.onPause()
        d { "on pause" }
        peek()
    }

    override fun onStop() {
        super.onStop()
        d { "on stop" }
        peek()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        d { "on destroy view" }
        peek()
    }

    override fun onDestroy() {
        super.onDestroy()
        d { "on destroy" }
        peek()
    }

    override fun onDetach() {
        super.onDetach()
        d { "on detach" }
        peek()
    }

    private fun peek() {
        FragmentLifecycleScopeProvider.from(this).peekLifecycle()?.let {
            d { "peeked $it" }
        }
    }

    private companion object {
        private val CONTAINER_IDS =
            arrayOf(R.id.container_0, R.id.container_1, R.id.container_2)
    }
}

object MultipleChildsKey : FragmentClassKey(MultipleChildsFragment::class)
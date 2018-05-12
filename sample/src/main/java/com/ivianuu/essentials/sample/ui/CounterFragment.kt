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
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.FragmentKey
import com.ivianuu.essentials.ui.traveler.requireKey
import com.ivianuu.traveler.commands.Command
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_counter.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_counter

    private var containerId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        containerId = container?.id ?: -1
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val key = requireKey<CounterKey>()

        title.text = "Count: ${key.count}"

        go_up.setOnClickListener { router.navigateTo(CounterKey(key.count + 1)) }
        do_sub.setOnClickListener { router.exit() }
    }

}

@Parcelize
data class CounterKey(val count: Int) : FragmentKey(), Parcelable {
    override fun createFragment(data: Any?) = CounterFragment()

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        fragmentTransaction: FragmentTransaction
    ) {
        super.setupFragmentTransaction(command, currentFragment, nextFragment, fragmentTransaction)
    }
}
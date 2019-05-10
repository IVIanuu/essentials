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

package com.ivianuu.essentials.ui.traveler

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ivianuu.traveler.android.FragmentKey

/**
 * A [FragmentPagerAdapter] which uses [FragmentKey]s
 */
open class KeyFragmentPagerAdapter(
    fm: FragmentManager,
    val keys: List<FragmentKey>,
    behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) : FragmentPagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment = keys[position]
        .createFragment(null)

    override fun getCount(): Int = keys.size

}

/**
 * A [FragmentStatePagerAdapter] which uses [FragmentKey]s
 */
open class KeyFragmentStatePagerAdapter(
    fm: FragmentManager,
    val keys: List<FragmentKey>,
    behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) : FragmentStatePagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment = keys[position]
        .createFragment(null)

    override fun getCount(): Int = keys.size

}
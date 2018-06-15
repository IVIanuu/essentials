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

package com.ivianuu.essentials.ui.traveler.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ivianuu.compass.Compass

/**
 * Key fragment pager adapter
 */
open class CompassFragmentPagerAdapter(
    fm: FragmentManager,
    private val destinations: List<Any>
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = Compass.requireFragment(destinations[position])

    override fun getCount() = destinations.size
}
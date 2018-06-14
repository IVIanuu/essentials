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
import android.support.v4.app.FragmentStatePagerAdapter
import com.ivianuu.compass.Compass
import com.ivianuu.compass.FragmentRouteFactory

/**
 * Key fragment state pager adapter
 */
open class CompassFragmentStatePagerAdapter(
    fm: FragmentManager,
    private val destinations: List<Any>
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val destination = destinations[position]
        val factory = Compass.getRouteFactory(destination)
                as? FragmentRouteFactory<Any>
                ?: throw IllegalArgumentException("no route factory found for $destination")

        return factory.createFragment(destination)
    }

    override fun getCount() = destinations.size
}
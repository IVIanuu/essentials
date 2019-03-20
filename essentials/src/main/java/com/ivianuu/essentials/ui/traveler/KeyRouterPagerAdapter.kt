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

import com.ivianuu.director.Router
import com.ivianuu.director.RouterManager
import com.ivianuu.director.hasRoot
import com.ivianuu.director.setRoot
import com.ivianuu.director.toTransaction
import com.ivianuu.director.traveler.ControllerKey
import com.ivianuu.director.viewpager.RouterPagerAdapter

/**
 * A [RouterPagerAdapter] which uses [ControllerKey]s
 */
open class KeyRouterPagerAdapter(
    routerManager: RouterManager,
    private val keys: List<ControllerKey>
) : RouterPagerAdapter(routerManager) {

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRoot) {
            router.setRoot(
                getKey(position)
                    .createController(null)
                    .toTransaction()
            )
        }
    }

    override fun getCount(): Int = keys.size

    protected fun getKey(position: Int): ControllerKey = keys[position]
}
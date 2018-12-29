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

import com.ivianuu.director.*
import com.ivianuu.director.traveler.ControllerKey
import com.ivianuu.director.viewpager.RouterPagerAdapter

/**
 * A [RouterPagerAdapter] which uses [ControllerKey]s
 */
open class KeyRouterPagerAdapter(
    private val host: Controller,
    private val keys: List<ControllerKey>
) : RouterPagerAdapter(host) {

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController) {
            router.setRoot(
                keys[position]
                    .createController(null)
                    .toTransaction()
            )
        }
    }

    override fun getCount() = keys.size
}
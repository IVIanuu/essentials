/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.navigation.director

import com.ivianuu.director.Router
import com.ivianuu.director.common.RouterPagerAdapter
import com.ivianuu.director.hasRoot
import com.ivianuu.director.setRoot
import com.ivianuu.director.toTransaction

/**
 * A [RouterPagerAdapter] which uses [ControllerRoute]s
 */
open class ControllerRoutePagerAdapter(
    private val context: ControllerRoute.Context,
    private val routes: List<ControllerRoute>,
    routerFactory: () -> Router
) : RouterPagerAdapter(routerFactory) {

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRoot) {
            router.setRoot(
                getRoute(position)
                    .factory(context)
                    .toTransaction()
            )
        }
    }

    override fun getCount(): Int = routes.size

    protected fun getRoute(position: Int): ControllerRoute = routes[position]
}

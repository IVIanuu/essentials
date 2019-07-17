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

import androidx.fragment.app.FragmentActivity
import com.ivianuu.director.RouterTransaction
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.AppSchedulers
import com.ivianuu.kommon.lifecycle.getViewModel
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.rxkotlin.ofType

class ControllerRenderer(
    private val activity: FragmentActivity,
    private val navigator: Navigator,
    private val router: com.ivianuu.director.Router,
    private val schedulers: AppSchedulers
) {

    fun renderUntil(scope: Scope) {
        navigator.observable
            .observeOn(schedulers.main)
            .ofType<List<ControllerRoute>>()
            .subscribe { applyBackStack(it) }
            .disposeBy(scope)
    }

    private fun applyBackStack(backStack: List<ControllerRoute>) {
        val oldDirectorBackStack = router.backStack

        val newDirectorBackStack: List<RouterTransaction> = backStack
            .map { route ->
                var transaction = oldDirectorBackStack.firstOrNull { transaction ->
                    transaction.controller.getViewModel<ControllerRouteHolder>()
                        .route == route
                }

                if (transaction == null) {
                    val context = ControllerRoute.Context(
                        router.parent, activity, activity.application
                    )
                    transaction = RouterTransaction(route.factory(context))
                    transaction.controller.getViewModel<ControllerRouteHolder>()
                        .route = route
                    route.options?.applyToTransaction(transaction)
                }

                transaction!!
            }

        router.setBackStack(
            newDirectorBackStack,
            oldDirectorBackStack.size <= newDirectorBackStack.size
        )
    }

}
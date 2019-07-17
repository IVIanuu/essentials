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

package com.ivianuu.essentials.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ivianuu.director.Router
import com.ivianuu.director.router
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.ControllerRenderer
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.inject
import com.ivianuu.scopes.android.onPause

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait, MvRxView {

    override val component by unsafeLazy {
        activityComponent { modules(this@EsActivity.modules()) }
    }

    val navigator by inject<Navigator>()

    protected open val layoutRes get() = 0

    open val containerId
        get() = android.R.id.content

    open val startRoute: ControllerRoute?
        get() = null

    lateinit var router: Router
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        router = createRouter()

        if (navigator.backStack.isEmpty()) {
            startRoute?.let { navigator.push(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        invalidate()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        ControllerRenderer(this, navigator, router)
            .renderUntil(onPause)
    }

    override fun onBackPressed() {
        if (navigator.backStack.size > 1) {
            navigator.pop()
        } else {
            super.onBackPressed()
        }
    }

    override fun invalidate() {
    }

    protected open fun modules(): List<Module> = emptyList()

    protected open fun createRouter(): Router = router(containerId)

}
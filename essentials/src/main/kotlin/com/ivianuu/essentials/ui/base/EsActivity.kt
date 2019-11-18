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

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.director.Router
import com.ivianuu.director.router
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.ControllerRenderer
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.essentials.util.withAlpha
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.get
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.module
import com.ivianuu.scopes.android.onPause
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait, MvRxView {

    override val component by unsafeLazy {
        activityComponent {
            modules(esActivityModule(this@EsActivity))
            modules(this@EsActivity.modules())
        }
    }

    val navigator: Navigator by inject()
    private val controllerRenderer: ControllerRenderer by inject()

    protected open val layoutRes: Int get() = 0
    protected open val drawEdgeToEdge: Boolean get() = false

    open val containerId: Int
        get() = android.R.id.content

    open val startRoute: ControllerRoute?
        get() = null

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            navigator.pop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (drawEdgeToEdge) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.statusBarColor = Color.BLACK.withAlpha(0.25f)
            window.navigationBarColor = Color.TRANSPARENT
        }

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        if (navigator.backStack.isEmpty()) {
            startRoute?.let { navigator.push(it) }
        }

        // force router init
        get<Router>()

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        navigator.flow
            .onEach { onBackPressedCallback.isEnabled = it.size > 1 }
            .launchIn(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        invalidate()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        onPause.coroutineScope.launch {
            controllerRenderer.render()
        }
    }

    override fun invalidate() {
    }

    protected open fun modules(): List<Module> = emptyList()

}

private fun esActivityModule(esActivity: EsActivity) = module {
    single { esActivity.router(esActivity.containerId) }
}
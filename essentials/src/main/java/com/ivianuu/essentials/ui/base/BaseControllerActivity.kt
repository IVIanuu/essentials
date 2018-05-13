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

package com.ivianuu.essentials.ui.base

import android.os.Bundle
import android.view.ViewGroup
import com.ivianuu.conductor.Conductor
import com.ivianuu.conductor.Router
import com.ivianuu.essentials.ui.traveler.ControllerKeyNavigator
import com.ivianuu.essentials.util.ext.unsafeLazy

/**
 * Base activity for controller activities
 */
abstract class BaseControllerActivity : BaseActivity() {

    protected open val controllerContainer
        get() = findViewById<ViewGroup>(android.R.id.content)

    override val navigator by unsafeLazy {
        ControllerKeyNavigator(this, router)
    }

    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router = Conductor.attachRouter(this,
            controllerContainer, savedInstanceState)
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}
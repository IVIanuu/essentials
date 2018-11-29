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

import android.content.Context
import android.os.Bundle
import com.ivianuu.director.dialog.DialogController
import com.ivianuu.director.scopes.destroy
import com.ivianuu.essentials.injection.inject
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.traveler.Router
import javax.inject.Inject

/**
 * Base dialog controller
 */
abstract class BaseDialogController : DialogController(), ContextAware {

    @Inject lateinit var travelerRouter: Router

    override val providedContext: Context
        get() = activity

    val coroutineScope = destroy.asMainCoroutineScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

}
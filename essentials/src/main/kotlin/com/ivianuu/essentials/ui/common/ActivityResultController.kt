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

package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.common.addActivityResultListener
import com.ivianuu.director.common.startActivityForResult
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.dialog
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import java.util.concurrent.atomic.AtomicInteger

fun ActivityResultRoute(intent: Intent) =
    ControllerRoute<ActivityResultController>(options = ControllerRoute.Options().dialog()) {
        parametersOf(intent)
    }

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
) {
    val isOk: Boolean get() = resultCode == Activity.RESULT_OK
    val isCanceled: Boolean get() = resultCode == Activity.RESULT_CANCELED
    val isFirstUser: Boolean get() = resultCode == Activity.RESULT_FIRST_USER
}

@Factory
internal class ActivityResultController(@Param private val intent: Intent) : EsController() {

    override fun onCreate() {
        super.onCreate()

        val resultCode = resultCodes.incrementAndGet()

        addActivityResultListener(resultCode) { requestCode, resultCode, data ->
            navigator.pop(ActivityResult(requestCode, resultCode, data))
        }

        startActivityForResult(intent, resultCode)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View = View(requireActivity()) // dummy
}

private val resultCodes = AtomicInteger(0)

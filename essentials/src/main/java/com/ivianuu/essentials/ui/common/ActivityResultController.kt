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

package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.activitycallbacks.addActivityResultListener
import com.ivianuu.director.activitycallbacks.startActivityForResult
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.inject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityResultKey(
    val resultCode: Int,
    val intent: Intent,
    val requestCode: Int
) : ControllerKey(::ActivityResultController)

/**
 * Activity result controller
 */
class ActivityResultController : EsController() {

    private val key by inject<ActivityResultKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addActivityResultListener(key.requestCode) { requestCode, resultCode, data ->
            travelerRouter.goBackWithResult(
                key.resultCode,
                ActivityResult(requestCode, resultCode, data)
            )
        }

        startActivityForResult(key.intent, key.requestCode)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View = View(activity) // dummy

}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)

val ActivityResult.isOk: Boolean get() = resultCode == Activity.RESULT_OK
val ActivityResult.isCanceled: Boolean get() = resultCode == Activity.RESULT_CANCELED
val ActivityResult.isFirstUser: Boolean get() = resultCode == Activity.RESULT_FIRST_USER
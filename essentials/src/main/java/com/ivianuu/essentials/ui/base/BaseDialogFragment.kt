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
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.scopes.coroutines.cancelBy
import com.ivianuu.traveler.Router
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base dialog fragment
 */
abstract class BaseDialogFragment : AppCompatDialogFragment(), BackListener,
    CoroutineScope, Injectable, IdentifiableScreen,
    LifecycleOwner2, RouterHolder, ViewModelFactoryHolder {

    @Inject lateinit var router: Router
    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    override val providedRouter: Router
        get() = router

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = Job().cancelBy(onDestroy)

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

}
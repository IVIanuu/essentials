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

package com.ivianuu.essentials.ui.common.toolbar

import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.View
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.injection.EssentialsAppServiceModule
import com.ivianuu.essentials.util.ext.doOnActivityCreated
import com.ivianuu.essentials.util.ext.doOnFragmentViewCreated
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages toolbar fragments
 */
@EssentialsAppServiceModule
@Singleton
@AutoBindsIntoSet(AppService::class)
class ToolbarService @Inject constructor(private val application: Application) : AppService {
    override fun start() {
        application.doOnActivityCreated { activity, _ ->
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.doOnFragmentViewCreated(true) { _: FragmentManager, fragment: Fragment, _: View, _: Bundle? ->
                    if (fragment is ToolbarContainer) {
                        fragment.setupToolbar()
                    }
                }
            }
        }
    }
}
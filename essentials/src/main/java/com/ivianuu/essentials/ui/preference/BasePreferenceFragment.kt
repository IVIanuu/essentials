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

package com.ivianuu.essentials.ui.preference

import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.unsafeLazy

/**
 * Base preference fragment
 */
abstract class BasePreferenceFragment : BaseFragment() {

    abstract val prefsContainerId: Int
    open val preferenceTag = "prefs"
    open val prefsRes = -1

    protected val delegate by unsafeLazy {
        PreferenceFragmentDelegate(childFragmentManager, prefsContainerId, preferenceTag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        delegate.onCreate()
        if (prefsRes != -1) {
            delegate.addPreferencesFromResource(prefsRes)
        }
    }

}
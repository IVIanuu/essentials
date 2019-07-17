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

package com.ivianuu.essentials.ui.prefs

import android.content.SharedPreferences
import android.view.View
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.injekt.inject

/**
 * Prefs controller
 */
abstract class PrefsController : ListController() {

    private val prefs by inject<SharedPreferences>()

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        postInvalidate()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        prefs.unregisterOnSharedPreferenceChangeListener(changeListener)
    }

}

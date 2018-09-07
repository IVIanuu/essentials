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

package com.ivianuu.essentials.ui.traveler

import android.view.View
import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.essentials.util.ext.navigateOnClick
import com.ivianuu.traveler.Router

/**
 * Router aware component
 */
interface RouterHolder {
    val router: Router

    fun View.navigateOnClick(key: () -> Any) {
        navigateOnClick(router, key)
    }

    fun PreferenceModel.Builder.navigateOnClick(key: () -> Any) {
        navigateOnClick(router, key)
    }

}
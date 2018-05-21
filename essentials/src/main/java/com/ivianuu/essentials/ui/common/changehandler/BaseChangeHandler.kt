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

package com.ivianuu.essentials.ui.common.changehandler

import android.os.Bundle

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class BaseChangeHandler @JvmOverloads constructor(
    duration: Long = -1
) : FragmentChangeHandler() {

    var duration = duration

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        bundle.putLong(KEY_DURATION, duration)
    }

    override fun saveToBundle(bundle: Bundle) {
        super.saveToBundle(bundle)
        duration = bundle.getLong(KEY_DURATION)
    }

    companion object {
        private const val KEY_DURATION = "BaseChangeHandler.duration"
    }

}
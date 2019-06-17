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

package com.ivianuu.essentials.ui.twilight

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ivianuu.essentials.util.AppSchedulers
import com.ivianuu.injekt.Inject
import com.ivianuu.kprefs.rx.asObservable
import com.ivianuu.scopes.android.onDestroy
import com.ivianuu.scopes.rx.disposeBy

@Inject
class TwilightController(
    private val twilightPrefs: TwilightPrefs,
    private val schedulers: AppSchedulers
) {

    fun configure(activity: AppCompatActivity) {
        activity.delegate.localNightMode = when (twilightPrefs.twilightMode.get()) {
            TwilightMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            TwilightMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            TwilightMode.BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            TwilightMode.TIME -> AppCompatDelegate.MODE_NIGHT_AUTO_TIME
            TwilightMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        twilightPrefs.twilightMode.asObservable()
            .skip(1)
            .observeOn(schedulers.main)
            .subscribe { activity.recreate() }
            .disposeBy(activity.onDestroy)
    }

}
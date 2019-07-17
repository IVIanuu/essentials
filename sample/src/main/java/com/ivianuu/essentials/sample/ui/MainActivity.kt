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

package com.ivianuu.essentials.sample.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.gestures.unlock.ScreenUnlocker
import com.ivianuu.essentials.messaging.BroadcastFactory
import com.ivianuu.essentials.sample.ui.counter.counterRoute
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.injekt.get
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean
import com.ivianuu.kprefs.rx.asObservable
import com.ivianuu.scopes.android.onDestroy
import com.ivianuu.scopes.rx.disposeBy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : EsActivity() {

    override val startRoute: ControllerRoute?
        get() = counterRoute(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        get<KPrefs>().boolean("tile_state").asObservable()
            .subscribe { d { "tile state changed $it" } }
            .disposeBy(onDestroy)

        get<BroadcastFactory>().create(Intent.ACTION_SCREEN_OFF)
            .subscribe {
                lifecycleScope.launch {
                    delay(2000)
                    d { "unlock screen ${get<ScreenUnlocker>().unlockScreen()}" }
                }
            }
            .disposeBy(onDestroy)
    }

}

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

package com.ivianuu.essentials.sample.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.intentFilterOf
import com.ivianuu.essentials.util.ext.registerOnSharedPreferenceChangeListener
import com.ivianuu.essentials.util.ext.registerReceiver
import dagger.Binds
import dagger.Module

class MainActivity : BaseActivity() {

    override val startDestination: Any?
        get() = CounterDestination(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val handler = Handler()

        handler.postDelayed(this, 1000L) {

        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        prefs.registerOnSharedPreferenceChangeListener(this) {

        }

        registerReceiver(
            this, intentFilterOf(
                Intent.ACTION_SCREEN_OFF,
                Intent.ACTION_SCREEN_ON
            )
        ) {
            d { "on receive -> $it" }
        }
    }
}

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun bindBaseActivity(mainActivity: MainActivity): BaseActivity

}
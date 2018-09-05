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

package com.ivianuu.essentials.util.ext

import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.lifecycle.LifecyclePlugins
import com.ivianuu.essentials.util.lifecycleAwareComponent
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

fun SharedPreferences.registerOnSharedPreferenceChangeListener(
    owner: LifecycleOwner,
    onChange: (String) -> Unit
) = registerOnSharedPreferenceChangeListener(owner, LifecyclePlugins.DEFAULT_ACTIVE_STATE, onChange)

fun SharedPreferences.registerOnSharedPreferenceChangeListener(
    owner: LifecycleOwner,
    activeState: Lifecycle.State,
    onChange: (String) -> Unit
) {
    val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onChange.invoke(key) }

    lifecycleAwareComponent(
        owner = owner,
        activeState = activeState,
        onActive = { registerOnSharedPreferenceChangeListener(listener) },
        onInactive = { unregisterOnSharedPreferenceChangeListener(listener) }
    )
}

fun SharedPreferences.changes(): Observable<String> =
    Observable.create(object : ObservableOnSubscribe<String> {

        private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            emitter?.let {
                if (!it.isDisposed) {
                    it.onNext(key)
                }
            }
        }

        private var emitter: ObservableEmitter<String>? = null

        override fun subscribe(emitter: ObservableEmitter<String>) {
            emitter.setCancellable {
                this.emitter = null
                unregisterOnSharedPreferenceChangeListener(listener)
            }

            if (!emitter.isDisposed) {
                this.emitter = emitter
                registerOnSharedPreferenceChangeListener(listener)
            }
        }
    })
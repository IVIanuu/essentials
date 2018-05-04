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

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen
import android.support.v7.widget.RecyclerView
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Base preference fragment
 */
abstract class BasePreferenceFragment : PreferenceFragmentCompat(), BackListener, HasSupportFragmentInjector {

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject lateinit var router: Router

    open val prefsRes = -1

    protected val disposables = CompositeDisposable()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        if (prefsRes != -1) {
            addPreferencesFromResource(prefsRes)
        }
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return EnabledAwarePreferenceAdapter(preferenceScreen)
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
    }

    override fun handleBack(): Boolean {
        return false
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
}
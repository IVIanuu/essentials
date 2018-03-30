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

package com.ivianuu.essentials.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ivianuu.essentials.ui.traveler.KeyNavigator
import com.ivianuu.kommonextensions.unsafeLazy
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Base activity
 */
abstract class EssentialsActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject lateinit var router: Router

    protected val disposables = CompositeDisposable()

    protected open val fragmentContainer = android.R.id.content

    protected open val layoutRes = -1

    private val navigator by unsafeLazy {
        KeyNavigator(this, supportFragmentManager, fragmentContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (layoutRes != -1) setContentView(layoutRes)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
}

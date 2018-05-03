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

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.ivianuu.essentials.injection.ForActivity
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.rxactivityresult.RxActivityResult
import com.ivianuu.rxpermissions.RxPermissions
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.keys.KeyNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

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

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainer)
        if (currentFragment == null
            || currentFragment !is BackListener
            || !currentFragment.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
}

@Module
abstract class BaseActivityModule<T : BaseActivity> {

    @Binds
    abstract fun bindBaseActivity(activity: T): BaseActivity

    @Binds
    abstract fun bindActivity(activity: BaseActivity): Activity

    @Binds
    abstract fun bindAppCompatActivity(activity: BaseActivity): AppCompatActivity

    @Binds
    abstract fun bindFragmentActivity(activity: AppCompatActivity): FragmentActivity

    @ForActivity
    @Binds
    abstract fun bindContext(activity: Activity): Context

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideActivityResultStarter(activity: Activity) =
            RxActivityResult.get(activity)

        @JvmStatic
        @Provides
        fun providePermissionRequester(activity: Activity) = RxPermissions.get(activity)

    }

}
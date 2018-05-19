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
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.essentials.injection.EssentialsBindingModule
import com.ivianuu.essentials.injection.ForActivity
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.ActivityEvent
import com.ivianuu.essentials.ui.common.ActivityEvent.*
import com.ivianuu.essentials.ui.common.BackHandler
import com.ivianuu.essentials.ui.common.CORRESPONDING_ACTIVITY_EVENTS
import com.ivianuu.essentials.ui.traveler.getNavigatorHolder
import com.ivianuu.essentials.ui.traveler.getRouter
import com.ivianuu.essentials.ui.traveler.getTraveler
import com.ivianuu.essentials.ui.traveler.navigator.KeyFragmentAppNavigator
import com.ivianuu.essentials.ui.traveler.setupRouter
import com.ivianuu.essentials.util.analytics.NamedScreen
import com.ivianuu.essentials.util.ext.behaviorSubject
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.rxactivityresult.RxActivityResult
import com.ivianuu.rxpermissions.RxPermissions
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.Router
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, Injectable,
    NamedScreen, LifecycleScopeProvider<ActivityEvent> {

    @Inject lateinit var backHandler: BackHandler
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    protected open val layoutRes = -1

    open val fragmentContainer = android.R.id.content

    protected open val navigator: Navigator by unsafeLazy {
        KeyFragmentAppNavigator(
            this,
            supportFragmentManager,
            fragmentContainer
        )
    }

    lateinit var router: Router

    private val lifecycleSubject = behaviorSubject<ActivityEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(CREATE)

        router = setupRouter(navigator, fragmentContainer)

        if (layoutRes != -1) setContentView(layoutRes)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleSubject.onNext(STOP)
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(DESTROY)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!backHandler.handleBack(this)) {
            super.onBackPressed()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun lifecycle() = lifecycleSubject

    override fun correspondingEvents() = CORRESPONDING_ACTIVITY_EVENTS

    override fun peekLifecycle() = lifecycleSubject.value
}

@Module(includes = [EssentialsBindingModule::class])
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
        fun provideActivityResultStarter(activity: FragmentActivity) =
            RxActivityResult.get(activity)

        @JvmStatic
        @Provides
        fun providePermissionRequester(activity: FragmentActivity) = RxPermissions.get(activity)

        @JvmStatic
        @Provides
        fun provideTraveler(activity: BaseActivity) =
            activity.getTraveler(activity.fragmentContainer)

        @JvmStatic
        @Provides
        fun provideNavigatorHolder(activity: BaseActivity) =
            activity.getNavigatorHolder(activity.fragmentContainer)

        @JvmStatic
        @Provides
        fun provideRouter(activity: BaseActivity) =
            activity.getRouter(activity.fragmentContainer)

    }

}
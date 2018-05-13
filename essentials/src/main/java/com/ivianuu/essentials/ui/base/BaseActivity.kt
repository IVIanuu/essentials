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
import com.ivianuu.conductor.Controller
import com.ivianuu.essentials.injection.ForActivity
import com.ivianuu.essentials.injection.conductor.HasControllerInjector
import com.ivianuu.essentials.ui.common.ActivityEvent
import com.ivianuu.essentials.ui.common.ActivityEvent.*
import com.ivianuu.essentials.ui.common.CORRESPONDING_ACTIVITY_EVENTS
import com.ivianuu.rxactivityresult.RxActivityResult
import com.ivianuu.rxpermissions.RxPermissions
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), HasControllerInjector, HasSupportFragmentInjector,
    LifecycleScopeProvider<ActivityEvent> {

    @Inject lateinit var controllerInjector: DispatchingAndroidInjector<Controller>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject lateinit var travelerRouter: Router

    protected open val layoutRes = -1

    abstract val navigator: Navigator

    private val lifecycleSubject = BehaviorSubject.create<ActivityEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        lifecycleSubject.onNext(CREATE)

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

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
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

    override fun controllerInjector(): AndroidInjector<Controller> = controllerInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun lifecycle() = lifecycleSubject
    
    override fun correspondingEvents() = CORRESPONDING_ACTIVITY_EVENTS

    override fun peekLifecycle() = lifecycleSubject.value
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
        fun provideActivityResultStarter(activity: FragmentActivity) =
            RxActivityResult.get(activity)

        @JvmStatic
        @Provides
        fun providePermissionRequester(activity: FragmentActivity) = RxPermissions.get(activity)

    }

}

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

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.ivianuu.director.arch.lifecycle.LifecycleController
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.RxJavaAppInitializer
import com.ivianuu.essentials.app.TimberAppInitializer
import com.ivianuu.essentials.sample.injekt.*
import com.ivianuu.essentials.sample.perfs.runPerfTest
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.*
import com.ivianuu.timberktx.d
import dagger.Module
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

/*    override val component =
        activityComponent(listOf(mainActivityModule(this)), name = "MainActivityComponent")
*/
    // override val startKey: Any? get() = CounterKey(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runPerfTest()

        //   travelerRouter.navigate(NavBarSettingsKey(true, true))

        /* router.pushController(
             MyController().toTransaction()
                 .pushChangeHandler(SimpleSwapChangeHandler(false))
         )*/
    }
}

class MyController : LifecycleController(), ComponentHolder {

    override val component by unsafeLazy {
        controllerComponent(listOf(myControllerModule(this)), name = "MyControllerComponent")
    }

    private val viewModel by inject<MainViewModel> { parametersOf("123456") }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View = View(activity)

    override fun onBindView(view: View, savedViewState: Bundle?) {
        super.onBindView(view, savedViewState)
        viewModel.print()
    }

}

@Module//(includes = [EsActivityModule::class])
abstract class MainActivityModule {

    // @Binds
    //  abstract fun bindEsActivity(mainActivity: MainActivity): EsActivity

}

class MyEagerDep

class MainViewModel(
    private val password: String,
    private val application: Application,
    private val myController: MyController,
    private val mainActivity: MainActivity,
    private val context: Context,
    private val resources: Resources,
    private val name: String,
    private val appInitializers: Map<KClass<out AppInitializer>, AppInitializer>
) : ViewModel() {

    fun print() {
        d { "hello $name password is $password" }
        d { "app initializers $appInitializers" }
    }

}

fun mainActivityModule(activity: MainActivity) = activityModule(activity, "MainActivityModule") {
    single(createOnStart = true) { MyEagerDep() }

    appInitializer { RxJavaAppInitializer() }

    single { TimberAppInitializer() } intoClassMap { AppInitializer::class to APP_INITIALIZERS }

    single { RxJavaAppInitializer() } intoClassMap AppInitializer::class
}

fun myControllerModule(controller: MyController) = module(name = "MyControllerModule") {
    factory { controller }

    appInitializer { TimberAppInitializer() }

    viewModel { (password: String) ->
        MainViewModel(
            password,
            get(),
            get(),
            get(),
            get(),
            get(),
            get("username"),
            component.getMap(APP_INITIALIZERS)
        )
    }
}
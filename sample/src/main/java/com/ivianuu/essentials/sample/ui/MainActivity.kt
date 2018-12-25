
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

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.SimpleSwapChangeHandler
import com.ivianuu.director.arch.lifecycle.LifecycleController
import com.ivianuu.director.pushChangeHandler
import com.ivianuu.director.pushController
import com.ivianuu.director.toTransaction
import com.ivianuu.essentials.hidenavbar.NavBarSettingsKey
import com.ivianuu.essentials.sample.injekt.activityComponent
import com.ivianuu.essentials.sample.injekt.activityModule
import com.ivianuu.essentials.sample.injekt.controllerComponent
import com.ivianuu.essentials.sample.injekt.simpleModule
import com.ivianuu.essentials.sample.ui.counter.CounterKey
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.base.EsActivityModule
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.*
import com.ivianuu.timberktx.d
import com.ivianuu.traveler.navigate
import dagger.Binds
import dagger.Module

class MainActivity : EsActivity(), ComponentHolder {

    override val component by unsafeLazy {
        activityComponent(listOf(mainActivityModule(this)))
    }

    override val startKey: Any? get() = CounterKey(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        travelerRouter.navigate(NavBarSettingsKey(true, true))

        router.pushController(
            MyController().toTransaction()
                .pushChangeHandler(SimpleSwapChangeHandler(false))
        )
    }
}

class MyController : LifecycleController(), ComponentHolder {

    override val component by unsafeLazy {
        controllerComponent(listOf(myControllerModule(this)))
    }

    private val viewModel by inject<MainViewModel> { parametersOf("my_id") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = component.context.provider(MainActivity::class)

        val one = provider()
        val two = provider()
        val three = provider()

        d { "one $one, two $two, three $three" }
    }

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

@Module(includes = [EsActivityModule::class])
abstract class MainActivityModule {

    @Binds
    abstract fun bindEsActivity(mainActivity: MainActivity): EsActivity

}

class MyEagerDep {

    init {
        d { "initialized eager dep" }
    }
}

class MainViewModel(
    private val password: String,
    private val myController: MyController,
    private val mainActivity: MainActivity,
    private val context: Context,
    private val resources: Resources,
    private val name: String
) {

    fun print() {
        d { "hello $name password is $password" }
    }

}

fun mainActivityModule(activity: MainActivity) = activityModule(activity) {
    single(eager = true) { MyEagerDep() }
}

fun myControllerModule(controller: MyController) = simpleModule(controller) {
    factory { (password: String) ->
        MainViewModel(
            password,
            get(),
            get(),
            get(),
            get(),
            get("name")
        )
    }
}
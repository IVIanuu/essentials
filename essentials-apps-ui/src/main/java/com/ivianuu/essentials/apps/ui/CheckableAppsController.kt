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

package com.ivianuu.essentials.apps.ui

import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.director.scopes.destroy
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.glide.AppIcon
import com.ivianuu.essentials.ui.list.EsHolder
import com.ivianuu.essentials.ui.list.SimpleItem
import com.ivianuu.essentials.ui.list.SimpleLoadingItem
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.list.mvRxItemController
import com.ivianuu.essentials.ui.mvrx.mvRxViewModel
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.util.*
import com.ivianuu.essentials.util.ext.coroutinesIo
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.parametersOf
import com.ivianuu.list.id
import com.ivianuu.rxjavaktx.BehaviorSubject
import com.ivianuu.rxjavaktx.PublishSubject
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.es_item_checkable_app.checked
import kotlinx.android.synthetic.main.es_item_checkable_app.icon
import kotlinx.android.synthetic.main.es_item_checkable_app.title
import kotlinx.coroutines.async
import kotlinx.coroutines.rx2.asSingle

/**
 * App blacklist
 */
abstract class CheckableAppsController : ListController() {

    override fun modules() = listOf(checkableAppsModule)

    override val toolbarMenuRes
        get() = R.menu.controller_checkable_apps

    protected open val launchableAppsOnly: Boolean
        get() = false

    private val viewModel by mvRxViewModel<CheckableAppsViewModel> {
        get { parametersOf(launchableAppsOnly) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkedAppsChanged
            .subscribe { onCheckedAppsChanged(it) }
            .disposeBy(destroy)

        viewModel.attachCheckedAppsObservable(getCheckedAppsObservable())
    }

    override fun onDestroy() {
        viewModel.detachCheckedAppsObservable()
        super.onDestroy()
    }

    override fun itemController() = mvRxItemController(viewModel) { state ->
        when (state.apps) {
            is Loading -> {
                SimpleLoadingItem {
                    id("loading")
                }
            }
            is Success -> {
                state.apps()?.forEach { app ->
                    CheckableAppModel().add {
                        this.app = app
                        onClick { viewModel.appClicked(app) }
                    }
                }
            }
        }
    }

    override fun onToolbarMenuItemClicked(item: MenuItem): Boolean {
        viewModel.menuItemClicked(item.itemId)
        return true
    }

    abstract fun getCheckedAppsObservable(): Observable<Set<String>>

    abstract fun onCheckedAppsChanged(apps: Set<String>)
}

private class CheckableAppModel : SimpleItem(layoutRes = R.layout.es_item_checkable_app) {

    var app by idProperty<CheckableApp> { it.info.packageName }

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        with(holder) {
            Glide.with(icon)
                .load(AppIcon(app.info.packageName))
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(icon)

            title.text = app.info.appName

            checked.isChecked = app.checked
        }
    }

}

private val checkableAppsModule = module {
    factory { CheckableAppsViewModel(it[0], get()) }
}

private class CheckableAppsViewModel(
    private val launchableOnly: Boolean,
    private val appStore: AppStore
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    val checkedAppsChanged: Observable<Set<String>> get() = _checkedAppsChanged
    private val _checkedAppsChanged = PublishSubject<Set<String>>()

    private val checkedAppsScope = ReusableScope()
    private val checkedApps = BehaviorSubject<Set<String>>()

    override fun onInitialize(savedState: SavedState?) {
        super.onInitialize(savedState)

        Observables
            .combineLatest(
                coroutineScope.async(coroutinesIo) {
                    if (launchableOnly) {
                        appStore.getLaunchableApps()
                    } else {
                        appStore.getInstalledApps()
                    }
                }
                    .asSingle(coroutinesIo)
                    .toObservable(),
                checkedApps
            )
            .map { (infos, checked) ->
                infos
                    .map {
                        CheckableApp(
                            it,
                            checked.contains(it.packageName)
                        )
                    }
                    .toList()
            }
            .execute { copy(apps = it) }
    }

    fun attachCheckedAppsObservable(observable: Observable<Set<String>>) {
        observable.subscribe { checkedApps.onNext(it) }
            .disposeBy(checkedAppsScope)
    }

    fun detachCheckedAppsObservable() {
        checkedAppsScope.clear()
    }

    fun appClicked(app: CheckableApp) {
        pushNewCheckedApps {
            if (!app.checked) {
                it.add(app.info.packageName)
            } else {
                it.remove(app.info.packageName)
            }
        }
    }

    fun menuItemClicked(id: Int) {
        when (id) {
            R.id.es_action_select_all -> {
                withState { state ->
                    if (state.apps is Success<List<CheckableApp>>) {
                        pushNewCheckedApps { apps ->
                            apps.addAll(state.apps()?.map { it.info.packageName } ?: emptyList())
                        }
                    }
                }
            }
            R.id.es_action_deselect_all -> {
                pushNewCheckedApps { it.clear() }
            }
        }
    }

    private fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
        checkedApps
            .take(1)
            .map { it.toMutableSet().also(reducer) }
            .subscribe { _checkedAppsChanged.onNext(it) }
            .disposeBy(scope)
    }
}

private data class CheckableAppsState(
    val apps: Async<List<CheckableApp>> = Uninitialized
)

private data class CheckableApp(
    val info: AppInfo,
    val checked: Boolean
)
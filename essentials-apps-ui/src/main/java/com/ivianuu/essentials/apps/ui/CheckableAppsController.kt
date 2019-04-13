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
import com.ivianuu.essentials.util.SavedState
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.ext.coroutinesIo
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.injekt.annotations.Param
import com.ivianuu.injekt.get
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
        if (state.loading) {
            SimpleLoadingItem {
                id("loading")
            }
        } else {
            state.apps.forEach { app ->
                CheckableAppModel().add {
                    this.app = app
                    onClick { viewModel.appClicked(app) }
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

/**
 * View model for the [CheckableAppsController]
 */
@Factory
class CheckableAppsViewModel(
    @Param private val launchableOnly: Boolean,
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
            .subscribe { (infos, checked) ->
                setState {
                    val apps = infos
                        .map {
                            CheckableApp(
                                it,
                                checked.contains(it.packageName)
                            )
                        }
                        .toList()

                    copy(apps = apps, loading = false)
                }
            }
            .disposeBy(scope)
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
                    pushNewCheckedApps { apps ->
                        apps.addAll(state.apps.map { it.info.packageName })
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

data class CheckableAppsState(
    val apps: List<CheckableApp> = emptyList(),
    val loading: Boolean = true
)

data class CheckableApp(
    val info: AppInfo,
    val checked: Boolean
)
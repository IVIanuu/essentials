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
import androidx.lifecycle.viewModelScope
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.glide.AppIcon
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.epoxy.model
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.mvRxViewModel
import com.ivianuu.essentials.ui.simple.ListFragment
import com.ivianuu.essentials.util.*
import com.ivianuu.essentials.util.ext.andTrue
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.parametersOf

import com.ivianuu.rxjavaktx.BehaviorSubject
import com.ivianuu.rxjavaktx.PublishSubject
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.android.onDestroy
import com.ivianuu.scopes.android.scope
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.es_item_checkable_app.es_checkable_app_icon
import kotlinx.android.synthetic.main.es_item_checkable_app.es_checkable_app_title
import kotlinx.android.synthetic.main.es_item_checkable_app.es_checkable_app_toggle
import kotlinx.coroutines.async
import kotlinx.coroutines.rx2.asSingle

/**
 * App blacklist
 */
abstract class CheckableAppsFragment : ListFragment() {

    override fun modules() = listOf(checkableAppsModule)

    override val toolbarMenuRes
        get() = R.menu.es_fragment_checkable_apps

    protected open val launchableAppsOnly: Boolean
        get() = false

    private val viewModel by mvRxViewModel<CheckableAppsViewModel> {
        get { parametersOf(launchableAppsOnly) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkedAppsChanged
            .subscribe { onCheckedAppsChanged(it) }
            .disposeBy(onDestroy)

        viewModel.attachCheckedAppsObservable(getCheckedAppsObservable())
    }

    override fun onDestroy() {
        viewModel.detachCheckedAppsObservable()
        super.onDestroy()
    }

    override fun epoxyController() = mvRxEpoxyController(viewModel) { state ->
        when (state.apps) {
            is Loading -> SimpleLoading(id = "loading")
            is Success -> state.apps()?.forEach { app ->
                CheckableApp(app = app, onClick = { viewModel.appClicked(app) })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.es_action_select_all -> viewModel.selectAllClicked().andTrue()
        R.id.es_action_deselect_all -> viewModel.deselectAllClicked().andTrue()
        else -> false
    }

    abstract fun getCheckedAppsObservable(): Observable<Set<String>>

    abstract fun onCheckedAppsChanged(apps: Set<String>)
}

private fun EpoxyController.CheckableApp(
    app: CheckableApp,
    onClick: () -> Unit
) = model(
    id = app.info.packageName,
    layoutRes = R.layout.es_item_checkable_app,
    state = arrayOf(app),
    bind = {
        Glide.with(es_checkable_app_icon)
            .load(AppIcon(app.info.packageName))
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
            )
            .into(es_checkable_app_icon)

        es_checkable_app_title.text = app.info.appName

        es_checkable_app_toggle.isChecked = app.isChecked

        root.setOnClickListener { onClick() }
    }
)

private val checkableAppsModule = module {
    factory { CheckableAppsViewModel(it[0], get(), get()) }
}

private class CheckableAppsViewModel(
    private val launchableOnly: Boolean,
    private val appStore: AppStore,
    private val dispatchers: AppDispatchers
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    val checkedAppsChanged: Observable<Set<String>> get() = _checkedAppsChanged
    private val _checkedAppsChanged = PublishSubject<Set<String>>()

    private val checkedAppsScope = ReusableScope()
    private val checkedApps = BehaviorSubject<Set<String>>()

    init {
        Observables
            .combineLatest(
                viewModelScope.async(dispatchers.io) {
                    if (launchableOnly) {
                        appStore.getLaunchableApps()
                    } else {
                        appStore.getInstalledApps()
                    }
                }
                    .asSingle(dispatchers.io)
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
            if (!app.isChecked) {
                it.add(app.info.packageName)
            } else {
                it.remove(app.info.packageName)
            }
        }
    }

    fun selectAllClicked() {
        withState { state ->
            if (state.apps is Success<List<CheckableApp>>) {
                pushNewCheckedApps { apps ->
                    apps.addAll(state.apps()?.map { it.info.packageName } ?: emptyList())
                }
            }
        }
    }

    fun deselectAllClicked() {
        pushNewCheckedApps { it.clear() }
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
    val isChecked: Boolean
)
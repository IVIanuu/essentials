/*
 * Copyright 2019 Manuel Wrage
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

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.glide.AppIcon
import com.ivianuu.essentials.ui.epoxy.CheckboxListItem
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.menu.PopupMenu
import com.ivianuu.essentials.ui.menu.PopupMenuItem
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.mvRxViewModel
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.AppSchedulers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.BehaviorSubject
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.PublishSubject
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.get
import com.ivianuu.injekt.parametersOf
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.android.onDestroy
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asSingle
import kotlinx.coroutines.rx2.awaitFirst

/**
 * App blacklist
 */
abstract class CheckableAppsController : ListController() {

    override val toolbarMenu: PopupMenu<*>?
        get() = PopupMenu(
            items = listOf(
                PopupMenuItem(value = MenuOption.SelectAll, titleRes = R.string.es_select_all),
                PopupMenuItem(value = MenuOption.DeselectAll, titleRes = R.string.es_deselect_all)
            ),
            onSelected = {
                when (it) {
                    MenuOption.SelectAll -> viewModel.selectAllClicked()
                    MenuOption.DeselectAll -> viewModel.deselectAllClicked()
                }
            }
        )

    protected open val launchableAppsOnly: Boolean
        get() = false

    private val viewModel by mvRxViewModel<CheckableAppsViewModel> {
        get { parametersOf(launchableAppsOnly) }
    }

    override fun onCreate() {
        super.onCreate()
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
                CheckboxListItem(
                    id = app.info.packageName,
                    value = app.isChecked,
                    title = app.info.appName,
                    onChange = { viewModel.appClicked(app) },
                    builderBlock = {
                        bind {
                            val avatar = findView<ImageView>(R.id.es_list_avatar)
                            avatar.isVisible = true
                            findView<View>(R.id.es_list_image_frame).isVisible = true
                            Glide.with(avatar)
                                .load(AppIcon(app.info.packageName))
                                .apply(
                                    RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                )
                                .into(avatar)
                        }
                    }
                )
            }
        }
    }

    abstract fun getCheckedAppsObservable(): Observable<Set<String>>

    abstract fun onCheckedAppsChanged(apps: Set<String>)

    private enum class MenuOption { SelectAll, DeselectAll }

}

@Inject
internal class CheckableAppsViewModel(
    @Param private val launchableOnly: Boolean,
    private val appStore: AppStore,
    private val dispatchers: AppDispatchers,
    private val schedulers: AppSchedulers
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    private val _checkedAppsChanged = PublishSubject<Set<String>>()
    val checkedAppsChanged: Observable<Set<String>> get() = _checkedAppsChanged

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
            .observeOn(schedulers.io)
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
        viewModelScope.launch(dispatchers.io) {
            pushNewCheckedApps {
                if (!app.isChecked) {
                    it.add(app.info.packageName)
                } else {
                    it.remove(app.info.packageName)
                }
            }
        }
    }

    fun selectAllClicked() {
        viewModelScope.launch(dispatchers.io) {
            state.apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps.addAll(allApps.map { it.info.packageName })
                }
            }
        }
    }

    fun deselectAllClicked() {
        viewModelScope.launch(dispatchers.io) {
            pushNewCheckedApps { it.clear() }
        }
    }

    private suspend fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
        checkedApps.awaitFirst()
            .toMutableSet()
            .apply(reducer)
            .let { _checkedAppsChanged.onNext(it) }
    }
}

internal data class CheckableAppsState(
    val apps: Async<List<CheckableApp>> = Uninitialized
)

internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)
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

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.api.loadAny
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.epoxy.model
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.mvRxViewModel
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.scopes.ReusableScope
import hu.akarnokd.kotlin.flow.BehaviorSubject
import hu.akarnokd.kotlin.flow.PublishSubject
import kotlinx.android.synthetic.main.es_item_checkable_app.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * App blacklist
 */
abstract class CheckableAppsController : ListController() {

    override val toolbarMenu: PopupMenu<*>?
        get() = PopupMenu(
            items = listOf(
                PopupMenuItem(titleRes = R.string.es_select_all, onSelected = {
                    viewModel.selectAllClicked()
                }),
                PopupMenuItem(titleRes = R.string.es_deselect_all, onSelected = {
                    viewModel.deselectAllClicked()
                })
            )
        )

    protected open val launchableAppsOnly: Boolean
        get() = false

    private val imageLoader by inject<ImageLoader>()

    private val viewModel by mvRxViewModel<CheckableAppsViewModel> {
        get { parametersOf(launchableAppsOnly) }
    }

    override fun onCreate() {
        super.onCreate()

        viewModel.checkedAppsChanged
            .onEach { onCheckedAppsChanged(it) }
            .launchIn(lifecycleScope)

        viewModel.attachCheckedAppsFlow(getCheckedAppsFlow())
    }

    override fun onDestroy() {
        viewModel.detachCheckedAppsObservable()
        super.onDestroy()
    }

    override fun epoxyController() = mvRxEpoxyController(viewModel) { state ->
        when (state.apps) {
            is Loading -> SimpleLoading(id = "loading")
            is Success -> state.apps()?.forEach { app ->
                CheckableApp(
                    app = app,
                    imageLoader = imageLoader,
                    onClick = { viewModel.appClicked(app) }
                )
            }
        }
    }

    abstract fun getCheckedAppsFlow(): Flow<Set<String>>

    abstract fun onCheckedAppsChanged(apps: Set<String>)

}

private fun EpoxyController.CheckableApp(
    app: CheckableApp,
    imageLoader: ImageLoader,
    onClick: () -> Unit
) = model(
    id = app.info.packageName,
    layoutRes = R.layout.es_item_checkable_app,
    state = arrayOf(app),
    bind = {
        es_checkable_app_icon.loadAny(AppIcon(app.info.packageName), imageLoader)
        es_checkable_app_title.text = app.info.appName
        es_checkable_app_checkbox.isChecked = app.isChecked
        root.setOnClickListener { onClick() }
    }
)

@Inject
internal class CheckableAppsViewModel(
    @Param private val launchableOnly: Boolean,
    private val appStore: AppStore,
    private val dispatchers: AppDispatchers
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    private val _checkedAppsChanged = PublishSubject<Set<String>>()
    val checkedAppsChanged: Flow<Set<String>> get() = _checkedAppsChanged

    private val checkedAppsScope = ReusableScope()
    private val checkedApps = BehaviorSubject<Set<String>>()

    init {
        viewModelScope.launch(dispatchers.io) {
            val appsFlow = flowOf {
                if (launchableOnly) appStore.getLaunchableApps() else appStore.getInstalledApps()
            }

            appsFlow.combine(checkedApps) { apps, checked ->
                apps
                    .map {
                        CheckableApp(
                            it,
                            it.packageName in checked
                        )
                    }
                    .toList()
            }.execute { copy(apps = it) }
        }
    }

    fun attachCheckedAppsFlow(flow: Flow<Set<String>>) {
        flow
            .onEach { checkedApps.emit(it) }
            .launchIn(checkedAppsScope.coroutineScope)
    }

    fun detachCheckedAppsObservable() {
        checkedAppsScope.clear()
    }

    fun appClicked(app: CheckableApp) {
        viewModelScope.launch(dispatchers.io) {
            pushNewCheckedApps {
                if (!app.isChecked) {
                    it += app.info.packageName
                } else {
                    it -= app.info.packageName
                }
            }
        }
    }

    fun selectAllClicked() {
        viewModelScope.launch(dispatchers.io) {
            state.apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps += allApps.map { it.info.packageName }
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
        checkedApps.first()
            .toMutableSet()
            .apply(reducer)
            .let { _checkedAppsChanged.emit(it) }
    }
}

internal data class CheckableAppsState(
    val apps: Async<List<CheckableApp>> = Uninitialized
)

internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)
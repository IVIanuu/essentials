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
import com.ivianuu.essentials.ui.mvrx.injekt.injectMvRxViewModel
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.essentials.util.ext.ResultKey
import com.ivianuu.essentials.util.ext.popWithResult
import com.ivianuu.injekt.Inject
import com.ivianuu.traveler.Router
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.es_item_app.*

@Parcelize
data class AppPickerKey(
    val launchableOnly: Boolean = false,
    override var resultCode: Int = 0
) : ControllerKey(::AppPickerController), ResultKey<AppInfo>

/**
 * App picker controller
 */
class AppPickerController : ListController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_title_app_picker

    private val viewModel: AppPickerViewModel by injectMvRxViewModel()

    override fun epoxyController() = mvRxEpoxyController(viewModel) { state ->
        when (state.apps) {
            is Loading -> SimpleLoading(id = "loading")
            is Success -> state.apps()?.forEach { app ->
                AppInfo(app = app, onClick = { viewModel.appClicked(app) })
            }
        }
    }

}

private fun EpoxyController.AppInfo(
    app: AppInfo,
    onClick: () -> Unit
) = model(
    id = app.packageName,
    layoutRes = R.layout.es_item_app,
    state = arrayOf(app),
    bind = {
        Glide.with(es_app_icon)
            .load(AppIcon(app.packageName))
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
            )
            .into(es_app_icon)

        es_app_title.text = app.appName

        root.setOnClickListener { onClick() }
    }
)

@Inject
internal class AppPickerViewModel(
    private val key: AppPickerKey,
    private val appStore: AppStore,
    dispatchers: AppDispatchers,
    private val router: Router
) : MvRxViewModel<AppPickerState>(AppPickerState()) {

    init {
        viewModelScope.execute(
            context = dispatchers.io,
            block = {
                if (key.launchableOnly) {
                    appStore.getLaunchableApps()
                } else {
                    appStore.getInstalledApps()
                }
            },
            reducer = { copy(apps = it) }
        )
    }

    fun appClicked(appInfo: AppInfo) {
        router.popWithResult(key.resultCode, appInfo)
    }
}

internal data class AppPickerState(val apps: Async<List<AppInfo>> = Uninitialized)
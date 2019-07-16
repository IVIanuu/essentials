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
import com.ivianuu.essentials.ui.epoxy.ListItem
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.injekt.injectMvRxViewModel
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.ui.traveler.ResultKey
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.popWithResult
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Inject
import com.ivianuu.traveler.Router

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
                ListItem(
                    id = app.packageName,
                    title = app.appName,
                    onClick = { viewModel.appClicked(app) },
                    builderBlock = {
                        bind {
                            val avatar = findView<ImageView>(R.id.es_list_avatar)
                            avatar.isVisible = true
                            findView<View>(R.id.es_list_image_frame).isVisible = true
                            Glide.with(avatar)
                                .load(AppIcon(app.packageName))
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

}

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
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

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.glide.AppIcon
import com.ivianuu.essentials.ui.list.EsHolder
import com.ivianuu.essentials.ui.list.SimpleItem
import com.ivianuu.essentials.ui.list.SimpleLoadingItem
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.injekt.mvRxViewModel
import com.ivianuu.essentials.ui.mvrx.list.mvRxItemController
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.SavedState
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.list.id
import com.ivianuu.traveler.Router
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.es_item_app.icon
import kotlinx.android.synthetic.main.es_item_app.title
import kotlinx.coroutines.launch

@Parcelize
data class AppPickerKey(
    val resultCode: Int,
    val launchableOnly: Boolean = false
) : ControllerKey(::AppPickerController)

/**
 * App picker controller
 */
class AppPickerController : ListController() {

    override fun modules() = listOf(appPickerModule)

    override val toolbarTitleRes: Int
        get() = R.string.es_title_app_picker

    private val viewModel by mvRxViewModel<AppPickerViewModel>()

    override fun itemController() = mvRxItemController(viewModel) { state ->
        if (state.loading) {
            SimpleLoadingItem {
                id("loading")
            }
        } else {
            state.apps.forEach { app ->
                AppInfoItem().add {
                    this.app = app
                    onClick { viewModel.appClicked(app) }
                }
            }
        }
    }

}

private class AppInfoItem : SimpleItem(layoutRes = R.layout.es_item_app) {

    var app by idProperty(AppInfo::packageName)

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        with(holder) {
            Glide.with(icon)
                .load(AppIcon(app.packageName))
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(icon)

            title.text = app.appName
        }
    }
}

private val appPickerModule = module {
    factory { AppPickerViewModel(get(), get(), get()) }
}

private class AppPickerViewModel(
    private val key: AppPickerKey,
    private val appStore: AppStore,
    private val router: Router
) : MvRxViewModel<AppPickerState>(AppPickerState()) {

    override fun onInitialize(savedState: SavedState?) {
        super.onInitialize(savedState)
        coroutineScope.launch {
            val apps = if (key.launchableOnly) {
                appStore.getLaunchableApps()
            } else {
                appStore.getInstalledApps()
            }

            setState { copy(apps = apps, loading = false) }
        }
    }

    fun appClicked(appInfo: AppInfo) {
        router.goBackWithResult(key.resultCode, appInfo)
    }
}

private data class AppPickerState(
    val apps: List<AppInfo> = emptyList(),
    val loading: Boolean = true
)
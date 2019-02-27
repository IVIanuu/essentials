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

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.glide.AppIcon
import com.ivianuu.essentials.injection.CONTROLLER_SCOPE
import com.ivianuu.essentials.ui.epoxy.EsEpoxyHolder
import com.ivianuu.essentials.ui.epoxy.SimpleEpoxyModel
import com.ivianuu.essentials.ui.epoxy.simpleLoading
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.epoxy.simpleEpoxyController
import com.ivianuu.essentials.ui.mvrx.injekt.mvRxViewModel
import com.ivianuu.essentials.ui.mvrx.withState
import com.ivianuu.essentials.ui.simple.SimpleController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.SavedState
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.timberktx.d
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
class AppPickerController : SimpleController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_screen_label_app_picker

    private val viewModel by mvRxViewModel<AppPickerViewModel>()

    override fun invalidate() {
        val state = withState(viewModel) { it }
        d { "invalidate state is $state" }
        super.invalidate()
    }

    override fun epoxyController() = simpleEpoxyController(viewModel) { state ->
        if (state.loading) {
            simpleLoading {
                id("loading")
            }
        } else {
            state.apps.forEach { app ->
                appInfo {
                    id(app.packageName)
                    app(app)
                    onClick { viewModel.appClicked(app) }
                }
            }
        }
    }

}

@EpoxyModelClass(layout = R2.layout.es_item_app)
abstract class AppInfoModel : SimpleEpoxyModel() {

    @EpoxyAttribute lateinit var app: AppInfo

    override fun bind(holder: EsEpoxyHolder) {
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

/**
 * View model for the [AppPickerController]
 */
@Factory(scopeName = CONTROLLER_SCOPE)
class AppPickerViewModel(
    private val key: AppPickerKey,
    private val appStore: AppStore,
    private val router: Router
) : MvRxViewModel<AppPickerState>(AppPickerState()) {

    override fun onInitialize(savedState: SavedState?) {
        super.onInitialize(savedState)
        d { "on init" }
        coroutineScope.launch {
            val apps = if (key.launchableOnly) {
                appStore.getLaunchableApps()
            } else {
                appStore.getInstalledApps()
            }

            d { "apps loaded $apps" }

            setState {
                d { "setting state" }
                copy(apps = apps, loading = false)
            }
        }
    }

    fun appClicked(appInfo: AppInfo) {
        router.goBackWithResult(key.resultCode, appInfo)
    }
}

data class AppPickerState(
    val apps: List<AppInfo> = emptyList(),
    val loading: Boolean = true
)
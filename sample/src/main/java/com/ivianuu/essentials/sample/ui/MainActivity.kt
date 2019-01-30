
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

import android.os.Bundle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.hidenavbar.NavBarSettingsKey
import com.ivianuu.essentials.sample.ui.counter.CounterKey
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.util.ext.cast
import com.ivianuu.essentials.util.getClassDelegate
import com.ivianuu.essentials.util.getClassDelegates
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.android.lifecycle.onDestroy
import com.ivianuu.timberktx.d
import com.ivianuu.traveler.navigate

class ViewModelStoreOwnerImpl : ViewModelStoreOwner {
    private val _viewModelStore by lazy { ViewModelStore() }
    override fun getViewModelStore(): ViewModelStore = _viewModelStore

    fun init(scope: Scope) {
        d { "init" }
        scope.addListener { _viewModelStore.clear() }
    }
}

class MainActivity : EsActivity(), ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    override val startKey: Any? get() = CounterKey(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        travelerRouter.navigate(NavBarSettingsKey(true, true))

        getClassDelegates().forEach { d { "found delegate -> $it" } }

        getClassDelegate<ViewModelStoreOwner>()
            .cast<ViewModelStoreOwnerImpl>().init(onDestroy)
    }
}
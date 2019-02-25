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

package com.ivianuu.essentials.ui.viewmodel.scopes

import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.doOnPostDestroy
import com.ivianuu.scopes.AbstractScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner
import com.ivianuu.scopes.cache.ScopeStore

val ViewModel.scopeOwner: ScopeOwner
    get() = scopeCache.get(this) as ScopeOwner

val ViewModel.scope: Scope get() = scopeOwner.scope

private val scopeCache = ScopeStore(::ViewModelScope)

private class ViewModelScope(viewModel: ViewModel) : AbstractScope(), ScopeOwner {

    override val scope: Scope
        get() = this

    init {
        viewModel.doOnPostDestroy { close() }
    }

}
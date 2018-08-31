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

package com.ivianuu.essentials.ui.epoxy

import android.support.annotation.CallSuper
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.reactivex.disposables.CompositeDisposable

/**
 * Base epoxy model with holder
 */
abstract class BaseEpoxyModel : EpoxyModelWithHolder<BaseEpoxyHolder>() {

    private var currentHolder: BaseEpoxyHolder? = null

    val disposables = CompositeDisposable()

    @CallSuper
    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        unbindInternal()

        holder.boundModel = this
        currentHolder = holder
    }

    @CallSuper
    override fun unbind(holder: BaseEpoxyHolder) {
        super.unbind(holder)
        unbindInternal()
    }

    private fun unbindInternal() {
        currentHolder?.let { it.boundModel = null }
        currentHolder = null
        disposables.clear()
    }

}
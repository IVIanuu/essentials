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
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ivianuu.autodispose.ScopeProvider
import com.ivianuu.essentials.util.ext.maybeSubject
import io.reactivex.subjects.MaybeSubject

/**
 * A [EpoxyModelWithHolder] which is also a [ScopeProvider]
 */
abstract class RxEpoxyModelWithHolder<T : EpoxyHolder> : EpoxyModelWithHolder<T>, ScopeProvider {

    private var bound = false
    private var currentHolder: RxEpoxyHolder? = null

    constructor() : super()

    constructor(id: Long) : super(id)

    @CallSuper
    override fun bind(holder: T) {
        super.bind(holder)
        if (bound) {
            unbindInternal()
        }

        if (holder is RxEpoxyHolder) {
            holder.boundModel = this
            currentHolder = holder
        }

        bound = true
    }

    @CallSuper
    override fun unbind(holder: T) {
        super.unbind(holder)
        if (bound) {
            unbindInternal()
        }

        bound = false
    }

    private var unbindNotifier: MaybeSubject<Unit>? = null

    private val notifier: MaybeSubject<Unit>
        get() {
            synchronized(this) {
                var n = unbindNotifier
                return if (n == null) {
                    n = maybeSubject()
                    unbindNotifier = n
                    n
                } else {
                    n
                }
            }
        }

    private fun unbindInternal() {
        currentHolder?.let {
            it.boundModel = null
        }
        currentHolder = null

        unbindNotifier?.let {
            if (!it.hasComplete()) {
                it.onSuccess(Unit)
            }
        }
        unbindNotifier = null
    }

    override fun requestScope() = notifier
}
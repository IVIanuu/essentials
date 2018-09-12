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

import android.view.View
import androidx.annotation.CallSuper
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.reactivex.disposables.CompositeDisposable

/**
 * Base epoxy model with holder
 */
abstract class BaseEpoxyModel<H : BaseEpoxyHolder> : EpoxyModelWithHolder<H>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var onClick: ((View) -> Unit)? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var onLongClick: ((View) -> Boolean)? = null

    protected val disposables: CompositeDisposable
        get() {
            if (_disposables == null) _disposables = CompositeDisposable()
            return _disposables!!
        }
    private var _disposables: CompositeDisposable? = null

    @CallSuper
    override fun bind(holder: H) {
        super.bind(holder)
        unbindInternal()
        holder.containerView.setOnClickListener(onClick)
        holder.containerView.setOnLongClickListener(onLongClick)
    }

    @CallSuper
    override fun unbind(holder: H) {
        super.unbind(holder)
        unbindInternal()
    }

    private fun unbindInternal() {
        _disposables?.clear()
    }
}
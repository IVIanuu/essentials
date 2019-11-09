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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.effectOf
import androidx.compose.frames.AbstractRecord
import androidx.compose.frames.Framed
import androidx.compose.frames.Record
import androidx.compose.frames._created
import androidx.compose.frames.readable
import androidx.compose.frames.writable
import androidx.compose.memo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel
import kotlin.reflect.KProperty

fun <T> retained(
    key: Any,
    init: () -> T
) = effectOf<T> {
    (+retainedState(key, init)).value
}

fun <T> retainedState(
    key: Any,
    init: () -> T
) = effectOf<RetainedState<T>> {
    val viewModel = +viewModel<RetainedStateViewModel>(
        key = "RetainedState:${key.hashCode()}",
        factory = RetainedStateViewModel.Factory
    )

    return@effectOf +memo {
        RetainedState(
            if (viewModel.values.containsKey(key)) {
                viewModel.values[key] as T
            } else {
                init()
            },
            key,
            viewModel
        )
    }
}

// todo add retainedStateFor

// todo replace with @Model once possible
class RetainedState<T> @PublishedApi internal constructor(
    value: T,
    private val key: Any,
    private val viewModel: RetainedStateViewModel
) : Framed {
    /* NOTE(lmr): When this module is compiled with IR, we will need to remove the below Framed implementation */

    @Suppress("UNCHECKED_CAST")
    var value: T
        get() = next.readable(this).value
        set(value) {
            viewModel.values[key] = value
            next.writable(this).value = value
        }

    private var next: StateRecord<T> =
        StateRecord(value)

    init {
        viewModel.values[key] = value
        _created(this)
    }

    override val firstFrameRecord: Record
        get() = next

    override fun prependFrameRecord(value: Record) {
        value.next = next
        @Suppress("UNCHECKED_CAST")
        next = value as StateRecord<T>
    }

    private class StateRecord<T>(myValue: T) : AbstractRecord() {
        override fun assign(value: Record) {
            @Suppress("UNCHECKED_CAST")
            this.value = (value as StateRecord<T>).value
        }

        override fun create(): Record =
            StateRecord(value)

        var value: T = myValue
    }

    /**
     * The componentN() operators allow state objects to be used with the property destructuring syntax
     *
     * var (foo, setFoo) = +state { 0 }
     * setFoo(123) // set
     * foo == 123 // get
     */
    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }

    /**
     * The getValue/setValue operators allow State to be used as a local variable with a delegate:
     *
     * var foo by +state { 0 }
     * foo += 123 // uses setValue(...)
     * foo == 123 // uses getValue(...)
     */
    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }

    fun removeValue() {
        viewModel.values.remove(key)
    }
}

@PublishedApi
internal class RetainedStateViewModel : ViewModel() {
    val values = mutableMapOf<Any, Any?>()

    companion object Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            RetainedStateViewModel() as T
    }
}
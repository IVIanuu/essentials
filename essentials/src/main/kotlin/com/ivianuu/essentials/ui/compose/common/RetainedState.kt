package com.ivianuu.essentials.ui.compose.common

import androidx.activity.ComponentActivity
import androidx.compose.ambient
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
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.util.getViewModel
import kotlin.reflect.KProperty

fun <T> retainedState(key: Any, init: () -> T) = effectOf<RetainedState<T>> {
    val activity = +ambient(ActivityAmbient) as ComponentActivity
    val viewModel: RetainedStateViewModel = activity.getViewModel(
        factory = RetainedStateViewModel,
        key = "RetainedState:${key.hashCode()}"
    )

    return@effectOf +memo {
        RetainedState(
            if (viewModel.value != viewModel) {
                viewModel.value as T
            } else {
                init()
            },
            viewModel
        )
    }
}

class RetainedState<T> @PublishedApi internal constructor(
    value: T,
    private val viewModel: RetainedStateViewModel
) : Framed {
    /* NOTE(lmr): When this module is compiled with IR, we will need to remove the below Framed implementation */

    @Suppress("UNCHECKED_CAST")
    var value: T
        get() = next.readable(this).value
        set(value) {
            viewModel.value = value
            next.writable(this).value = value
        }

    private var next: StateRecord<T> =
        StateRecord(value)

    init {
        viewModel.value = value
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
}

@PublishedApi
internal class RetainedStateViewModel : ViewModel() {
    var value: Any? = this
    companion object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            RetainedStateViewModel() as T
    }
}
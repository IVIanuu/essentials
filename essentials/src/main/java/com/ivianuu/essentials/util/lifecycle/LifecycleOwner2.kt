package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.ivianuu.androidktx.lifecycle.observeK
import com.ivianuu.liveevent.LiveEvent

/**
 * Extensions for lifecycle owner
 */
interface LifecycleOwner2 : LifecycleOwner {

    fun <T> LiveData<T>.observeK(onChanged: (T) -> Unit) {
        observeK(this@LifecycleOwner2, onChanged)
    }

    fun <T> LiveEvent<T>.consume(consumer: (T) -> Unit) {
        consume(this@LifecycleOwner2, consumer)
    }

}
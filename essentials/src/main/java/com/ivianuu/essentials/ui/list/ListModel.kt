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

package com.ivianuu.essentials.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.ui.list.internal.hash64Bit
import kotlin.properties.ReadWriteProperty

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class ListModel<H : ListHolder> {

    var id: Long = -1
        internal set(value) {
            check(!addedToController) { "cannot change the id of an added model" }
            field = value
        }

    val properties = ModelProperties()

    open val viewType: Int get() = layoutRes

    protected abstract val layoutRes: Int

    private val listeners = mutableSetOf<ListModelListener>()
    private var superCalled = false

    private var addedToController = false

    protected abstract fun onCreateHolder(): H

    protected open fun onBuildView(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)

    protected open fun onBind(holder: H) {
        superCalled = true
    }

    protected open fun onUnbind(holder: H) {
        superCalled = true
    }

    protected open fun onAttach(holder: H) {
        superCalled = true
    }

    protected open fun onDetach(holder: H) {
        superCalled = true
    }

    protected open fun onFailedToRecycle(holder: H): Boolean = true

    protected fun <T> property(
        key: String? = null,
        doHash: Boolean = true,
        defaultValue: (String) -> T
    ): ReadWriteProperty<ListModel<*>, T> = ModelPropertyDelegate(key, doHash, defaultValue)

    protected fun <T> requiredProperty(
        key: String? = null,
        doHash: Boolean = true
    ): ReadWriteProperty<ListModel<*>, T> = ModelPropertyDelegate(key, doHash) {
        error("missing property $it use optionalProperty() for optional ones")
    }

    protected fun <T> optionalProperty(
        key: String? = null,
        doHash: Boolean = true
    ): ReadWriteProperty<ListModel<*>, T?> =
        ModelPropertyDelegate(key, doHash) { null }

    fun addListener(listener: ListModelListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ListModelListener) {
        listeners.remove(listener)
    }

    internal fun createHolder(): H {
        notifyListeners { it.preCreateHolder(this) }
        val holder = onCreateHolder()
        notifyListeners { it.postCreateHolder(this, holder) }
        return holder
    }

    internal fun buildView(parent: ViewGroup): View {
        notifyListeners { it.preCreateView(this) }
        val view = onBuildView(parent)
        notifyListeners { it.postCreateView(this, view) }
        return view
    }

    internal fun bind(holder: H) {
        notifyListeners { it.preBind(this, holder) }
        requireSuperCalled { onBind(holder) }
        notifyListeners { it.postBind(this, holder) }
    }

    internal fun unbind(holder: H) {
        notifyListeners { it.preUnbind(this, holder) }
        requireSuperCalled { onUnbind(holder) }
        notifyListeners { it.postUnbind(this, holder) }
    }

    internal fun attach(holder: H) {
        notifyListeners { it.preAttach(this, holder) }
        requireSuperCalled { onAttach(holder) }
        notifyListeners { it.postAttach(this, holder) }
    }

    internal fun detach(holder: H) {
        notifyListeners { it.preDetach(this, holder) }
        requireSuperCalled { onDetach(holder) }
        notifyListeners { it.postDetach(this, holder) }
    }

    internal fun failedToRecycleView(holder: H): Boolean {
        val result = onFailedToRecycle(holder)
        notifyListeners { it.onFailedToRecycleView(this, holder) }
        return result
    }

    internal fun addedToController() {
        check(id != 0L) { "id must be set" }
        addedToController = true
        properties.addedToController()
    }

    private inline fun notifyListeners(block: (ListModelListener) -> Unit) {
        listeners.toList().forEach(block)
    }

    private inline fun requireSuperCalled(block: () -> Unit) {
        superCalled = false
        block()
        check(superCalled) { "super not called" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListModel<*>) return false

        if (id != other.id) return false
        if (viewType != other.viewType) return false

        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + viewType
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun toString(): String {
        return "${javaClass.simpleName}(" +
                "id=$id," +
                "viewType=$viewType" +
                "properties=$properties" +
                ")"
    }
}

fun <T> ListModel<*>.getProperty(key: String): ModelProperty<T>? =
    properties.getProperty(key)

fun <T> ListModel<*>.setProperty(property: ModelProperty<T>) {
    properties.setProperty(property)
}

fun <T> ListModel<*>.setProperty(
    key: String,
    value: T,
    doHash: Boolean = true
) {
    setProperty(ModelProperty(key, value, doHash))
}

fun ListModel<*>.id(id: Long) {
    this.id = id.hash64Bit()
}

fun ListModel<*>.id(id: String?) {
    this.id = id.hash64Bit()
}

inline fun <T : ListModel<*>> T.addTo(
    controller: ListController,
    block: T.() -> Unit
) {
    block()
    controller.addInternal(this)
}

abstract class ListModelCreator<T : ListModel<*>>(
    @PublishedApi internal val factory: () -> T
) {
    inline operator fun invoke(controller: ListController, block: T.() -> Unit): T =
        factory().also { it.addTo(controller, block) }
}
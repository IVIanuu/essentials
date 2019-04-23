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

package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

internal class ActivityRecyclerPool {

    private val pools = mutableListOf<PoolReference>()

    fun getPool(context: Context): PoolReference {

        val iterator = pools.iterator()
        var poolToUse: PoolReference? = null

        while (iterator.hasNext()) {
            val poolReference = iterator.next()
            when {
                poolReference.context === context -> {
                    if (poolToUse != null) {
                        throw IllegalStateException("A pool was already found")
                    }
                    poolToUse = poolReference
                    // finish iterating to remove any old contexts
                }
                poolReference.context.isActivityDestroyed() -> {
                    poolReference.viewPool.clear()
                    iterator.remove()
                }
            }
        }

        if (poolToUse == null) {
            poolToUse = PoolReference(context, this)
            (context as? LifecycleOwner)?.lifecycle?.addObserver(poolToUse)
            pools.add(poolToUse)
        }

        return poolToUse
    }

    fun clearIfDestroyed(pool: PoolReference) {
        if (pool.context.isActivityDestroyed()) {
            pool.viewPool.clear()
            pools.remove(pool)
        }
    }
}

internal class PoolReference(
    context: Context,
    private val parent: ActivityRecyclerPool
) : LifecycleObserver {

    private val contextReference: WeakReference<Context> = WeakReference(context)
    val viewPool = RecyclerView.RecycledViewPool()

    val context: Context? get() = contextReference.get()

    fun clearIfDestroyed() {
        parent.clearIfDestroyed(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onContextDestroyed() {
        clearIfDestroyed()
    }
}

internal fun Context?.isActivityDestroyed(): Boolean {
    if (this == null) {
        return true
    }

    if (this !is Activity) {
        return false
    }

    if (isFinishing) {
        return true
    }

    return isDestroyed
}
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

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.DiffUtil
import java.util.concurrent.Executor

private val handler = Handler(Looper.getMainLooper())
private val mainThreadExecutor = Executor {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        it.run()
    } else {
        handler.post(it)
    }
}

class AsyncModelDiffer(
    private val diffingExecutor: Executor,
    private val resultCallback: (result: DiffResult) -> Unit
) {

    private var models = emptyList<ListModel<*>>()
    var currentModels = emptyList<ListModel<*>>()
        private set

    private var maxScheduledGeneration = 0

    fun submitModels(newModels: List<ListModel<*>>) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = ++maxScheduledGeneration

        if (newModels == models) {
            notifyChange(models, newModels, null)
            return
        }

        val previousModels = currentModels

        // fast simple remove all
        if (newModels.isEmpty()) {
            models = emptyList()
            currentModels = emptyList()
            notifyChange(previousModels, newModels, null)
            return
        }

        // fast simple first insert
        if (models.isEmpty()) {
            models = newModels
            currentModels = newModels.toList()
            notifyChange(previousModels, newModels, null)
            return
        }

        diffingExecutor.execute {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = previousModels.size

                override fun getNewListSize(): Int = newModels.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldModel = previousModels[oldItemPosition]
                    val newModel = newModels[newItemPosition]
                    return newModel.id == oldModel.id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldModel = previousModels[oldItemPosition]
                    val newModel = newModels[newItemPosition]
                    return oldModel == newModel
                }
            })

            mainThreadExecutor.execute {
                if (maxScheduledGeneration == runGeneration) {
                    latchList(newModels, result)
                }
            }
        }
    }

    private fun latchList(
        newModels: List<ListModel<*>>,
        diffResult: DiffUtil.DiffResult
    ) {
        val previousModels = currentModels
        models = newModels
        currentModels = newModels
        notifyChange(previousModels, newModels, diffResult)
    }

    private fun notifyChange(
        previousModels: List<ListModel<*>>,
        newModels: List<ListModel<*>>,
        result: DiffUtil.DiffResult?
    ) {
        mainThreadExecutor.execute {
            resultCallback(DiffResult(previousModels, newModels, result))
        }
    }

}
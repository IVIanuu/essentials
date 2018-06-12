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

package com.ivianuu.essentials.ui.common.back

import android.os.Bundle

internal class TransactionIndexer {

    private var index = 0

    @Synchronized fun getAndIncrement(): Int {
        index++
        return index
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putInt(KEY_INDEX, index)
    }

    fun restoreInstance(savedInstanceState: Bundle) {
        index = savedInstanceState.getInt(KEY_INDEX)
    }

    private companion object {
        private const val KEY_INDEX = "TransactionIndexer.index"
    }

}
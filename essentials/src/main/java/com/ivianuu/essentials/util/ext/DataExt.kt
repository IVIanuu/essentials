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

package com.ivianuu.essentials.util.ext

import android.os.Bundle
import android.support.v4.app.Fragment
import androidx.core.os.bundleOf
import com.ivianuu.essentials.util.Data

fun Bundle.toData() = Data(this)

private val EMPTY_DATA = dataOf()

fun emptyData() = EMPTY_DATA

fun dataOf(vararg pairs: Pair<String, Any?>): Data {
    // map data to bundles
    val mappedPairs = pairs
        .map {
            if (it.second is Data) {
                it.first to (it.second as Data).toBundle()
            } else {
                it
            }
        }.toTypedArray()

    return bundleOf(*mappedPairs).toData()
}

var Fragment.args: Data
    get() = arguments?.toData() ?: emptyData()
    set(value) {
        arguments = value.toBundle()
    }
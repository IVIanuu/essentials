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

import android.support.v7.widget.SearchView

fun SearchView.doOnQueryTextChange(action: (newText: String) -> Boolean) =
    setOnQueryTextListener(onQueryTextChange = action)

fun SearchView.doOnQueryTextSubmit(action: (query: String) -> Boolean) =
    setOnQueryTextListener(onQueryTextSubmit = action)

fun SearchView.setOnQueryTextListener(
    onQueryTextChange: ((newText: String) -> Boolean)? = null,
    onQueryTextSubmit: ((query: String) -> Boolean)? = null
) : SearchView.OnQueryTextListener {
    val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean {
            return onQueryTextChange?.invoke(newText) ?: false
        }

        override fun onQueryTextSubmit(query: String): Boolean {
            return onQueryTextSubmit?.invoke(query) ?: false
        }
    }
    setOnQueryTextListener(listener)
    return listener
}
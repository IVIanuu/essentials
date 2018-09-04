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

import androidx.appcompat.widget.SearchView
import io.reactivex.Observable

fun SearchView.closes(predicate: (() -> Boolean)? = null): Observable<Unit> =
    Observable.create { e ->
        setOnCloseListener {
            if (!e.isDisposed) {
                e.onNext(Unit)
            }
            predicate?.invoke() ?: true
        }

        e.setCancellable { setOnCloseListener(null) }
    }

fun SearchView.doOnQueryTextChange(block: (newText: String) -> Boolean) =
    setOnQueryTextListener(onQueryTextChange = block)

fun SearchView.doOnQueryTextSubmit(block: (query: String) -> Boolean) =
    setOnQueryTextListener(onQueryTextSubmit = block)

fun SearchView.setOnQueryTextListener(
    onQueryTextChange: ((newText: String) -> Boolean)? = null,
    onQueryTextSubmit: ((query: String) -> Boolean)? = null
): SearchView.OnQueryTextListener {
    val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String) =
            onQueryTextChange?.invoke(newText) ?: false

        override fun onQueryTextSubmit(query: String) =
            onQueryTextSubmit?.invoke(query) ?: false
    }
    setOnQueryTextListener(listener)
    return listener
}
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

import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.AppCompatTextView
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import java.util.concurrent.Executor

fun TextView.doBeforeTextChanged(action: (s: CharSequence, start: Int, count: Int, after: Int) -> Unit) =
    addTextChangedListener(beforeTextChanged = action)

fun TextView.doOnTextChanged(action: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit) =
    addTextChangedListener(onTextChanged = action)

fun TextView.doAfterTextChanged(action: (s: Editable) -> Unit) =
    addTextChangedListener(afterTextChanged = action)

fun TextView.addTextChangedListener(
    beforeTextChanged: ((s: CharSequence, start: Int, count: Int, after: Int) -> Unit)? = null,
    onTextChanged: ((s: CharSequence, start: Int, before: Int, count: Int) -> Unit)? = null,
    afterTextChanged: ((s: Editable) -> Unit)? = null
): TextWatcher {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable) {
            afterTextChanged?.invoke(s)
        }
    }
    addTextChangedListener(textWatcher)
    return textWatcher
}

internal fun TextView.setTextFuture(
    text: CharSequence,
    params: PrecomputedTextCompat.Params = TextViewCompat.getTextMetricsParams(this),
    executor: Executor? = null
) {
    (this as? AppCompatTextView)
        ?.setTextFuture(PrecomputedTextCompat.getTextFuture(text, params, executor))
}
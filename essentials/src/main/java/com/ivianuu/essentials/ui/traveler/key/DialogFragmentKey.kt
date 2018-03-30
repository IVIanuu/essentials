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

package com.ivianuu.essentials.ui.traveler.key

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.DialogFragment

/**
 * Key for [DialogFragment]'s
 */
abstract class DialogFragmentKey {

    fun newInstance(): DialogFragment = createDialogFragment().apply {
        if (this@DialogFragmentKey is Parcelable) {
            if (arguments != null) {
                arguments!!.putParcelable(KEY_KEY, this@DialogFragmentKey)
            } else {
                arguments = Bundle().apply {
                    putParcelable(KEY_KEY, this@DialogFragmentKey)
                }
            }
        }
    }

    open val fragmentTag: String
        get() = toString()

    protected abstract fun createDialogFragment(): DialogFragment

    companion object {
        private const val KEY_KEY = "key"

        fun <T> get(dialog: DialogFragment): T? where T : DialogFragmentKey, T : Parcelable =
            dialog.arguments?.getParcelable(KEY_KEY) as T?
    }
}

fun <T> DialogFragment.key() where T : DialogFragmentKey, T : Parcelable =
    DialogFragmentKey.get<T>(this)

fun <T> DialogFragment.requireKey() where T : DialogFragmentKey, T : Parcelable =
    key<T>() ?: throw IllegalStateException("missing key")
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

package com.ivianuu.essentials.ui.common.changehandler

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils

/**
 * Applies a fragment transition to a fragment
 */
abstract class FragmentChangeHandler {

    abstract fun apply(fragment: Fragment)

    fun toBundle(): Bundle {
        val bundle = Bundle()

        bundle.putString(KEY_CLASS_NAME, javaClass.name)

        val savedState = Bundle()
        saveToBundle(savedState)
        bundle.putBundle(KEY_SAVED_STATE, savedState)

        return bundle
    }

    protected open fun saveToBundle(bundle: Bundle) {
    }

    protected open fun restoreFromBundle(bundle: Bundle) {
    }

    companion object {
        private const val KEY_CLASS_NAME = "FragmentChangeHandler.className"
        private const val KEY_SAVED_STATE = "FragmentChangeHandler.savedState"

        fun fromBundle(bundle: Bundle): FragmentChangeHandler {
            val className = bundle.getString(KEY_CLASS_NAME) ?: throw IllegalStateException()

            val changeHandler =
                ClassUtils.newInstance<FragmentChangeHandler>(
                    className
                ) ?: throw IllegalStateException()

            changeHandler.restoreFromBundle(bundle.getBundle(KEY_SAVED_STATE))

            return changeHandler
        }
    }
}

private object ClassUtils {

    fun <T> classForName(className: String, allowEmptyName: Boolean): Class<out T>? {
        if (allowEmptyName && TextUtils.isEmpty(className)) {
            return null
        }

        try {
            return Class.forName(className) as Class<out T>
        } catch (e: Exception) {
            throw RuntimeException("An exception occurred while finding class for name " + className + ". " + e.message)
        }

    }

    fun <T> newInstance(className: String): T? {
        try {
            val cls = classForName<T>(
                className,
                true
            )
            return cls?.newInstance()
        } catch (e: Exception) {
            throw RuntimeException("An exception occurred while creating a new instance of " + className + ". " + e.message)
        }

    }

}
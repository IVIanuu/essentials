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

import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.doOnActivityCreated
import com.ivianuu.essentials.util.ext.registerFragmentLifecycleCallbacks

/**
 * @author Manuel Wrage (IVIanuu)
 */
object FragmentTransitionManager {

    private const val KEY_CHANGE_HANDLER = "FragmentTransitionManager.changeHandler"

    private val changeHandlersMap
            = mutableMapOf<Fragment, FragmentChangeHandler>()

    fun init(application: Application) {
        application.doOnActivityCreated { activity, _ ->
            if (activity is FragmentActivity) {
                attachFragmentCallbacks(
                    activity
                )
            }
        }
    }

    fun setChangeHandler(fragment: Fragment, changeHandler: FragmentChangeHandler?) {
        if (changeHandler != null) {
            changeHandlersMap[fragment] = changeHandler
        } else {
            changeHandlersMap.remove(fragment)
        }
    }

    fun getChangeHandler(from: Fragment): FragmentChangeHandler? {
        return changeHandlersMap[from]
    }

    private fun attachFragmentCallbacks(activity: FragmentActivity) {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(true,
            onFragmentPreCreated = { _: FragmentManager, f: Fragment, savedInstanceState: Bundle? ->
                // restore
                if (savedInstanceState != null && !changeHandlersMap.contains(f)) {
                    savedInstanceState.getBundle(KEY_CHANGE_HANDLER)?.let {
                        val changeHandler =
                            FragmentChangeHandler.fromBundle(
                                it
                            )
                        changeHandlersMap[f] = changeHandler
                        d { "restored change handler $changeHandler" }
                    }
                }

                // apply
                changeHandlersMap[f]
                    ?.also { d { "apply change handler $it to $f" } }
                    ?.apply(f)
            },
            onFragmentSaveInstanceState = { _: FragmentManager, f: Fragment, outState: Bundle ->
                // persist
                changeHandlersMap[f]?.let {
                    d { "persist change handler $it" }
                    outState.putBundle(KEY_CHANGE_HANDLER, it.toBundle())
                }
            },
            onFragmentDetached = { _: FragmentManager, f: Fragment ->
                // clear
                changeHandlersMap.remove(f)
            }
        )
    }
}
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

package com.ivianuu.essentials.ui.compat

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View

/**
 * A dialog fragment with a additional lifecycle for views
 * This can be removed on android support library version 28.0.0
 */
open class ViewLifecycleDialogFragment : DialogFragment() {

    val viewLifecycleOwner: LifecycleOwner
        get() = _viewLifecycleOwner
    private val _viewLifecycleOwner = ViewLifecycleOwner()

    private var viewCreated = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated = true
        _viewLifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        if (viewCreated) {
            _viewLifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewCreated) {
            _viewLifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewCreated) {
            _viewLifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (viewCreated) {
            _viewLifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDestroyView() {
        if (viewCreated) {
            viewCreated = false
            _viewLifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        super.onDestroyView()
    }

}
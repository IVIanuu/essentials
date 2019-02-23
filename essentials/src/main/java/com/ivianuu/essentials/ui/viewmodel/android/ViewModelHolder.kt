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

package com.ivianuu.essentials.ui.viewmodel.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ivianuu.essentials.ui.viewmodel.ViewModelManager
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.util.ParceledSavedState
import com.ivianuu.essentials.util.toParceledSavedState

/**
 * Holder for [ViewModelManager]s
 */
class ViewModelHolder : Fragment(), ViewModelManagerOwner {

    override val viewModelManager = ViewModelManager()

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelManager.restoreState(
            savedInstanceState
                ?.getParcelable<ParceledSavedState>(KEY_VIEW_MODELS)
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            KEY_VIEW_MODELS,
            viewModelManager.saveState().toParceledSavedState()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelManager.destroy()
    }

    companion object {
        private const val TAG = "com.ivianuu.essentials.ui.viewmodel.android.ViewModelHolder"

        private const val KEY_VIEW_MODELS = "ViewModelHolder.viewModels"

        internal fun get(fm: FragmentManager): ViewModelManagerOwner {
            var holder = fm.findFragmentByTag(TAG) as? ViewModelHolder
            if (holder == null) {
                holder = ViewModelHolder()
                fm.beginTransaction()
                    .add(holder, TAG)
                    .commitNow()
            }

            return holder
        }

    }

}

val FragmentActivity.viewModelManagerOwner: ViewModelManagerOwner
    get() = ViewModelHolder.get(supportFragmentManager)

val FragmentActivity.viewModelManager: ViewModelManager
    get() = viewModelManagerOwner.viewModelManager

val Fragment.viewModelManagerOwner: ViewModelManagerOwner
    get() = ViewModelHolder.get(childFragmentManager)

val Fragment.viewModelManager: ViewModelManager
    get() = viewModelManagerOwner.viewModelManager
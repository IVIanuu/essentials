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

package com.ivianuu.essentials.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Base dialog controller
 */
abstract class BaseDialogController @JvmOverloads constructor(args: Bundle = Bundle()) : BaseController(args) {

    var dialog: Dialog? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        dialog = onCreateDialog(savedViewState).apply {
            ownerActivity = requireActivity()
            setOnDismissListener {
                requireRouter().popController(this@BaseDialogController)
            }
        }
        //stub view
        return View(activity)
    }

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)
        val dialogState = savedViewState.getBundle(KEY_DIALOG_SAVED_STATE)
        if (dialogState != null) {
            onRestoreInstanceState(dialogState)
        }
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        dialog?.onSaveInstanceState()?.let {
            outState.putBundle(KEY_DIALOG_SAVED_STATE, it)
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        dialog?.show()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        dialog?.hide()
    }

    override fun onDestroyView(view: View) {
        dialog?.let {
            it.setOnDismissListener(null)
            it.dismiss()
        }
        dialog = null
        super.onDestroyView(view)
    }

    protected abstract fun onCreateDialog(savedViewState: Bundle?): Dialog

    private companion object {
        private const val KEY_DIALOG_SAVED_STATE = "dialog_saved_state"
    }
}
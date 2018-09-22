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

package com.ivianuu.essentials.sample.ui

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.traveler.Command
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.plugin.NavigatorPlugin

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class ShowMaterialDialog(val builder: MaterialDialog.Builder.() -> Unit) : Command

class MaterialDialogNavigatorPlugin(private val context: Context) : NavigatorPlugin {

    override fun handles(command: Command) = command is ShowMaterialDialog

    override fun apply(command: Command) {
        if (command !is ShowMaterialDialog) return
        MaterialDialog.Builder(context)
            .apply(command.builder)
            .show()
    }
}

fun Router.showDialog(builder: MaterialDialog.Builder.() -> Unit) {
    executeCommands(ShowMaterialDialog(builder))
}
/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.backup

import com.ivianuu.essentials.backup.BackupAndRestoreAction.BackupData
import com.ivianuu.essentials.backup.BackupAndRestoreAction.RestoreData
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UiStateBinding
@Given
fun backupAndRestoreState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial BackupAndRestoreState = BackupAndRestoreState,
    @Given actions: Actions<BackupAndRestoreAction>,
    @Given backupCreator: BackupCreator,
    @Given backupApplier: BackupApplier,
    @Given toaster: Toaster
): StateFlow<BackupAndRestoreState> = scope.state(initial) {
    actions
        .filterIsInstance<BackupData>()
        .onEach {
            backupCreator()
                .onFailure {
                    it.printStackTrace()
                    toaster.showToast(R.string.es_backup_error)
                }
        }
        .launchIn(this)
    actions
        .filterIsInstance<RestoreData>()
        .onEach {
            backupApplier()
                .onFailure {
                    it.printStackTrace()
                    toaster.showToast(R.string.es_restore_error)
                }
        }
        .launchIn(this)
}

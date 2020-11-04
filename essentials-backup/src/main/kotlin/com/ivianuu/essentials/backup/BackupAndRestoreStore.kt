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
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.CoroutineScope

@Binding
fun CoroutineScope.BackupAndRestoreStore(
    backupData: backupData,
    restoreData: restoreData,
    showToastRes: showToastRes,
) = store<BackupAndRestoreState, BackupAndRestoreAction>(BackupAndRestoreState) {
    for (action in this) {
        when (action) {
            BackupData -> {
                backupData()
                    .onFailure {
                        it.printStackTrace()
                        showToastRes(R.string.es_backup_error)
                    }
            }
            RestoreData -> {
                restoreData()
                    .onFailure {
                        it.printStackTrace()
                        showToastRes(R.string.es_restore_error)
                    }
            }
        }.exhaustive
    }
}

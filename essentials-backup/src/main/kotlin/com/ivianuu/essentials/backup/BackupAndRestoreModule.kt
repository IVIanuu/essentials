package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.unscoped

@Module
internal fun backupAndRestoreModule() {
    installIn<ApplicationComponent>()
    unscoped<@BackupDir String> { "${get<@DataDir String>()}/backups" }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
internal annotation class BackupDir

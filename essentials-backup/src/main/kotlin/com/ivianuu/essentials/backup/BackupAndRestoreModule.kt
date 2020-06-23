package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.transient

@Module
internal fun backupAndRestoreModule() {
    installIn<ApplicationComponent>()
    transient<@BackupDir String> { dataDir: @DataDir String -> "$dataDir/backups" }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
internal annotation class BackupDir

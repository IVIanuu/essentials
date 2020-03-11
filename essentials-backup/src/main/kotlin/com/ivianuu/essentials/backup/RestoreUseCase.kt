package com.ivianuu.essentials.backup

import android.app.Activity
import android.content.Intent
import com.ivianuu.essentials.activityresult.ActivityResult
import com.ivianuu.essentials.activityresult.ActivityResultRoute
import com.ivianuu.essentials.android.ui.navigation.NavigatorState
import com.ivianuu.essentials.android.util.Toaster
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Factory
class RestoreUseCase(
    private val activity: Activity,
    private val dispatchers: AppCoroutineDispatchers,
    private val navigator: NavigatorState,
    private val processRestarter: ProcessRestarter,
    private val toaster: Toaster
) {

    suspend fun restore() = withContext(dispatchers.io) {
        val uri = navigator.push<ActivityResult>(
            ActivityResultRoute(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "application/zip"
                    }, ""
                )
            )
        )?.data?.data ?: return@withContext

        try {
            val buffer = ByteArray(8192)

            val zipInputStream = ZipInputStream(
                activity.contentResolver.openInputStream(uri)!!.buffered()
            )

            val targetDirectory =
                File("${activity.applicationInfo.dataDir}/prefs")

            var entry: ZipEntry? = zipInputStream.nextEntry
            while (entry != null) {
                val file = File(targetDirectory, entry.name)
                val dir = if (entry.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs())
                    throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
                if (entry.isDirectory)
                    continue
                FileOutputStream(file).use { fileOutputStream ->
                    var count = zipInputStream.read(buffer)
                    while (count != -1) {
                        fileOutputStream.write(buffer, 0, count)
                        count = zipInputStream.read(buffer)
                    }
                }
                entry = zipInputStream.nextEntry
            }

            withContext(dispatchers.main) { processRestarter.restartProcess() }
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_share_backup)
        }
    }
}

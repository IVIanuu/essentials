package com.ivianuu.essentials.backup

import android.content.Intent
import androidx.activity.ComponentActivity
import com.ivianuu.essentials.processrestart.RestartProcessUseCase
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.StartActivityForResultUseCase
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Transient
internal class RestoreDataUseCase(
    private val activity: ComponentActivity,
    private val dispatchers: AppCoroutineDispatchers,
    private val restartProcessUseCase: RestartProcessUseCase,
    private val startActivityForResultUseCase: StartActivityForResultUseCase,
    private val toaster: Toaster
) {

    suspend operator fun invoke() = withContext(dispatchers.io) {
        val uri = startActivityForResultUseCase(
            Intent.createChooser(
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "application/zip"
                }, ""
            )
        ).data?.data ?: return@withContext

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

            restartProcessUseCase()
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_share_backup)
        }
    }
}

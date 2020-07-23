package com.ivianuu.essentials.backup

import android.content.Intent
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.processrestart.restartProcess
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.runCatchingAndLog
import com.ivianuu.essentials.util.startActivityForResult
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Reader
internal suspend fun restoreData() = runCatchingAndLog {
    withContext(dispatchers.io) {
        val uri = startActivityForResult(
            Intent.createChooser(
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "application/zip"
                }, ""
            )
        ).data?.data ?: return@withContext

        val buffer = ByteArray(8192)

        val zipInputStream = ZipInputStream(
            applicationContext.contentResolver.openInputStream(uri)!!.buffered()
        )

        val targetDirectory = File(given<PrefsDir>())

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

        restartProcess()
    }
}

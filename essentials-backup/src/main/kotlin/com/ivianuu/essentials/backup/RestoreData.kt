package com.ivianuu.essentials.backup

import android.content.Intent
import com.github.michaelbull.result.Result
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.processrestart.restartProcess
import com.ivianuu.essentials.util.IODispatcher
import com.ivianuu.essentials.util.runCatchingAndLog
import com.ivianuu.essentials.util.startActivityForIntentResult
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@FunBinding
suspend fun restoreData(
    applicationContext: ApplicationContext,
    ioDispatcher: IODispatcher,
    prefsDir: PrefsDir,
    restartProcess: restartProcess,
    startActivityForIntentResult: startActivityForIntentResult,
): Result<Unit, Throwable> = runCatchingAndLog {
    withContext(ioDispatcher) {
        val uri = startActivityForIntentResult(
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

        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            val file = File(prefsDir, entry.name)
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

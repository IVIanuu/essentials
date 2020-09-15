package com.ivianuu.essentials.backup

import android.content.Intent
import androidx.core.content.FileProvider
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.ui.navigation.ActivityRoute
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.runCatchingAndLog
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Reader
internal suspend fun backupData() = runCatchingAndLog {
    withContext(dispatchers.io) {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
        val backupFileName = "backup_${dateFormat.format(Date())}"

        val backupFile = given<BackupDir>().resolve("$backupFileName.zip")
            .also {
                it.parentFile.mkdirs()
                it.createNewFile()
            }

        val dest = FileOutputStream(backupFile)
        val out = ZipOutputStream(BufferedOutputStream(dest))

        given<BackupFiles>()
            .map { it() }
            .flatMap { it.walkTopDown() }
            .forEach { file ->
                val content = file.bufferedReader()
                val entry = ZipEntry(file.name)
                out.putNextEntry(entry)
                content.forEachLine {
                    out.write(it.toByteArray())
                }
                content.close()
            }

        out.close()

        val uri =
            FileProvider.getUriForFile(
                androidApplicationContext,
                given<BuildInfo>().packageName,
                backupFile
            )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/zip"
        intent.data = uri
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        navigator.push(ActivityRoute { Intent.createChooser(intent, "Share File") })
    }
}

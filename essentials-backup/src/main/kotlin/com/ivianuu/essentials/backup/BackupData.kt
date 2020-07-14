package com.ivianuu.essentials.backup

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.ui.navigation.ActivityRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Reader
internal suspend fun backupData() = runCatching {
    withContext(dispatchers.io) {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
        val backupFileName = "backup_${dateFormat.format(Date())}"

        val backupFile = File("${given<BackupDir>()}/$backupFileName.zip")
        backupFile.mkdirs()
        backupFile.createNewFile()

        val dest = FileOutputStream(backupFile)
        val out = ZipOutputStream(BufferedOutputStream(dest))

        val prefsFile = File(given<PrefsDir>())
        val prefsToBackup = prefsFile.listFiles()

        for (file in prefsToBackup) {
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
                applicationContext,
                given<BuildInfo>().packageName,
                backupFile
            )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/zip"
        intent.data = uri
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        given<Navigator>().push(ActivityRoute { Intent.createChooser(intent, "Share File") })
    }
}

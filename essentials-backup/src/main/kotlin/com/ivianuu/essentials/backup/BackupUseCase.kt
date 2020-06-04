package com.ivianuu.essentials.backup

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import com.ivianuu.essentials.ui.navigation.ActivityRoute
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Transient
class BackupUseCase(
    private val application: Application,
    private val buildInfo: BuildInfo,
    private val dispatchers: AppCoroutineDispatchers,
    private val navigator: NavigatorState,
    private val toaster: Toaster
) {

    @SuppressLint("SimpleDateFormat")
    suspend fun backup() = withContext(dispatchers.io) {
        try {
            val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
            val backupFileName = "backup_${dateFormat.format(Date())}"
            val dataPath = application.applicationInfo.dataDir

            val backupFile = File("$dataPath/backups/$backupFileName.zip")
            backupFile.mkdirs()
            backupFile.createNewFile()

            val dest = FileOutputStream(backupFile)
            val out = ZipOutputStream(BufferedOutputStream(dest))

            val prefsDir = File("$dataPath/prefs")
            val prefsToBackup = prefsDir.listFiles()

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

            withContext(dispatchers.main) {
                val uri =
                    FileProvider.getUriForFile(
                        application,
                        buildInfo.packageName,
                        backupFile
                    )
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "application/zip"
                intent.data = uri
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                navigator.push(ActivityRoute { Intent.createChooser(intent, "Share File") })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_backup_error)
        }
    }
}

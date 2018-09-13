package com.ivianuu.essentials.ui.common

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.R
import com.ivianuu.essentials.data.app.AppInfo
import com.ivianuu.essentials.data.app.AppStore
import com.ivianuu.essentials.ui.base.BaseDialogFragment
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.essentials.util.ext.string
import kotlinx.coroutines.launch
import javax.inject.Inject

@Destination(AppPickerDialog::class)
data class AppPickerDestination(
    val title: CharSequence? = null,
    val launchableOnly: Boolean = false,
    override val resultCode: Int = RequestCodeGenerator.generate()
) : ResultDestination<AppInfo>

/**
 * App picker
 */
class AppPickerDialog : BaseDialogFragment() {

    @Inject lateinit var appStore: AppStore

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val apps = mutableListOf<AppInfo>()

        val destination = appPickerDestination()

        val dialog = MaterialDialog.Builder(requireContext())
            .title(destination.title ?: string(R.string.dialog_title_app_picker))
            .negativeText(R.string.action_cancel)
            .autoDismiss(false)
            .onNegative { _, _ -> router.exit() }
            .items()
            .itemsCallback { _, _, position, _ ->
                val app = apps[position]
                router.exitWithResult(destination.resultCode, app)
            }
            .build()

        launch {
            val newApps = if (destination.launchableOnly) {
                appStore.launchableApps()
            } else {
                appStore.installedApps()
            }
            apps.clear()
            apps.addAll(newApps)
            dialog.setItems(*apps.map { it.appName }.toTypedArray())
        }

        return dialog
    }
}
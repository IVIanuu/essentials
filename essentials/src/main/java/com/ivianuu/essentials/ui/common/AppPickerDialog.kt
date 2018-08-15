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
import com.ivianuu.essentials.util.ext.MAIN
import com.ivianuu.essentials.util.ext.string
import com.uber.autodispose.autoDisposable
import com.uber.autodispose.subscribeBy
import javax.inject.Inject

@Destination(AppPickerDialog::class)
data class AppPickerDestination(
    val title: String? = null,
    override val resultCode: Int
) : ResultDestination

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

        appStore.launchableApps()
            .observeOn(MAIN)
            .autoDisposable(scopeProvider)
            .subscribeBy {
                apps.clear()
                apps.addAll(it)
                dialog.setItems(*apps.map { it.appName }.toTypedArray())
            }

        return dialog
    }
}
package com.ivianuu.essentials.ui.common

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.androidktx.fragment.app.string
import com.ivianuu.essentials.R
import com.ivianuu.essentials.data.app.AppInfo
import com.ivianuu.essentials.data.app.AppStore
import com.ivianuu.essentials.ui.base.BaseDialogFragment
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.ui.traveler.key.BaseFragmentDestination
import com.ivianuu.essentials.ui.traveler.key.destination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import javax.inject.Inject

@Parcelize
data class AppPickerDestination(
    val title: CharSequence? = null,
    val launchableOnly: Boolean = false,
    override val resultCode: Int = RequestCodeGenerator.generate()
) : BaseFragmentDestination(AppPickerDialog::class), ResultDestination<AppInfo>

/**
 * App picker
 */
// todo remove/ move to somewhere else
class AppPickerDialog : BaseDialogFragment() {

    @Inject lateinit var appStore: AppStore

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val apps = mutableListOf<AppInfo>()

        val destination = destination<AppPickerDestination>()

        val dialog = MaterialDialog.Builder(requireContext())
            .title(destination.title ?: string(R.string.dialog_title_app_picker))
            .negativeText(R.string.action_cancel)
            .autoDismiss(false)
            .onNegative { _, _ -> router.goBack() }
            .items()
            .itemsCallback { _, _, position, _ ->
                val app = apps[position]
                router.goBackWithResult(destination.resultCode, app)
            }
            .build()

        coroutineScope.launch {
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
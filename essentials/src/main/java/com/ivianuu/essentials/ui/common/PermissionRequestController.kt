package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.requestPermissions
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.ext.isM
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PermissionRequestKey(
    val resultCode: Int,
    val permissions: Set<String>,
    val requestCode: Int
) : ControllerKey(PermissionRequestController::class, NavOptions().dialog())

/**
 * Permission request controller
 */
class PermissionRequestController : EsController() {

    private val key by bindKey<PermissionRequestKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isM()) {
            requestPermissions(key.permissions.toTypedArray(), key.requestCode)
        }
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ) = View(activity)

    override fun onAttach(view: View) {
        super.onAttach(view)
        if (!isM()) {
            travelerRouter.goBackWithResult(
                key.resultCode, PermissionResult(
                    key.requestCode,
                    key.permissions,
                    key.permissions
                        .map { PackageManager.PERMISSION_GRANTED }
                        .toIntArray()
                ))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        travelerRouter.goBackWithResult(
            key.resultCode,
            PermissionResult(requestCode, permissions.toSet(), grantResults)
        )
    }

}

data class PermissionResult(
    val requestCode: Int,
    val permissions: Set<String>,
    val grantResults: IntArray
) {
    val allGranted get() = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
}
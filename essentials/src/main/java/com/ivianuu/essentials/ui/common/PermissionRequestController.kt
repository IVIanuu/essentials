package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.permission.PermissionCallback
import com.ivianuu.director.permission.registerPermissionCallback
import com.ivianuu.director.permission.requestPermissions
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.inject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PermissionRequestKey(
    val resultCode: Int,
    val permissions: Set<String>,
    val requestCode: Int
) : ControllerKey(::PermissionRequestController, NavOptions().dialog())

/**
 * Permission request controller
 */
class PermissionRequestController : EsController() {

    private val key by inject<PermissionRequestKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerPermissionCallback(key.requestCode, object : PermissionCallback {
                override fun onRequestPermissionsResult(
                    requestCode: Int,
                    permissions: Array<out String>,
                    grantResults: IntArray
                ) {
                    travelerRouter.goBackWithResult(
                        key.resultCode,
                        PermissionResult(requestCode, permissions.toSet(), grantResults)
                    )
                }
            })

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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

}

data class PermissionResult(
    val requestCode: Int,
    val permissions: Set<String>,
    val grantResults: IntArray
)

val PermissionResult.allGranted: Boolean get() = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
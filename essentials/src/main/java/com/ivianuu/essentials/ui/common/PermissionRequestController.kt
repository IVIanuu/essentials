package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.essentials.util.ext.isM
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PermissionRequestKey(
    override val resultCode: Int,
    val permissions: Array<String>,
    val requestCode: Int = RequestCodeGenerator.generate()
) : ControllerKey(PermissionRequestController::class, NavOptions().dialog()),
    ResultKey<PermissionResult> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionRequestKey

        if (resultCode != other.resultCode) return false
        if (!Arrays.equals(permissions, other.permissions)) return false
        if (requestCode != other.requestCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resultCode
        result = 31 * result + Arrays.hashCode(permissions)
        result = 31 * result + requestCode
        return result
    }
}

/**
 * Permission request controller
 */
class PermissionRequestController : BaseController() {

    private val key by bindKey<PermissionRequestKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isM()) {
            requestPermissions(key.permissions, key.requestCode)
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
            PermissionResult(requestCode, permissions, grantResults)
        )
    }

}

data class PermissionResult(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
) {

    val allGranted get() = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionResult

        if (requestCode != other.requestCode) return false
        if (!Arrays.equals(permissions, other.permissions)) return false
        if (!Arrays.equals(grantResults, other.grantResults)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestCode
        result = 31 * result + Arrays.hashCode(permissions)
        result = 31 * result + Arrays.hashCode(grantResults)
        return result
    }
}
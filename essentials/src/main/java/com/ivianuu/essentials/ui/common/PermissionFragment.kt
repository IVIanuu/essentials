package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.result.goBackWithResult
import java.util.*

@Destination(PermissionFragment::class)
data class PermissionDestination(
    override val resultCode: Int,
    val permissions: Array<String>,
    val requestCode: Int = RequestCodeGenerator.generate()
) : ResultDestination<PermissionResult> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionDestination

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
 * Activity result activity
 */
class PermissionFragment : BaseFragment() {

    private val destination by bindPermissionDestination()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(destination.permissions, destination.requestCode)
        } else {
            router.goBackWithResult(
                destination.resultCode, PermissionResult(
                destination.requestCode,
                destination.permissions,
                destination.permissions
                    .map { PackageManager.PERMISSION_GRANTED }
                    .toIntArray()
            ))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        router.goBackWithResult(
            destination.resultCode,
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
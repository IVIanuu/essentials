package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.key.FragmentKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PermissionKey(
    override val resultCode: Int,
    val permissions: Array<String>,
    val requestCode: Int = RequestCodeGenerator.generate()
) : FragmentKey(PermissionFragment::class),
    ResultKey<PermissionResult> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionKey

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

    private val key by bindKey<PermissionKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(key.permissions, key.requestCode)
        } else {
            router.goBackWithResult(
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
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        router.goBackWithResult(
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
package com.ivianuu.essentials.sample

import android.os.StrictMode
import com.ivianuu.essentials.app.AppInitializerBinding
import com.ivianuu.injekt.FunBinding

@AppInitializerBinding
@FunBinding
fun initializeStrictModule() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .build()
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectAll()
            .build()
    )
}

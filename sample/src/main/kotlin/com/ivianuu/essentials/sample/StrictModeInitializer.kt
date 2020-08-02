package com.ivianuu.essentials.sample

import android.os.StrictMode
import com.ivianuu.essentials.app.AppInitializer

@AppInitializer
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

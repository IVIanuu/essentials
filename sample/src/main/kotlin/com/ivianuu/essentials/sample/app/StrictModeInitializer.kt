package com.ivianuu.essentials.sample.app

import android.os.StrictMode
import com.ivianuu.essentials.app.BindAppInitializer
import com.ivianuu.injekt.Transient

@BindAppInitializer
@Transient
class StrictModeInitializer {

    init {
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

}

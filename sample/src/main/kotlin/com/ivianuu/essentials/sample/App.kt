// injekt-incremental-fix 1614768578766 injekt-end
package com.ivianuu.essentials.sample

import android.app.Application
import com.ivianuu.essentials.app.initializeEssentials

class App : Application() {
    override fun onCreate() {
        initializeEssentials()
        super.onCreate()
    }
}

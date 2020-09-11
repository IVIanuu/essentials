package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.EsApp
import com.ivianuu.injekt.initializeInjekt

class App : EsApp() {
    override fun onCreate() {
        initializeInjekt()
        super.onCreate()
    }
}
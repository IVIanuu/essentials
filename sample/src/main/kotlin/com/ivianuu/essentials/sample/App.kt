package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.EsApp
import com.ivianuu.injekt.component.initializeApp

class App : EsApp() {
    override fun onCreate() {
        println()
        initializeApp()
        super.onCreate()
    }
}

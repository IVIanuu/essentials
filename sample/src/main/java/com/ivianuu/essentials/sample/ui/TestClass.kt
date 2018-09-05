package com.ivianuu.essentials.sample.ui

import android.app.Application
import android.content.Context
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class TestClass @Inject constructor(private val context: Context) {

    @Inject lateinit var app: Application


}
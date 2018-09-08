package com.ivianuu.essentials.injection.worker

import androidx.work.Worker
import dagger.android.AndroidInjector

/**
 * Has worker injector
 */
interface HasWorkerInjector {
    fun workerInjector(): AndroidInjector<Worker>
}
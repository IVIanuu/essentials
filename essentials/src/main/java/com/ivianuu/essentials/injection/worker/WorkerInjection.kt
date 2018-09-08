package com.ivianuu.essentials.injection.worker

import androidx.work.Worker

/**
 * Worker injection
 */
object WorkerInjection {

    fun inject(worker: Worker) {
        val application = worker.applicationContext
        if (application !is HasWorkerInjector) {
            throw RuntimeException("${application.javaClass.canonicalName} does not implement ${HasWorkerInjector::class.java.canonicalName}")
        }

        val workerInjector = application.workerInjector()
        workerInjector.inject(worker)
    }
}
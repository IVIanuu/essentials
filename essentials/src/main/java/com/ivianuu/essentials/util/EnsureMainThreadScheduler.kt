/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.ivianuu.essentials.util.ext.isMainThread
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.TimeUnit

/**
 * Main thread scheduler which only use handler.post if were not on the main thread
 */
class EnsureMainThreadScheduler : Scheduler() {

    private val handler = Handler(Looper.getMainLooper())

    override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val run = RxJavaPlugins.onSchedule(run)
        val scheduled = ScheduledRunnable(handler, run)

        if (delay == 0L) {
            if (isMainThread) {
                scheduled.run()
            } else {
                handler.post(scheduled)
            }
        } else {
            handler.postDelayed(scheduled, unit.toMillis(delay))
        }

        return scheduled
    }

    override fun createWorker(): Scheduler.Worker {
        return MainThreadWorker(handler)
    }

    private class MainThreadWorker(private val handler: Handler) : Scheduler.Worker() {

        @Volatile private var disposed: Boolean = false

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            if (disposed) {
                return Disposables.disposed()
            }

            val run = RxJavaPlugins.onSchedule(run)

            val scheduled = ScheduledRunnable(handler, run)

            if (delay == 0L) {
                if (isMainThread) {
                    scheduled.run()
                } else {
                    val message = Message.obtain(handler, scheduled)
                    message.obj = this
                    handler.sendMessage(message)
                }
            } else {
                val message = Message.obtain(handler, scheduled)
                message.obj = this
                handler.sendMessageDelayed(message, unit.toMillis(delay))
            }

            // Re-check disposed state for removing in case we were racing a call to dispose().
            if (disposed) {
                handler.removeCallbacks(scheduled)
                return Disposables.disposed()
            }

            return scheduled
        }

        override fun dispose() {
            disposed = true
            handler.removeCallbacksAndMessages(this /* token */)
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }

    private class ScheduledRunnable internal constructor(
        private val handler: Handler,
        private val delegate: Runnable
    ) : Runnable, Disposable {

        @Volatile private var disposed: Boolean = false

        override fun run() {
            try {
                delegate.run()
            } catch (t: Throwable) {
                RxJavaPlugins.onError(t)
            }
        }

        override fun dispose() {
            disposed = true
            handler.removeCallbacks(this)
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }

    companion object {
        val INSTANCE = EnsureMainThreadScheduler()
    }
}
package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.AppWorker
import com.ivianuu.essentials.app.BindAppWorker
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@BindAppWorker
@Transient
class Test(
    factory: PrefBoxFactory
) : AppWorker {

    private val a by lazy { factory.create("a") { 0 } }
    private val b by lazy { factory.create("b") { 0 } }

    override suspend fun run() {
        coroutineScope {
            while (isActive) {
                a.updateData {
                    println("start slow $it")
                    b.updateData {
                        println("start fast $it")
                        delay(1000)
                        println("fast $it")
                        it + 1
                    }
                    println("slow $it")
                    it + 1
                }

                delay(3000)
            }
        }
    }

}

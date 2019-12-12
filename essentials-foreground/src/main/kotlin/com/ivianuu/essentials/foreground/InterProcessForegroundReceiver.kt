/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.foreground

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.ivianuu.essentials.messaging.EsBroadcastReceiver
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import kotlin.reflect.KClass

class InterProcessForegroundReceiver : EsBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val type =
            intent.getParcelableExtra<ParceledType<out ForegroundComponent>>(KEY_TYPE)?.asType()
                ?: return
        val foregroundComponent = component.get(type = type)
        val foregroundManager = component.get<ForegroundManager>()
        when (intent.action) {
            ACTION_START -> foregroundManager.startForeground(foregroundComponent)
            ACTION_STOP -> foregroundManager.stopForeground(foregroundComponent)
        }
    }

    internal companion object {
        private const val ACTION_START =
            "com.ivianuu.essentials.foreground.INTER_PROCESS_START_FOREGROUND"
        private const val ACTION_STOP =
            "com.ivianuu.essentials.foreground.INTER_PROCESS_STOP_FOREGROUND"
        private const val KEY_TYPE = "type"

        fun start(
            context: Context,
            type: Type<out ForegroundComponent>
        ) {
            send(context, type, ACTION_START)
        }

        fun stop(
            context: Context,
            type: Type<out ForegroundComponent>
        ) {
            send(context, type, ACTION_STOP)
        }

        private fun send(context: Context, type: Type<out ForegroundComponent>, action: String) {
            context.sendBroadcast(
                Intent().apply {
                    this.action = action
                    component = ComponentName(context, InterProcessForegroundReceiver::class.java)
                    putExtra(KEY_TYPE, type.asParceledType())
                }
            )
        }
    }
}

@Parcelize
@TypeParceler<KClass<*>, KClassParceler>
private class ParceledType<T>(
    val raw: KClass<*>,
    val isNullable: Boolean,
    val parameters: Array<out ParceledType<*>>
) : Parcelable {
    fun asType(): Type<T> = typeOf(
        raw = raw,
        isNullable = isNullable,
        parameters = *parameters.map { it.asType() }.toTypedArray()
    )
}

private object KClassParceler : Parceler<KClass<*>> {
    override fun create(parcel: Parcel): KClass<*> = Class.forName(parcel.readString()!!).kotlin
    override fun KClass<*>.write(parcel: Parcel, flags: Int) {
        parcel.writeString(java.name)
    }
}

private fun <T> Type<T>.asParceledType(): ParceledType<T> = ParceledType(
    raw = raw,
    isNullable = isNullable,
    parameters = *parameters.map { it.asParceledType() }.toTypedArray()
)

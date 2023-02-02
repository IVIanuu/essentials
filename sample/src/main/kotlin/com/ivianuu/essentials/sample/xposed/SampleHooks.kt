/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import android.bluetooth.BluetoothGattCharacteristic
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.xposed.Hooks
import com.ivianuu.essentials.xposed.arg
import com.ivianuu.essentials.xposed.hookAllMethods
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable
import java.text.DecimalFormat

context(Logger) @Provide fun sampleHooks() = Hooks {
  if (packageName.value != "com.apelabs.wapp") return@Hooks

  hookAllMethods(
    BluetoothGattCharacteristic::class,
    "setValue"
  ) {
    before {
      val message = arg<ByteArray>(0)
      log { "message ${message.contentToString()}" }
      if (message.getOrNull(0)?.toInt() == 68) {
        val red = message[5].toColorFloat()
        val green = message[6].toColorFloat()
        val blue = message[7].toColorFloat()
        val white = message[8].toColorFloat()

        log {
          "color = LightColor(${red.toColorString()}f, " +
              "${green.toColorString()}f, " +
              "${blue.toColorString()}f, " +
              "${white.toColorString()}f) " +
              "${message.contentToString()}"
        }
      }
    }
  }
}

val map = mapOf(
  "Candlelight Classic" to LightColor(0.72f, 0.19f, 0.00f, 1.00f),
  "Candlelight Rustic" to LightColor(0.76f, 0.28f, 0.00f, 1.00f),
  "Soft Amber" to LightColor(0.53f, 0.16f, 0.00f, 1.00f),
  "Blush Pink" to LightColor(0.46f, 0.00f, 0.19f, 1.00f),
  "Lilac" to LightColor(0.25f, 0.00f, 0.98f, 1.00f),
  "Seafoam" to LightColor(0.07f, 0.38f, 0.00f, 1.00f),
  "Mint" to LightColor(0.09f, 0.62f, 0.00f, 1.00f),
  "Pastel Blue" to LightColor(0.00f, 0.58f, 1.00f, 0.20f),
  "Turquoise" to LightColor(0.00f, 1.00f, 0.42f, 0.00f),
  "Red" to LightColor(1.00f, 0.00f, 0.00f, 0.00f),
  "Green" to LightColor(0.00f, 1.00f, 0.00f, 0.00f),
  "Blue" to LightColor(0.00f, 0.00f, 1.00f, 0.00f),
  "Amber" to LightColor(1.00f, 0.75f, 0.00f, 0.03f),
  "Amber White" to LightColor(0.40f, 0.10f, 0.00f, 1.00f),
  "Warm White" to LightColor(0.25f, 0.00f, 0.00f, 1.00f),
  "White" to LightColor(0.19f, 0.00f, 0.00f, 1.00f),
  "Cool White" to LightColor(0.00f, 0.19f, 0.08f, 1.00f),
  "Violet" to LightColor(0.14f, 0.00f, 1.00f, 0.00f),
  "Purple" to LightColor(0.11f, 0.00f, 1.00f, 0.06f),
  "Lavender" to LightColor(0.15f, 0.00f, 1.00f, 0.58f),
  "Magenta" to LightColor(0.85f, 0.00f, 1.00f, 0.00f),
  "Neon Pink" to LightColor(1.00f, 0.00f, 0.34f, 0.33f),
  "Coral" to LightColor(1.00f, 0.05f, 0.00f, 0.06f),
  "Yellow" to LightColor(1.00f, 0.79f, 0.00f, 0.33f),
  "Orange" to LightColor(1.00f, 0.60f, 0.00f, 0.00f),
  "Peach" to LightColor(0.72f, 0.00f, 0.00f, 1.00f),
  "Lime" to LightColor(0.18f, 1.00f, 0.00f, 0.00f),
  "Slate Blue" to LightColor(0.00f, 0.45f, 1.00f, 0.38f),
  "Moonlight Blue" to LightColor(0.00f, 0.55f, 1.00f, 0.00f)
)

private fun Float.toColorByte(): Byte = lerp(0, 255, this)
  .let { if (it > 127) it - 256 else it }
  .toByte()

private fun Byte.toColorFloat(): Float {
  var tmp = toInt()
  if (tmp < 0) tmp += 256
  return tmp / 255.0f
}

private val format = DecimalFormat("0.00")

private fun Float.toColorString(): String = format.format(this)
  .replace(",", ".")

@Serializable data class LightColor(
  val red: Float = 0.0f,
  val green: Float = 0.0f,
  val blue: Float = 0.0f,
  val white: Float = 1.0f
)

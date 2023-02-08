/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import android.app.Application
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.util.broadcastsFactory
import com.ivianuu.essentials.xposed.Hooks
import com.ivianuu.essentials.xposed.arg
import com.ivianuu.essentials.xposed.callMethod
import com.ivianuu.essentials.xposed.hookAllConstructors
import com.ivianuu.essentials.xposed.hookAllMethods
import com.ivianuu.essentials.xposed.`this`
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

@Serializable data class TransitionData(
  val fadeInTimestamp: Duration = Duration.ZERO,
  val fadeInDuration: Duration = 3.seconds,
  val fadeOutTimestamp: Duration = Duration.INFINITE,
  val fadeOutDuration: Duration = 3.seconds
)

@Serializable data class FadeCurve(
  val startPoint: Double,
  val endPoint: Double,
  @SerialName("fade_curve") val fadeCurve: List<Item>
) {
  @Serializable data class Item(
    val x: Double,
    val y: Double
  )
}

// [{\"start_point\":0.0,\"end_point\":1.0," +
//      "\"fade_curve\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.024,\"y\":0.32662357504185807},{\"x\":0.138,\"y\":0.5520288942883655},{\"x\":0.48,\"y\":0.8023654375445535},{\"x\":0.999,\"y\":0.9996998949404599}]}]

private val FadeInCurve = FadeCurve(
  0.0,
  1.0,
  listOf(
    FadeCurve.Item(0.0, 0.0),
    FadeCurve.Item(0.024, 0.32),
    FadeCurve.Item(0.138, 0.55),
    FadeCurve.Item(0.48, 0.8),
    FadeCurve.Item(1.0, 1.0)
  )
)

//   [{\"start_point\":0.0,\"end_point\":1.0,\"fade_curve\":[{\"x\":0.0,\"y\":1.0},{\"x\":0.08,\"y\":0.977371522300419},{\"x\":0.192,\"y\":0.9158691305093662},{\"x\":0.448,\"y\":0.7001405982789005},{\"x\":0.999,\"y\":0.0014996249374765735}]}]

private val FadeOutCurve = FadeCurve(
  0.0,
  1.0,
  listOf(
    FadeCurve.Item(0.0, 1.0),
    FadeCurve.Item(0.08, 0.97),
    FadeCurve.Item(0.192, 0.91),
    FadeCurve.Item(0.44, 0.7),
    FadeCurve.Item(0.99, 0.001)
  )
)

var transitionDatas = emptyMap<String, TransitionData>()

context(Logger) @Provide fun sampleHooks() = Hooks {
  if (packageName.value != "com.spotify.music") return@Hooks

  hookAllMethods(
    Application::class,
    "onCreate"
  ) {
    before {
      with(`this`<AppContext>()) {
        GlobalScope.launch {
          broadcastsFactory()
            .broadcasts("track_transitions")
            .collect {
              transitionDatas = Json.decodeFromString(it.getStringExtra("data")!!)

              log { "data changed $transitionDatas" }
            }
        }
      }
    }
  }

  hookAllConstructors(
    classLoader.loadClass("com.spotify.player.model.AutoValue_ContextTrack")!!.kotlin
  ) {
    before {
      val originalMap = arg<Map<String, String>>(2)
      val id = arg<String>(0).removePrefix("spotify:track:")

      val transitionData = transitionDatas[id] ?: TransitionData()

      args[2] = originalMap.callMethod(
        "c",
        originalMap
          .toMutableMap()
          .apply {
            put(
              "audio.fade_in_start_time",
              transitionData.fadeInTimestamp.inWholeMilliseconds.toString()
            )
            put(
              "audio.fade_in_duration",
              transitionData.fadeInDuration.inWholeMilliseconds.toString()
            )
            put("audio.fade_in_curves", Json.encodeToString(FadeInCurve))

            val fadeOutTimestamp = if (transitionData.fadeOutTimestamp == Duration.INFINITE)
              get("duration")?.toInt()?.milliseconds?.let {
                it - transitionData.fadeOutDuration
              }?.toString()
            else
              transitionData.fadeOutTimestamp.inWholeMilliseconds.toString()

            if (fadeOutTimestamp != null) {
              put("audio.fade_out_start_time", fadeOutTimestamp)
              put(
                "audio.fade_out_duration",
                transitionData.fadeOutDuration.inWholeMilliseconds.toString()
              )
              put("audio.fade_out_curves", Json.encodeToString(FadeOutCurve))
            } else {
              log { "No duration ${args.contentToString()}" }
            }

            put("audio.only_allow_fade_on_advance", "false")
          }
      )
    }

    after {
      log { "ContextTrack ${args.contentToString()}" }
    }
  }
}

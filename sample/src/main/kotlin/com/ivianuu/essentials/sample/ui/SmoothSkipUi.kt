/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.material.Text
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.animation.transition.defaultAnimationSpec
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.incrementingStepPolicy
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.prefs.SliderListItem
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Provide val smoothSkipHomeItem = HomeItem("Smooth skip") { SmoothSkipKey }

object SmoothSkipKey : Key<Unit>

@Provide fun smoothSkipUi(
  audioManager: AudioManager,
  broadcastsFactory: BroadcastsFactory,
  context: AppContext,
  pref: DataStore<SmoothSkipPrefs>
) = SimpleKeyUi<SmoothSkipKey> {
  val prefs = pref.data.bind(SmoothSkipPrefs())

  SimpleListScreen("Smooth skip") {
    item {
      SliderListItem(
        value = prefs.fadeOutDuration,
        onValueChange = action { value ->
          pref.updateData { copy(fadeOutDuration = value) }
        },
        valueRange = 0.seconds..10.seconds,
        stepPolicy = incrementingStepPolicy(500.milliseconds),
        title = { Text("Fade out duration") },
        valueText = { Text(it.toString()) }
      )
    }

    item {
      SliderListItem(
        value = prefs.fadeInDuration,
        onValueChange = action { value ->
          pref.updateData { copy(fadeInDuration = value) }
        },
        valueRange = 0.seconds..10.seconds,
        stepPolicy = incrementingStepPolicy(500.milliseconds),
        title = { Text("Fade in duration") },
        valueText = { Text(it.toString()) }
      )
    }

    item {
      Button(onClick = action {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentTrack = broadcastsFactory.broadcasts("com.spotify.music.metadatachanged")
          .mapNotNull { it.getStringExtra("id")?.removePrefix("spotify:track:") }
          .first()

        animate(spec = defaultAnimationSpec(prefs.fadeOutDuration)) {
          audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            lerp(
              currentVolume,
              audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC),
              it
            ),
            0
          )
        }

        context.sendOrderedBroadcast(
          mediaIntentFor(
            KeyEvent.ACTION_DOWN,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            "com.spotify.music"
          ), null
        )
        context.sendOrderedBroadcast(
          mediaIntentFor(
            KeyEvent.ACTION_UP,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            "com.spotify.music"
          ), null
        )

        broadcastsFactory.broadcasts("com.spotify.music.metadatachanged")
          .mapNotNull { it.getStringExtra("id")?.removePrefix("spotify:track:") }
          .first { it != currentTrack }

        animate(spec = defaultAnimationSpec(prefs.fadeInDuration)) {
          audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            lerp(
              audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC),
              currentVolume,
              it
            ),
            0
          )
        }
      }) {
        Text("Smooth skip")
      }
    }
  }
}

private suspend fun animate(
  spec: AnimationSpec<Float> = defaultAnimationSpec(),
  block: (Float) -> Unit
) {
  Animatable(0f).animateTo(1f, spec) {
    block(value)
  }
}

private fun mediaIntentFor(
  keyEvent: Int,
  keycode: Int,
  packageName: String
): Intent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
  putExtra(
    Intent.EXTRA_KEY_EVENT,
    KeyEvent(keyEvent, keycode)
  )

  `package` = packageName
}

@Serializable data class SmoothSkipPrefs(
  val fadeOutDuration: Duration = 1.seconds,
  val fadeInDuration: Duration = 3.seconds
) {
  companion object {
    @Provide val pref = PrefModule { SmoothSkipPrefs() }
  }
}

package com.ivianuu.essentials.ui.prefs

import android.content.SharedPreferences
import androidx.compose.Recompose
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.onActive
import com.ivianuu.essentials.ui.compose.injekt.inject

fun ComponentComposition.Prefs(
    children: ComponentComposition.() -> Unit
) {
    val sharedPreferences = inject<SharedPreferences>()
    Recompose { recompose ->
        onActive {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                recompose()
            }

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            onDispose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }

        children()
    }
}
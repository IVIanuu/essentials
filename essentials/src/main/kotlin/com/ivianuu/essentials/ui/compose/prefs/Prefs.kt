package com.ivianuu.essentials.ui.compose.prefs

import android.content.SharedPreferences
import androidx.compose.Composable
import androidx.compose.Observe
import androidx.compose.unaryPlus
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.flow
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.util.*

@Composable
fun Prefs(children: () -> Unit) = composable("Prefs") {
    Observe {
        val sharedPreferencesChanges = +inject<SharedPreferencesChanges>()
        +flow(sharedPreferencesChanges.changes.map { UUID.randomUUID().toString() })
        children()
    }
}

@Inject
@ApplicationScope
class SharedPreferencesChanges(sharedPreferences: SharedPreferences) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    val changes: Flow<String>
        get() = channel.asFlow()

    private val channel = BroadcastChannel<String>(1)

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        channel.offer(key)
    }
}
package com.ivianuu.essentials.gestures.action.actions

/**
import android.content.Intent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action

private fun createShortcutAction() = Action(
    key = KEY_SHORTCUT,
    title = string(R.string.action_shortcut),
    states = stateless(R.drawable.es_ic_content_cut)
)

private fun launchShortcut(key: String) {
    val uri = key.replace(KEY_SHORTCUT, "")
    val intent = Intent.parseUri(uri, 0)
    startActivity(intent)
}
*/
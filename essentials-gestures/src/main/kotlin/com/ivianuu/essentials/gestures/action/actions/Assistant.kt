package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.path
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.lazyMaterialIcon
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get

internal val EsAssistantActionModule = Module {
    bindAction(
        key = "assistant",
        title = { getStringResource(R.string.es_action_assistant) },
        iconProvider = { SingleActionIconProvider(Icons.Essentials.Google) },
        unlockScreen = { true },
        executor = { get<AssistantActionExecutor>() }
    )
}

private val EssentialsIcons.Google: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(20.822313f, 10.3125f)
        curveTo(20.937244f, 10.914257f, 21.0f, 11.544343f, 21.0f, 12.2024145f)
        curveTo(21.000044f, 17.343771f, 17.48765f, 21.0f, 12.183874f, 21.0f)
        curveTo(7.1098714f, 21.0f, 3.0f, 16.972414f, 3.0f, 12.0f)
        curveTo(3.0f, 7.0275855f, 7.1098714f, 3.0f, 12.183874f, 3.0f)
        curveTo(14.663651f, 3.0f, 16.735403f, 3.8943f, 18.325655f, 5.3455715f)
        lineTo(15.736503f, 7.8825855f)
        lineTo(15.736503f, 7.8768f)
        curveTo(14.772371f, 6.976714f, 13.549867f, 6.5155716f, 12.183874f, 6.5155716f)
        curveTo(9.153327f, 6.5155716f, 6.690868f, 9.0243f, 6.690868f, 11.994472f)
        curveTo(6.690868f, 14.964043f, 9.153283f, 17.478815f, 12.183874f, 17.478815f)
        curveTo(14.933308f, 17.478815f, 16.804325f, 15.937457f, 17.18948f, 13.822586f)
        lineTo(12.183874f, 13.822586f)
        lineTo(12.183874f, 10.3125f)
        lineTo(20.822313f, 10.3125f)
        lineTo(20.822313f, 10.3125f)
        close()
    }
}

@Factory
internal class AssistantActionExecutor(
    private val searchManager: SearchManager
) : ActionExecutor {
    @SuppressLint("DiscouragedPrivateApi")
    override suspend fun invoke() {
        try {
            val launchAssist = searchManager.javaClass
                .getDeclaredMethod("launchAssist", Bundle::class.java)
            launchAssist.invoke(searchManager, Bundle())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

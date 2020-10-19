package com.ivianuu.essentials.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.essentials.util.Logger

@UiDecoratorBinding
fun UiLogger(logger: Logger): UiDecorator = { children ->
    onActive {
        logger.d("hello from ui")
        onDispose {
            logger.d("bye from ui")
        }
    }
    children()
}

package com.ivianuu.essentials.ui.core

import androidx.compose.composer
import com.ivianuu.essentials.util.sourceLocation

inline fun pointInComposition() = composer.joinKey(composer.currentCompoundKeyHash, sourceLocation())
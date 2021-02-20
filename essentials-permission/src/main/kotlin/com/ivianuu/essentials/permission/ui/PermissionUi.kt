package com.ivianuu.essentials.permission.ui

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.permission.Permission

data class PermissionUiMetadata<T : Permission>(
    val title: String,
    val desc: String? = null,
    val icon: @Composable (() -> Unit)? = null
)

package com.ivianuu.essentials.apps.shortcuts

import android.content.Context
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide

typealias GetAllAppShortcutsForAppUseCase = (String) -> List<AppShortcut>

@Provide fun getAllAppShortcutsForAppUseCase(
  context: AppContext
): GetAllAppShortcutsForAppUseCase = useCase@ { packageName ->
  val resources = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
    .resources

  parseAppShortcutMetadata(context, packageName)
    .flatMap {
      parseAppShortcuts(context, resources, packageName, it.key, it.value)
    }
}

typealias GetAppShortcutUseCase = (String, String) -> AppShortcut

@Provide fun getAppShortcutUseCase(
  getAllAppShortcutsForApp: GetAllAppShortcutsForAppUseCase
): GetAppShortcutUseCase = useCase@ { packageName, id ->
  getAllAppShortcutsForApp(packageName)
    .single { it.id == id }
}

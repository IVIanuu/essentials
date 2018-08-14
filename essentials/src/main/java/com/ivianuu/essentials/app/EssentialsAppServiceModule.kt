package com.ivianuu.essentials.app

import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.ui.common.back.BackHandler
import com.ivianuu.essentials.ui.common.toolbar.ToolbarService
import com.ivianuu.essentials.util.screenlogger.ScreenLogger
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

/**
 * Essentials app service module
 */
@Module
abstract class EssentialsAppServiceModule {

    @IntoSet
    @Binds
    abstract fun bindAutoInjector(autoInjector: AutoInjector): AppService

    @IntoSet
    @Binds
    abstract fun bindBackHandler(backHandler: BackHandler): AppService

    @IntoSet
    @Binds
    abstract fun bindScreenLogger(screenLogger: ScreenLogger): AppService

    @IntoSet
    @Binds
    abstract fun bindToolbarService(toolbarService: ToolbarService): AppService

}
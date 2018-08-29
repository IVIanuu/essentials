package com.ivianuu.essentials.app

import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.ui.common.back.BackHandler
import com.ivianuu.essentials.util.screenlogger.ScreenLogger
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

/**
 * Essentials app service module
 */
@Module
abstract class EssentialsAppServiceModule {

    @Binds
    @IntoSet
    abstract fun bindAutoInjector(autoInjector: AutoInjector): AppService

    @Binds
    @IntoSet
    abstract fun bindBackHandler(backHandler: BackHandler): AppService

    @Binds
    @IntoSet
    abstract fun bindScreenLogger(screenLogger: ScreenLogger): AppService

}
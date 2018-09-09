package com.ivianuu.essentials.injection.view

import android.view.View
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * View key
 */
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ViewKey(val value: KClass<out View>)
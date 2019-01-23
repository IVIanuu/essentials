package com.ivianuu.essentials.ui.traveler.key

import android.os.Bundle
import android.os.Parcelable
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import kotlin.reflect.KClass

/**
 * Modules to bind a traveler key
 */
fun keyModule(
    bundle: Bundle?,
    throwIfNotAvailable: Boolean = true
) = module("KeyModule") {
    if (bundle != null && bundle.containsKey(TRAVELER_KEY_CLASS) && bundle.containsKey(TRAVELER_KEY)) {
        val className = bundle.getString(TRAVELER_KEY_CLASS)!!
        val type = Class.forName(className).kotlin as KClass<Any>
        single<Parcelable>(type) { bundle.getParcelable(TRAVELER_KEY)!! }
    } else if (throwIfNotAvailable) {
        error("No traveler key in bundle $bundle")
    }
}
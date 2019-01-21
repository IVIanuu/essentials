package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.util.PrefValueHolder

/**
 * @author Manuel Wrage (IVIanuu)
 */
enum class NavBarRotationMode(override val value: String) : PrefValueHolder<String> {
    MARSHMALLOW("marshmallow"), NOUGAT("nougat"), TABLET("tablet")
}
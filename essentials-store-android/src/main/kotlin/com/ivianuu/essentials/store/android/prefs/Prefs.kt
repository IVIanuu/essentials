package com.ivianuu.essentials.store.android.prefs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Prefs internal constructor(val map: Map<String, String> = mapOf())

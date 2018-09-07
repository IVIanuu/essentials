package com.ivianuu.essentials.util.ext

import java.util.*

inline var Calendar.year: Int
    get() = this.get(Calendar.YEAR)
    set(value) = this.set(Calendar.YEAR, value)

inline var Calendar.month: Int
    get() = this.get(Calendar.MONTH)
    set(value) = this.set(Calendar.MONTH, value)

inline var Calendar.hour: Int
    get() = this.get(Calendar.HOUR_OF_DAY)
    set(value) = this.set(Calendar.HOUR_OF_DAY, value)

inline var Calendar.day: Int
    get() = this.get(Calendar.DAY_OF_MONTH)
    set(value) = this.set(Calendar.DAY_OF_MONTH, value)

inline var Calendar.minute: Int
    get() = this.get(Calendar.MINUTE)
    set(value) = this.set(Calendar.MINUTE, value)

inline var Calendar.second: Int
    get() = this.get(Calendar.SECOND)
    set(value) = this.set(Calendar.SECOND, value)

inline var Calendar.millisecond: Int
    get() = this.get(Calendar.MILLISECOND)
    set(value) = this.set(Calendar.MILLISECOND, value)
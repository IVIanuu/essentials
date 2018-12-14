package com.ivianuu.essentials.ui.viewmodel

import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import androidx.core.os.bundleOf
import java.io.Serializable

/**
 * @author Manuel Wrage (IVIanuu)
 */
interface SavedState {

    fun contains(key: String): Boolean

    fun <T : Any> get(key: String): T
    
    fun <T : Any> put(key: String, value: T)
    
}

fun <T : Any> SavedState.getOrNull(key: String) = if (contains(key)) {
    get<T>(key)
} else {
    null
}

class BundleSavedState(val bundle: Bundle) : SavedState {

    override fun contains(key: String) = bundle.containsKey(key)

    override fun <T : Any> get(key: String): T = bundle[key] as T

    override fun <T : Any> put(key: String, value: T) {
        when (value) {
            // Scalars
            is Boolean -> bundle.putBoolean(key, value)
            is Byte -> bundle.putByte(key, value)
            is Char -> bundle.putChar(key, value)
            is Double -> bundle.putDouble(key, value)
            is Float -> bundle.putFloat(key, value)
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)
            is Short -> bundle.putShort(key, value)

            // References
            is Bundle -> bundle.putBundle(key, value)
            is CharSequence -> bundle.putCharSequence(key, value)
            is Parcelable -> bundle.putParcelable(key, value)

            // Scalar arrays
            is BooleanArray -> bundle.putBooleanArray(key, value)
            is ByteArray -> bundle.putByteArray(key, value)
            is CharArray -> bundle.putCharArray(key, value)
            is DoubleArray -> bundle.putDoubleArray(key, value)
            is FloatArray -> bundle.putFloatArray(key, value)
            is IntArray -> bundle.putIntArray(key, value)
            is LongArray -> bundle.putLongArray(key, value)
            is ShortArray -> bundle.putShortArray(key, value)

            // Reference arrays
            is Array<*> -> {
                val componentType = value::class.java.componentType
                @Suppress("UNCHECKED_CAST") // Checked by reflection.
                when {
                    Parcelable::class.java.isAssignableFrom(componentType) -> {
                        bundle.putParcelableArray(key, value as Array<Parcelable>)
                    }
                    String::class.java.isAssignableFrom(componentType) -> {
                        bundle.putStringArray(key, value as Array<String>)
                    }
                    CharSequence::class.java.isAssignableFrom(componentType) -> {
                        bundle.putCharSequenceArray(key, value as Array<CharSequence>)
                    }
                    Serializable::class.java.isAssignableFrom(componentType) -> {
                        bundle.putSerializable(key, value)
                    }
                    else -> {
                        val valueType = componentType.canonicalName
                        throw IllegalArgumentException(
                            "Illegal value array type $valueType for key \"$key\"")
                    }
                }
            }

            // Last resort. Also we must check this after Array<*> as all arrays are serializable.
            is Serializable -> bundle.putSerializable(key, value)

            else -> {
                if (Build.VERSION.SDK_INT >= 18 && value is Binder) {
                    bundle.putBinder(key, value)
                } else if (Build.VERSION.SDK_INT >= 21 && value is Size) {
                    bundle.putSize(key, value)
                } else if (Build.VERSION.SDK_INT >= 21 && value is SizeF) {
                    bundle.putSizeF(key, value)
                } else {
                    val valueType = value.javaClass.canonicalName
                    throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
                }
            }
        }
    }
    
}
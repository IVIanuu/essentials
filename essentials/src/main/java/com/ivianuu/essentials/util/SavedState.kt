/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import androidx.core.os.bundleOf
import java.io.Serializable
import java.util.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
interface SavedState {

    val entries: Map<String, Any?>

    operator fun <T> get(key: String): T?

    operator fun <T> set(key: String, value: T)

    fun contains(key: String): Boolean

}

val SavedState.size: Int get() = entries.size

class MapSavedState : SavedState {

    override val entries: Map<String, Any?>
        get() = _entries

    private val _entries = mutableMapOf<String, Any?>()

    override fun <T> get(key: String): T? = _entries[key] as? T

    override fun <T> set(key: String, value: T) {
        _entries[key] = value
    }

    override fun contains(key: String): Boolean = _entries.contains(key)

}

fun savedStateOf(vararg pairs: Pair<String, Any?>): SavedState {
    val savedState = MapSavedState()
    pairs.forEach { (key, value) -> savedState[key] = value }
    return savedState
}

//
// map
//
fun Map<String, *>.toSavedSaved(): SavedState {
    val savedState = savedStateOf()
    forEach { (key, value) -> savedState[key] = value }
    return savedState
}

//
// android
//


fun parceledSavedStateOf(vararg pairs: Pair<String, Any?>): ParceledSavedState {
    val savedState = ParceledSavedState()
    pairs.forEach { (key, value) -> savedState[key] = value }
    return savedState
}

fun SavedState.toParceledSavedState(): ParceledSavedState {
    val savedState = ParceledSavedState()
    entries.forEach { (key, value) -> savedState[key] = value }
    return savedState
}

class ParceledSavedState() : Any(), SavedState, Parcelable {

    private val _entries = mutableMapOf<String, Any?>()

    override val entries: Map<String, Any?>
        get() = _entries

    override fun <T> get(key: String): T? = _entries[key] as? T

    override fun <T> set(key: String, value: T) {
        value.checkType()
        _entries[key] = value
    }

    constructor(parcel: Parcel) : this() {
        val bundle = parcel.readBundle(javaClass.classLoader)!!
        val keys: ArrayList<out Any?> = bundle.getParcelableArrayList(KEY_KEYS)!!
        val values: ArrayList<out Any?> = bundle.getParcelableArrayList(KEY_VALUES)!!

        keys.forEachIndexed { i, key ->
            val value = values[i]
            _entries[key as String] = value
        }
    }

    override fun contains(key: String): Boolean = _entries.containsKey(key)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        val bundle = bundleOf()

        val keys = arrayListOf<Any?>()
        val values = arrayListOf<Any?>()

        _entries
            .mapValues {
                val value = it.value
                // make sure that saved states are parcelable
                if (value is SavedState && value !is Parcelable) {
                    value.toParceledSavedState()
                } else {
                    value
                }
            }
            .forEach { (key, value) ->
                keys.add(key)
                values.add(value)
            }

        // yup this works:D
        bundle.putParcelableArrayList(KEY_KEYS, keys as ArrayList<out Parcelable>)
        bundle.putParcelableArrayList(KEY_VALUES, values as ArrayList<out Parcelable>)

        parcel.writeBundle(bundle)
    }

    override fun describeContents(): Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParceledSavedState

        if (_entries != other._entries) return false

        return true
    }

    override fun hashCode(): Int {
        return _entries.hashCode()
    }

    override fun toString(): String {
        return "ParceledSavedState $_entries"
    }

    private fun Any?.checkType() {
        require(ACCEPTABLE_CLASSES.any { it.java.isInstance(this) }) {
            "Can't put value with type $javaClass into ParceledSavedState"
        }
    }

    companion object {
        private const val KEY_KEYS = "ParceledSavedState.keys"
        private const val KEY_VALUES = "ParceledSavedState.values"

        @JvmField
        val CREATOR = object : Parcelable.Creator<ParceledSavedState> {
            override fun createFromParcel(parcel: Parcel): ParceledSavedState =
                ParceledSavedState(parcel)

            override fun newArray(size: Int): Array<ParceledSavedState?> = arrayOfNulls(size)
        }

        // doesn't have Integer, Long etc box types because they are "Serializable"
        private val ACCEPTABLE_CLASSES = arrayOf(
            //baseBundle
            Boolean::class,
            BooleanArray::class,
            Double::class,
            DoubleArray::class,
            Int::class,
            IntArray::class,
            Long::class,
            LongArray::class,
            String::class,
            Array<String>::class,

            //bundle
            Binder::class,
            Bundle::class,
            Byte::class,
            ByteArray::class,
            Char::class,
            CharArray::class,
            CharSequence::class,
            Array<CharSequence>::class,
            // type erasure ¯\_(ツ)_/¯, we won't eagerly check elements contents
            ArrayList::class,
            Float::class,
            FloatArray::class,
            Parcelable::class,
            Array<Parcelable>::class,
            Serializable::class,
            Short::class,
            ShortArray::class,
            SparseArray::class,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) Size::class else Int::class,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) SizeF::class else Int::class,

            // will be auto converted to ParceledSavedState
            SavedState::class
        )
    }

}

fun Bundle.putSavedState(key: String, savedState: SavedState) {
    if (savedState is ParceledSavedState) {
        putParcelable(key, savedState)
    } else {
        putParcelable(key, savedState.toParceledSavedState())
    }
}

fun Bundle.getSavedState(key: String): ParceledSavedState? = getParcelable(key)
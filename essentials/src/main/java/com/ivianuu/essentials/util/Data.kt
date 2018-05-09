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
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import androidx.core.os.bundleOf
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Data(val bundle: Bundle = Bundle()) : Parcelable {

    // VALS

    val entries: Set<Pair<String, Any?>>
        get() = keys
            .map { it to bundle.get(it) }
            .toSet()

    val keys: Set<String>
        get() = bundle.keySet()

    val values: Collection<Any?>
        get() = bundle.keySet()
            .map { bundle.get(it) }
            .toSet()

    // PUTTER

    fun putBoolean(key: String, value: Boolean): Data {
        bundle.putBoolean(key, value)
        return this
    }

    fun putByte(key: String, value: Byte): Data {
        bundle.putByte(key, value)
        return this
    }

    fun putChar(key: String, value: Char): Data {
        bundle.putChar(key, value)
        return this
    }

    fun putDouble(key: String, value: Double): Data {
        bundle.putDouble(key, value)
        return this
    }

    fun putFloat(key: String, value: Float): Data {
        bundle.putFloat(key, value)
        return this
    }

    fun putInt(key: String, value: Int): Data {
        bundle.putInt(key, value)
        return this
    }

    fun putLong(key: String, value: Long): Data {
        bundle.putLong(key, value)
        return this
    }

    fun putShort(key: String, value: Short): Data {
        bundle.putShort(key, value)
        return this
    }

    fun putData(key: String, value: Data): Data {
        bundle.putParcelable(key, value)
        return this
    }

    fun putBundle(key: String, value: Bundle): Data {
        bundle.putBundle(key, value)
        return this
    }

    fun putCharSequence(key: String, value: CharSequence): Data {
        bundle.putCharSequence(key, value)
        return this
    }

    fun putParcelable(key: String, value: Parcelable): Data {
        bundle.putParcelable(key, value)
        return this
    }

    fun putString(key: String, value: String): Data {
        bundle.putString(key, value)
        return this
    }

    fun putBooleanArray(key: String, value: BooleanArray): Data {
        bundle.putBooleanArray(key, value)
        return this
    }

    fun putByteArray(key: String, value: ByteArray): Data {
        bundle.putByteArray(key, value)
        return this
    }

    fun putCharArray(key: String, value: CharArray): Data {
        bundle.putCharArray(key, value)
        return this
    }

    fun putFloatArray(key: String, value: FloatArray): Data {
        bundle.putFloatArray(key, value)
        return this
    }

    fun putIntArray(key: String, value: IntArray): Data {
        bundle.putIntArray(key, value)
        return this
    }

    fun putLongArray(key: String, value: LongArray): Data {
        bundle.putLongArray(key, value)
        return this
    }

    fun putShortArray(key: String, value: ShortArray): Data {
        bundle.putShortArray(key, value)
        return this
    }

    fun putParcelableArray(key: String, value: Array<Parcelable>): Data {
        bundle.putParcelableArray(key, value)
        return this
    }

    fun putStringArray(key: String, value: Array<String>): Data {
        bundle.putStringArray(key, value)
        return this
    }

    fun putCharSequenceArray(key: String, value: Array<CharSequence>): Data {
        bundle.putCharSequenceArray(key, value)
        return this
    }

    fun putSerializable(key: String, value: Serializable): Data {
        bundle.putSerializable(key, value)
        return this
    }

    fun putBinder(key: String, value: Binder): Data {
        bundle.putBinder(key, value)
        return this
    }

    fun putSize(key: String, value: Size): Data {
        bundle.putSize(key, value)
        return this
    }

    fun putSizeF(key: String, value: SizeF): Data {
        bundle.putSizeF(key, value)
        return this
    }

    fun putAll(vararg values: Pair<String, Any?>): Data {
        bundle.putAll(bundleOf(*values))
        return this
    }

    fun putAll(values: Map<String, Any?>): Data {
        val pairs = values.map { it.key to it.value }
        putAll(*pairs.toTypedArray())
        return this
    }

    // GETTER

    fun getBoolean(key: String): Boolean? {
        return if (bundle.containsKey(key)) {
            bundle.getBoolean(key)
        } else {
            null
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getBoolean(key) ?: defaultValue
    }

    fun getByte(key: String): Byte? {
        return if (bundle.containsKey(key)) {
            bundle.getByte(key)
        } else {
            null
        }
    }

    fun getByte(key: String, defaultValue: Byte): Byte {
        return getByte(key) ?: defaultValue
    }

    fun getChar(key: String): Char? {
        return if (bundle.containsKey(key)) {
            bundle.getChar(key)
        } else {
            null
        }
    }

    fun getChar(key: String, defaultValue: Char): Char {
        return getChar(key) ?: defaultValue
    }

    fun getDouble(key: String): Double? {
        return if (bundle.containsKey(key)) {
            bundle.getDouble(key)
        } else {
            null
        }
    }

    fun getDouble(key: String, defaultValue: Double): Double {
        return getDouble(key) ?: defaultValue
    }

    fun getFloat(key: String): Float? {
        return if (bundle.containsKey(key)) {
            bundle.getFloat(key)
        } else {
            null
        }
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return getFloat(key) ?: defaultValue
    }

    fun getInt(key: String): Int? {
        return if (bundle.containsKey(key)) {
            bundle.getInt(key)
        } else {
            null
        }
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return getInt(key) ?: defaultValue
    }

    fun getLong(key: String): Long? {
        return if (bundle.containsKey(key)) {
            bundle.getLong(key)
        } else {
            null
        }
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return getLong(key) ?: defaultValue
    }

    fun getShort(key: String): Short? {
        return if (bundle.containsKey(key)) {
            bundle.getShort(key)
        } else {
            null
        }
    }

    fun getShort(key: String, defaultValue: Short): Short {
        return getShort(key) ?: defaultValue
    }

    fun getData(key: String): Data? {
        return bundle.getParcelable(key)
    }

    fun getData(key: String, data: Data): Data {
        return bundle.getParcelable(key) ?: data
    }

    fun getBundle(key: String): Bundle? {
        return bundle.getBundle(key)
    }

    fun getBundle(key: String, defaultValue: Bundle): Bundle {
        return getBundle(key) ?: defaultValue
    }

    fun getCharSequence(key: String): CharSequence? {
        return bundle.getCharSequence(key)
    }

    fun getCharSequence(key: String, defaultValue: CharSequence): CharSequence {
        return getCharSequence(key, defaultValue)
    }

    fun <T : Parcelable> getParcelable(key: String): T? {
        return bundle.getParcelable(key)
    }

    fun <T : Parcelable> getParcelable(key: String, defaultValue: T): T {
        return getParcelable(key) ?: defaultValue
    }

    fun getString(key: String): String? {
        return bundle.getString(key)
    }

    fun getString(key: String, defaultValue: String): String {
        return getString(key, defaultValue)
    }

    fun getBooleanArray(key: String): BooleanArray? {
        return bundle.getBooleanArray(key)
    }

    fun getBooleanArray(key: String, defaultValue: BooleanArray): BooleanArray {
        return getBooleanArray(key) ?: defaultValue
    }

    fun getByteArray(key: String): ByteArray? {
        return bundle.getByteArray(key)
    }

    fun getByteArray(key: String, defaultValue: ByteArray): ByteArray {
        return getByteArray(key) ?: defaultValue
    }

    fun getCharArray(key: String): CharArray? {
        return bundle.getCharArray(key)
    }

    fun getCharArray(key: String, defaultValue: CharArray): CharArray {
        return getCharArray(key) ?: defaultValue
    }

    fun getFloatArray(key: String): FloatArray? {
        return bundle.getFloatArray(key)
    }

    fun getFloatArray(key: String, defaultValue: FloatArray): FloatArray {
        return getFloatArray(key) ?: defaultValue
    }

    fun getIntArray(key: String): IntArray? {
        return bundle.getIntArray(key)
    }

    fun getIntArray(key: String, defaultValue: IntArray): IntArray {
        return getIntArray(key) ?: defaultValue
    }

    fun getLongArray(key: String): LongArray? {
        return bundle.getLongArray(key)
    }

    fun getLongArray(key: String, defaultValue: LongArray): LongArray {
        return getLongArray(key) ?: defaultValue
    }

    fun getShortArray(key: String): ShortArray? {
        return bundle.getShortArray(key)
    }

    fun getShortArray(key: String, defaultValue: ShortArray): ShortArray {
        return getShortArray(key) ?: defaultValue
    }

    fun getParcelableArray(key: String): Array<Parcelable>? {
        return bundle.getParcelableArray(key)
    }

    fun getParcelableArray(key: String, defaultValue: Array<Parcelable>): Array<Parcelable> {
        return getParcelableArray(key) as Array<Parcelable>? ?: defaultValue
    }

    fun <T : Parcelable> getParcelableArrayList(key: String): ArrayList<T>? {
        return bundle.getParcelableArrayList<T>(key)
    }

    fun <T : Parcelable> getParcelableArrayList(key: String, defaultValue: ArrayList<T>): ArrayList<T> {
        return getParcelableArrayList(key) ?: defaultValue
    }

    fun getStringArray(key: String): Array<String>? {
        return bundle.getStringArray(key)
    }

    fun getStringArray(key: String, defaultValue: Array<String>): Array<String> {
        return getStringArray(key) ?: defaultValue
    }

    fun getCharSequenceArray(key: String): Array<CharSequence>? {
        return bundle.getCharSequenceArray(key)
    }

    fun getCharSequenceArray(key: String, defaultValue: Array<CharSequence>): Array<CharSequence> {
        return getCharSequenceArray(key) ?: defaultValue
    }

    fun getSerializable(key: String): Serializable? {
        return bundle.getSerializable(key)
    }

    fun getSerializable(key: String, defaultValue: Serializable): Serializable {
        return getSerializable(key) ?: defaultValue
    }

    fun getBinder(key: String): IBinder? {
        return bundle.getBinder(key)
    }

    fun getBinder(key: String, defaultValue: IBinder): IBinder {
        return getBinder(key) ?: defaultValue
    }

    fun getSize(key: String): Size? {
        return bundle.getSize(key)
    }

    fun getSize(key: String, defaultValue: Size): Size {
        return getSize(key) ?: defaultValue
    }

    fun getSizeF(key: String): SizeF? {
        return bundle.getSizeF(key)
    }

    fun getSizeF(key: String, defaultValue: SizeF): SizeF {
        return getSizeF(key) ?: defaultValue
    }

    fun get(key: String): Any? {
        return bundle.get(key)
    }

    fun get(key: String, defaultValue: Any): Any {
        return get(key) ?: defaultValue
    }

    // REQUIRE

    fun requireBoolean(key: String): Boolean {
        return getBoolean(key) ?: throwMissingKey(key)
    }

    fun requireByte(key: String): Byte {
        return getByte(key) ?: throwMissingKey(key)
    }

    fun requireChar(key: String): Char {
        return getChar(key) ?: throwMissingKey(key)
    }

    fun requireDouble(key: String): Double {
        return getDouble(key) ?: throwMissingKey(key)
    }

    fun requireFloat(key: String): Float {
        return getFloat(key) ?: throwMissingKey(key)
    }

    fun requireInt(key: String): Int {
        return getInt(key) ?: throwMissingKey(key)
    }

    fun requireLong(key: String): Long {
        return getLong(key) ?: throwMissingKey(key)
    }

    fun requireShort(key: String): Short {
        return getShort(key) ?: throwMissingKey(key)
    }

    fun requireData(key: String): Data {
        return getData(key) ?: throwMissingKey(key)
    }

    fun requireBundle(key: String): Bundle {
        return getBundle(key) ?: throwMissingKey(key)
    }

    fun requireCharSequence(key: String): CharSequence {
        return getCharSequence(key) ?: throwMissingKey(key)
    }

    fun <T : Parcelable> requireParcelable(key: String): T {
        return getParcelable(key) ?: throwMissingKey(key)
    }

    fun requireString(key: String): String {
        return getString(key) ?: throwMissingKey(key)
    }

    fun requireBooleanArray(key: String): BooleanArray {
        return getBooleanArray(key) ?: throwMissingKey(key)
    }

    fun requireByteArray(key: String): ByteArray {
        return getByteArray(key) ?: throwMissingKey(key)
    }

    fun requireCharArray(key: String): CharArray {
        return getCharArray(key) ?: throwMissingKey(key)
    }

    fun requireFloatArray(key: String): FloatArray {
        return getFloatArray(key) ?: throwMissingKey(key)
    }

    fun requireIntArray(key: String): IntArray {
        return getIntArray(key) ?: throwMissingKey(key)
    }

    fun requireLongArray(key: String): LongArray {
        return getLongArray(key) ?: throwMissingKey(key)
    }

    fun requireShortArray(key: String): ShortArray {
        return getShortArray(key) ?: throwMissingKey(key)
    }

    fun requireParcelableArray(key: String): Array<Parcelable> {
        return getParcelableArray(key) ?: throwMissingKey(key)
    }

    fun <T : Parcelable> requireParcelableArrayList(key: String): ArrayList<T> {
        return getParcelableArrayList(key) ?: throwMissingKey(key)
    }

    fun requireStringArray(key: String): Array<String> {
        return getStringArray(key) ?: throwMissingKey(key)
    }

    fun requireCharSequenceArray(key: String): Array<CharSequence> {
        return getCharSequenceArray(key) ?: throwMissingKey(key)
    }

    fun requireSerializable(key: String): Serializable {
        return getSerializable(key) ?: throwMissingKey(key)
    }

    fun requireBinder(key: String): IBinder {
        return getBinder(key) ?: throwMissingKey(key)
    }

    fun requireSize(key: String): Size {
        return getSize(key) ?: throwMissingKey(key)
    }

    fun requireSizeF(key: String): SizeF {
        return getSizeF(key) ?: throwMissingKey(key)
    }

    fun require(key: String): Any {
        return get(key) ?: throwMissingKey(key)
    }

    // MISC

    fun remove(key: String) {
        bundle.remove(key)
    }

    fun containsKey(key: String) = bundle.containsKey(key)

    fun toBundle() = Bundle(bundle)

    fun toMap(): Map<String, Any?> {
        return entries.toMap()
    }

    fun toList(): List<Pair<String, Any?>> {
        return toMap().toList()
    }

    private fun throwMissingKey(key: String): Nothing {
        throw IllegalStateException("missing key $key")
    }
}
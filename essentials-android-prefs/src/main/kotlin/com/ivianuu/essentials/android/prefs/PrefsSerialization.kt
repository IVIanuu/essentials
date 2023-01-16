/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.TaggedEncoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(InternalSerializationApi::class)
class PrefsEncoder(
  private val embeddedFormat: StringFormat,
  private val descriptor: SerialDescriptor,
  private val result: MutableMap<String, String?>,
  override val serializersModule: SerializersModule = embeddedFormat.serializersModule
) : TaggedEncoder<String>() {
  override fun SerialDescriptor.getTag(index: Int): String = getElementName(index)

  override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
    if (serializer.descriptor == this.descriptor ||
      serializer.descriptor.kind is PrimitiveKind ||
      serializer.descriptor.kind == SerialKind.ENUM
    ) super.encodeSerializableValue(serializer, value)
    else encodeString(embeddedFormat.encodeToString(serializer, value))
  }

  override fun encodeTaggedBoolean(tag: String, value: Boolean) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedByte(tag: String, value: Byte) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedChar(tag: String, value: Char) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedDouble(tag: String, value: Double) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor, ordinal: Int) {
    result[tag] = enumDescriptor.getElementName(ordinal)
  }

  override fun encodeTaggedFloat(tag: String, value: Float) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedInline(tag: String, inlineDescriptor: SerialDescriptor): Encoder = this

  override fun encodeTaggedInt(tag: String, value: Int) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedLong(tag: String, value: Long) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedNull(tag: String) {
    result[tag] = null
  }

  override fun encodeTaggedShort(tag: String, value: Short) {
    result[tag] = value.toString()
  }

  override fun encodeTaggedString(tag: String, value: String) {
    result[tag] = value
  }
}

class PrefsDecoder(
  private val prefs: Map<String, String?>,
  private val embeddedFormat: StringFormat,
  private val descriptor: SerialDescriptor,
  override val serializersModule: SerializersModule = embeddedFormat.serializersModule
) : AbstractDecoder() {
  private var index = 0

  override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = this

  override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
    while (index <= descriptor.elementsCount - 1 &&
      !prefs.containsKey(descriptor.getElementName(index))
    ) {
      index++
    }

    return if (index > descriptor.elementsCount - 1) CompositeDecoder.DECODE_DONE
    else index
  }

  override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
    if (deserializer.descriptor == this.descriptor ||
      deserializer.descriptor.kind is PrimitiveKind ||
      deserializer.descriptor.kind == SerialKind.ENUM
    ) super.decodeSerializableValue(deserializer)
    else
      embeddedFormat.decodeFromString(deserializer, decodeString())

  override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? =
    if (prefs[deserializer.descriptor.getElementName(index)] == null) null.also { index++ }
    else decodeSerializableValue(deserializer)

  override fun decodeBoolean(): Boolean =
    prefs.getValue(descriptor.getElementName(index++)).toBoolean()

  override fun decodeByte(): Byte = prefs.getValue(descriptor.getElementName(index++))!!.toByte()

  override fun decodeChar(): Char = prefs.getValue(descriptor.getElementName(index++))!!.single()

  override fun decodeDouble(): Double =
    prefs.getValue(descriptor.getElementName(index++))!!.toDouble()

  override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
    enumDescriptor.getElementIndex(prefs.getValue(descriptor.getElementName(index++))!!)

  override fun decodeFloat(): Float = prefs.getValue(descriptor.getElementName(index++))!!.toFloat()

  override fun decodeInt(): Int = prefs.getValue(descriptor.getElementName(index++))!!.toInt()

  override fun decodeLong(): Long = prefs.getValue(descriptor.getElementName(index++))!!.toLong()

  override fun decodeNotNullMark(): Boolean = prefs[descriptor.getElementName(index)] != null

  override fun decodeNull(): Nothing? = null.also { index++ }

  override fun decodeShort(): Short = prefs.getValue(descriptor.getElementName(index++))!!.toShort()

  override fun decodeString(): String = prefs.getValue(descriptor.getElementName(index++))!!
}

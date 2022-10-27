/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

class OneLevelEmittingEncoder(
  override val serializersModule: SerializersModule,
  private val embeddedFormat: StringFormat,
  private val descriptor: SerialDescriptor,
  private val consumer: (String) -> Unit
) : AbstractEncoder() {
  override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
    if (serializer.descriptor == this.descriptor ||
      serializer.descriptor.kind is PrimitiveKind ||
      serializer.descriptor.kind == SerialKind.ENUM
    ) super.encodeSerializableValue(serializer, value)
    else encodeString(embeddedFormat.encodeToString(serializer, value))
  }

  override fun encodeBoolean(value: Boolean) {
    encodeInt(if (value) 1 else 0)
  }

  override fun encodeByte(value: Byte) {
    consumer(value.toString())
  }

  override fun encodeChar(value: Char) {
    consumer(value.toString())
  }

  override fun encodeDouble(value: Double) {
    consumer(value.toString())
  }

  override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
    consumer(enumDescriptor.getElementName(index))
  }

  override fun encodeFloat(value: Float) {
    consumer(value.toString())
  }

  override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder = this

  override fun encodeInt(value: Int) {
    consumer(value.toString())
  }

  override fun encodeLong(value: Long) {
    consumer(value.toString())
  }

  override fun encodeNull() {
    consumer("NULL")
  }

  override fun encodeShort(value: Short) {
    consumer(value.toString())
  }

  override fun encodeString(value: String) {
    consumer(value)
  }
}

class CursorDecoder(
  private val cursor: Cursor,
  override val serializersModule: SerializersModule,
  private val descriptor: SerialDescriptor,
  private val embeddedFormat: StringFormat
) : AbstractDecoder() {
  private var index = 0

  override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = this

  override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
    // we skip non nullable elements which are null in the cursor
    while (index <= descriptor.elementsCount - 1 &&
      !descriptor.getElementDescriptor(index).isNullable &&
      cursor.isNull(cursor.getColumnIndex(descriptor.getElementName(index)))
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
    if (cursor.isNull(index)) null.also { index++ } else decodeSerializableValue(deserializer)

  override fun decodeBoolean(): Boolean = decodeLong() == 1L

  override fun decodeByte(): Byte = decodeLong().toByte()

  override fun decodeChar(): Char = decodeString().single()

  override fun decodeDouble(): Double =
    cursor.getDouble(cursor.getColumnIndex(descriptor.getElementName(index++)))!!

  override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
    enumDescriptor.getElementIndex(decodeString())

  override fun decodeFloat(): Float = decodeDouble().toFloat()

  override fun decodeInt(): Int = decodeLong().toInt()

  override fun decodeLong(): Long =
    cursor.getLong(cursor.getColumnIndex(descriptor.getElementName(index++)))!!

  override fun decodeNotNullMark(): Boolean = !cursor.isNull(index)

  override fun decodeNull(): Nothing? = null.also { index++ }

  override fun decodeShort(): Short =
    decodeLong().toShort()

  override fun decodeString(): String =
    cursor.getString(cursor.getColumnIndex(descriptor.getElementName(index++)))!!
}

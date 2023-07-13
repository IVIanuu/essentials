/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import com.ivianuu.injekt.common.TypeKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.PrimitiveKind

interface EntityDescriptor<T> {
  val key: TypeKey<T>

  val tableName: String

  val rows: List<Row>

  val serializer: KSerializer<T>
}

context(TypeKey<T>, KSerializer<T>)
fun <T> EntityDescriptor(tableName: String): EntityDescriptor<T> =
  EntityDescriptorImpl(tableName, this@TypeKey, this@KSerializer)

@Target(AnnotationTarget.CLASS)
@SerialInfo
annotation class Entity

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class PrimaryKey

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class AutoIncrement

private class EntityDescriptorImpl<T>(
  override val tableName: String,
  override val key: TypeKey<T>,
  override val serializer: KSerializer<T>
) : EntityDescriptor<T> {
  override val rows: List<Row> = (0 until serializer.descriptor.elementsCount)
    .map { elementIndex ->
      val annotations = serializer.descriptor.getElementAnnotations(elementIndex)
      Row(
        name = serializer.descriptor.getElementName(elementIndex),
        type = when (serializer.descriptor.getElementDescriptor(elementIndex).kind) {
          PrimitiveKind.BOOLEAN -> Row.Type.INT
          PrimitiveKind.BYTE -> Row.Type.INT
          PrimitiveKind.CHAR -> Row.Type.STRING
          PrimitiveKind.SHORT -> Row.Type.INT
          PrimitiveKind.INT -> Row.Type.INT
          PrimitiveKind.LONG -> Row.Type.INT
          PrimitiveKind.FLOAT -> Row.Type.DOUBLE
          PrimitiveKind.DOUBLE -> Row.Type.DOUBLE
          else -> Row.Type.STRING
        },
        isNullable = serializer.descriptor.getElementDescriptor(elementIndex).isNullable,
        isPrimaryKey = annotations.any { it is PrimaryKey },
        autoIncrement = annotations.any { it is AutoIncrement }
      )
    }

  init {
    check(serializer.descriptor.annotations.any { it is Entity }) {
      "Entity needs to be annotated with @Entity"
    }

    rows
      .filter { it.isPrimaryKey }
      .let { primaryKeys ->
        when {
          primaryKeys.size == 0 -> error("Entity needs @PrimaryKey")
          primaryKeys.size > 1 -> error("Entity can only have one @PrimaryKey")
          else -> {}
        }
      }
  }
}

data class Row(
  val name: String,
  val type: Type,
  val isNullable: Boolean,
  val isPrimaryKey: Boolean,
  val autoIncrement: Boolean
) {
  enum class Type { STRING, INT, BYTES, DOUBLE }
}

context(TypeKey<T>) fun <T> T.toSqlColumnsAndArgsString(schema: Schema): String = buildString {
  val descriptor = schema.descriptor<T>()

  val rowsWithValues = descriptor.rows.zip(
    buildList {
      OneLevelEmittingEncoder(
        schema.serializersModule,
        schema.embeddedFormat,
        descriptor.serializer.descriptor
      ) { this += it }
        .encodeSerializableValue(descriptor.serializer, this@toSqlColumnsAndArgsString)
    }
  ).filter {
    !it.first.autoIncrement ||
        (it.second != "NULL" && it.second != "0")
  }

  append("(")
  append(rowsWithValues.joinToString { it.first.name })
  append(") ")

  append("VALUES(")

  append(rowsWithValues.joinToString { it.second.toSqlArg(it.first) })
  append(")")
}

fun <T> T.toSqlArg(row: Row): String = if (row.type == Row.Type.STRING &&
  (!row.isNullable || this != "NULL")
) "'${toString()}'" else toString()

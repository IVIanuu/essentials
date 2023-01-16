/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.inject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind

interface EntityDescriptor<T> {
  val typeKey: TypeKey<T>

  val tableName: String

  val rows: List<Row>

  val serializer: KSerializer<T>
}

// todo replace @Inject with context receivers once supported
abstract class AbstractEntityDescriptor<T>(
  tableName: String,
  @Inject K: TypeKey<T>,
  @Inject S: KSerializer<T>
) : EntityDescriptor<T> by EntityDescriptor(tableName)

@Target(AnnotationTarget.CLASS)
@SerialInfo
annotation class Entity

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class PrimaryKey

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class AutoIncrement

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class Relation

context(TypeKey<T>, KSerializer<T>)
fun <T> EntityDescriptor(tableName: String): EntityDescriptor<T> =
  EntityDescriptorImpl(tableName, inject(), inject())

private class EntityDescriptorImpl<T>(
  override val tableName: String,
  override val typeKey: TypeKey<T>,
  override val serializer: KSerializer<T>
) : EntityDescriptor<T> {
  override val rows: List<Row> = (0 until serializer.descriptor.elementsCount)
    .map { elementIndex ->
      val annotations = serializer.descriptor.getElementAnnotations(elementIndex)
      val isRelation = annotations.any { it is Relation }

      val kind: PrimitiveKind
      val relationPrimaryKeyIndex: Int?
      if (isRelation) {
        val relationDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
        check(relationDescriptor.annotations.any { it is Entity }) {
          "Relation must be a entity"
        }

        val _relationPrimaryKeyIndex = (0 until relationDescriptor.elementsCount)
          .singleOrNull {
            relationDescriptor.getElementAnnotations(it)
              .any { it is PrimaryKey }
          }

        checkNotNull(_relationPrimaryKeyIndex) {
          "Relation has no primary key"
        }

        kind = relationDescriptor.getElementDescriptor(_relationPrimaryKeyIndex).kind.cast()
        relationPrimaryKeyIndex = _relationPrimaryKeyIndex
      } else {
        kind = serializer.descriptor.getElementDescriptor(elementIndex).kind.cast()
        relationPrimaryKeyIndex = null
      }

      Row(
        name = serializer.descriptor.getElementName(elementIndex),
        type = kind.toRowType(),
        kind = kind,
        isNullable = serializer.descriptor.getElementDescriptor(elementIndex).isNullable,
        isPrimaryKey = annotations.any { it is PrimaryKey },
        autoIncrement = annotations.any { it is AutoIncrement },
        isRelation = isRelation,
        relationPrimaryKeyIndex = relationPrimaryKeyIndex
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

private fun SerialKind.toRowType() = when (this) {
  PrimitiveKind.BOOLEAN -> Row.Type.INT
  PrimitiveKind.BYTE -> Row.Type.INT
  PrimitiveKind.CHAR -> Row.Type.STRING
  PrimitiveKind.SHORT -> Row.Type.INT
  PrimitiveKind.INT -> Row.Type.INT
  PrimitiveKind.LONG -> Row.Type.INT
  PrimitiveKind.FLOAT -> Row.Type.DOUBLE
  PrimitiveKind.DOUBLE -> Row.Type.DOUBLE
  else -> Row.Type.STRING
}

data class Row(
  val name: String,
  val type: Type,
  val kind: PrimitiveKind,
  val isNullable: Boolean,
  val isPrimaryKey: Boolean,
  val autoIncrement: Boolean,
  val isRelation: Boolean,
  val relationPrimaryKeyIndex: Int?
) {
  enum class Type {
    STRING,
    INT,
    BYTES,
    DOUBLE
  }
}

context(TypeKey<T>) fun <T> T.toSqlColumnsAndArgsString(schema: Schema): String = buildString {
  val descriptor = schema.descriptor<T>()

  val rowsWithValues = descriptor.rows.zip(
    buildList {
      OneLevelEmittingEncoder(
        schema.serializersModule,
        schema.embeddedFormat,
        descriptor.serializer.descriptor,
        descriptor
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

package com.ivianuu.essentials.db

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Inject1
import com.ivianuu.injekt.Inject2
import com.ivianuu.injekt.common.TypeKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.PrimitiveKind

interface EntityDescriptor<T> {
  val typeKey: TypeKey<T>

  val tableName: String

  val rows: List<Row>

  val serializer: KSerializer<T>
}

@Inject2<TypeKey<T>, KSerializer<T>> abstract class AbstractEntityDescriptor<T>(
  tableName: String
) : EntityDescriptor<T> by EntityDescriptor(tableName)

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class PrimaryKey

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class AutoIncrement

fun <T> EntityDescriptor(
  tableName: String,
  @Inject key: TypeKey<T>,
  serializer: KSerializer<T>
): EntityDescriptor<T> = EntityDescriptorImpl(tableName, key, serializer)

private class EntityDescriptorImpl<T>(
  override val tableName: String,
  override val typeKey: TypeKey<T>,
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
}

data class Row(
  val name: String,
  val type: Type,
  val isNullable: Boolean,
  val isPrimaryKey: Boolean,
  val autoIncrement: Boolean
) {
  enum class Type {
    STRING,
    INT,
    BYTES,
    DOUBLE
  }
}

@Inject1<TypeKey<T>> fun <T> T.toSqlColumnsAndArgsString(schema: Schema): String = buildString {
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
  (!row.isNullable || this != "NULL")) "'${toString()}'" else toString()

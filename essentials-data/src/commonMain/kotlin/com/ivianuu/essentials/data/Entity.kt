package com.ivianuu.essentials.data

import com.ivianuu.injekt.Inject
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

@Target(AnnotationTarget.PROPERTY)
@SerialInfo
annotation class PrimaryKey

fun <T> EntityDescriptor(
  tableName: String,
  @Inject typeKey: TypeKey<T>,
  @Inject serializer: KSerializer<T>
): EntityDescriptor<T> = EntityDescriptorImpl(tableName, typeKey, serializer)

private class EntityDescriptorImpl<T>(
  override val tableName: String,
  override val typeKey: TypeKey<T>,
  override val serializer: KSerializer<T>
) : EntityDescriptor<T> {
  override val rows: List<Row> = (0 until serializer.descriptor.elementsCount)
    .map { elementIndex ->
      Row(
        name = serializer.descriptor.getElementName(elementIndex),
        type = when {
          serializer.descriptor.kind == PrimitiveKind.BOOLEAN -> Row.Type.LONG
          serializer.descriptor.kind == PrimitiveKind.BYTE -> Row.Type.LONG
          serializer.descriptor.kind == PrimitiveKind.CHAR -> Row.Type.STRING
          serializer.descriptor.kind == PrimitiveKind.SHORT -> Row.Type.LONG
          serializer.descriptor.kind == PrimitiveKind.INT -> Row.Type.LONG
          serializer.descriptor.kind == PrimitiveKind.LONG -> Row.Type.LONG
          serializer.descriptor.kind == PrimitiveKind.FLOAT -> Row.Type.DOUBLE
          serializer.descriptor.kind == PrimitiveKind.DOUBLE -> Row.Type.DOUBLE
          else -> Row.Type.STRING
        },
        isNullable = serializer.descriptor.isNullable,
        isPrimaryKey = serializer.descriptor.getElementAnnotations(elementIndex)
          .any { it is PrimaryKey }
      )
    }
}

data class Row(
  val name: String,
  val type: Type,
  val isNullable: Boolean,
  val isPrimaryKey: Boolean
) {
  enum class Type {
    STRING,
    LONG,
    BYTES,
    DOUBLE
  }
}

@OptIn(ExperimentalStdlibApi::class)
fun <T> T.toSqlRowsAndArgsString(schema: Schema, @Inject key: TypeKey<T>): String = buildString {
  val descriptor = schema.descriptor<T>()

  append("(")
  append(descriptor.rows.joinToString { it.name })
  append(") ")

  append("VALUES(")
  val values = buildList {
    EmittingEncoder(
      schema.serializersModule,
      schema.embeddedFormat,
      descriptor.serializer.descriptor
    ) { this += it }
      .encodeSerializableValue(descriptor.serializer, this@toSqlRowsAndArgsString)
  }
  append(
    values.zip(descriptor.rows)
      .joinToString { it.first.toSqlArg(it.second) }
  )
  append(")")
}

fun <T> T.toSqlArg(row: Row) =
  if (row.type == Row.Type.STRING) "'${toString()}'" else toString()

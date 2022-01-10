/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import androidx.test.core.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.*

@RunWith(RobolectricTestRunner::class)
class AndroidDbTest {
  @Test fun testSelectAll() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))

    db.selectAll<MyEntity>().first() shouldBe listOf(MyEntity("Manuel", 25))

    db.dispose()
  }

  @Test fun testInsert() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))

    db.selectAll<MyEntity>().first() shouldBe listOf(MyEntity("Manuel", 25))

    db.dispose()
  }

  @Test fun testInsertConflictWithAbort() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))
    catch { db.insert(MyEntity("Manuel", 24), InsertConflictStrategy.ABORT) }

    db.selectAll<MyEntity>().first() shouldBe listOf(MyEntity("Manuel", 25))

    db.dispose()
  }

  @Test fun testInsertConflictWithIgnore() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))
    db.insert(MyEntity("Manuel", 24), InsertConflictStrategy.IGNORE)

    db.selectAll<MyEntity>().first() shouldBe listOf(MyEntity("Manuel", 25))

    db.dispose()
  }

  @Test fun testInsertConflictWithReplace() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))
    db.insert(MyEntity("Manuel", 24), InsertConflictStrategy.REPLACE)

    db.selectAll<MyEntity>().first() shouldBe listOf(MyEntity("Manuel", 24))

    db.dispose()
  }

  @Test fun testInsertAll() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insertAll(listOf(MyEntity("Manuel", 25), MyEntity("Cindy", 23)))

    db.selectAll<MyEntity>().first() shouldBe listOf(
      MyEntity("Manuel", 25),
      MyEntity("Cindy", 23)
    )

    db.dispose()
  }

  @Test fun testQueryWillBeExecutedOnUnspecifiedChange() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    var updates = 0
    db.query("SELECT * FROM MyEntity", null) {
      updates++
    }.launchIn(this)
    advanceUntilIdle()

    updates shouldBe 1

    db.insert(MyEntity("Manuel", 25))

    updates shouldBe 2

    db.dispose()
  }

  @Test fun testQueryWillBeExecutedOnlyOnSpecifiedTableChanges() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(
          EntityDescriptor<MyEntity>(tableName = "MyEntity"),
          EntityDescriptor<MyEntity2>(tableName = "MyEntity2")
        )
      ),
      coroutineContext = coroutineContext
    )

    var updates = 0
    db.query("SELECT * FROM MyEntity", "MyEntity") {
      updates++
    }.launchIn(this)
    advanceUntilIdle()

    updates shouldBe 1

    db.insert(MyEntity("Manuel", 25))

    updates shouldBe 2

    db.insert(MyEntity2("Manuel", 25))

    updates shouldBe 2

    db.dispose()
  }

  @Test fun testQueryWillBeExecutedOnlyOncePerTransaction() = runBlocking {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      )
    )

    var updates = 0
    val queryJob = launch {
      db.query("SELECT * FROM MyEntity", "MyEntity") {
        updates++
      }.collect()
    }

    delay(500)

    updates shouldBe 1

    db.transaction {
      db.insert(MyEntity("Manuel", 25))
      db.insert(MyEntity("Cindy", 23))
    }

    delay(500)
    queryJob.cancelAndJoin()

    updates shouldBe 2

    db.dispose()
  }

  @Test fun testSelectById() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))

    db.selectById<MyEntity>("Manuel").first() shouldBe MyEntity("Manuel", 25)

    db.dispose()
  }

  @Test fun testDeleteById() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))
    db.insert(MyEntity("Cindy", 23))

    db.deleteById<MyEntity>("Cindy")

    db.selectAll<MyEntity>().first() shouldBe listOf(MyEntity("Manuel", 25))

    db.dispose()
  }

  @Test fun testDeleteAll() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(MyEntity("Manuel", 25))

    db.deleteAll<MyEntity>()

    db.selectAll<MyEntity>().first() shouldBe emptyList()

    db.dispose()
  }

  @Test fun testSelectAllEmitsChanges() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    val collector = db.selectAll<MyEntity>().testCollect(this)

    advanceUntilIdle()

    collector.values.size shouldBe 1
    collector.values.last() shouldBe emptyList()

    val entity1 = MyEntity("Manuel", 25)
    val entity2 = MyEntity("Cindy", 23)

    db.insert(entity1)

    collector.values.size shouldBe 2
    collector.values.last() shouldBe listOf(entity1)

    db.insert(entity2)

    collector.values.size shouldBe 3
    collector.values.last() shouldBe listOf(entity1, entity2)

    db.deleteAll<MyEntity>()

    collector.values.size shouldBe 4
    collector.values.last() shouldBe emptyList()

    db.dispose()
  }

  @Test fun testMigrationWithNewRowInBetweenExistingOnes() = runCancellingBlockingTest {
    val v1Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<Migration1MyEntityV1>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    v1Db.insert(Migration1MyEntityV1("Manuel", 25))
    
    v1Db.dispose()

    val v2Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 2,
        entities = listOf(
          EntityDescriptor<Migration1MyEntityV2>(tableName = "MyEntity")
        ),
        migrations = listOf(
          Migration(1, 2) { db, _, _ ->
            db.execute(
              "ALTER TABLE MyEntity ADD COLUMN height LONG DEFAULT NULL",
              "MyEntity"
            )
          }
        )
      ),
      coroutineContext = coroutineContext
    )

    v2Db.selectById<Migration1MyEntityV2>("Manuel").first() shouldBe
        Migration1MyEntityV2("Manuel", 183, 25)

    v2Db.dispose()
  }

  @Test fun testMigrationWithNewRowAfterExistingOnes() = runCancellingBlockingTest {
    val v1Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<Migration2MyEntityV1>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    v1Db.insert(Migration2MyEntityV1("Manuel", 25))

    v1Db.dispose()

    val v2Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 2,
        entities = listOf(
          EntityDescriptor<Migration2MyEntityV2>(tableName = "MyEntity")
        ),
        migrations = listOf(
          Migration(1, 2) { db, _, _ ->
            db.execute(
              "ALTER TABLE MyEntity ADD COLUMN height LONG DEFAULT NULL",
              "MyEntity"
            )
          }
        )
      ),
      coroutineContext = coroutineContext
    )

    v2Db.selectById<Migration2MyEntityV2>("Manuel").first() shouldBe
        Migration2MyEntityV2("Manuel", 25, 183)

    v2Db.dispose()
  }

  @Test fun testMigrationWhichRemovesRow() = runCancellingBlockingTest {
    val v1Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<Migration3MyEntityV1>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    v1Db.insert(Migration3MyEntityV1("Manuel", 25))

    v1Db.dispose()

    val v2Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 2,
        entities = listOf(
          EntityDescriptor<Migration3MyEntityV2>(tableName = "MyEntity")
        ),
        migrations = listOf(
          Migration(1, 2) { db, _, _ ->
            db.createTable<Migration3MyEntityV2>(
              entity = EntityDescriptor(tableName = "MyEntity"),
              tableName = "MyEntity_new"
            )
            db.execute("INSERT INTO MyEntity_new (name) SELECT name FROM MyEntity", "MyEntity")
            db.execute("DROP TABLE MyEntity", "MyEntity")
            db.execute("ALTER TABLE MyEntity_new RENAME TO MyEntity", "MyEntity")
          }
        )
      ),
      coroutineContext = coroutineContext
    )

    v2Db.selectById<Migration3MyEntityV2>("Manuel").first() shouldBe
        Migration3MyEntityV2("Manuel")

    v2Db.dispose()
  }

  @Test fun testEntityWithEmbeddedObject() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<UserWithDog>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(UserWithDog("Manuel", Dog("Luca")))

    db.selectAll<UserWithDog>().first() shouldBe listOf(UserWithDog("Manuel", Dog("Luca")))

    db.dispose()
  }

  @Test fun testEntityWithEmbeddedListObject() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<UserWithMultipleDogs>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(UserWithMultipleDogs("Manuel", listOf(Dog("Luca"), Dog("Bello"))))

    db.selectAll<UserWithMultipleDogs>().first() shouldBe
        listOf(UserWithMultipleDogs("Manuel", listOf(Dog("Luca"), Dog("Bello"))))

    db.dispose()
  }

  @Test fun testEntityWithEmbeddedEnum() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<EntityWithEnum>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(EntityWithEnum("1", MyEnum.A))

    db.selectAll<EntityWithEnum>().first() shouldBe listOf(EntityWithEnum("1", MyEnum.A))

    db.dispose()
  }

  @Test fun testEntityWithEnumId() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<EntityWithEnumId>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(EntityWithEnumId(MyEnum.A))

    db.selectById<EntityWithEnumId>(MyEnum.A).first() shouldBe EntityWithEnumId(MyEnum.A)

    db.dispose()
  }

  @Test fun testEntityWithNullableStringField() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<EntityWithNullableStringField>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(EntityWithNullableStringField("id", null))

    db.selectById<EntityWithNullableStringField>("id").first() shouldBe
        EntityWithNullableStringField("id", null)

    db.dispose()
  }

  @Test fun testEntityWithNullableField() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<EntityWithNullableField>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.insert(EntityWithNullableField("id", null))

    db.selectById<EntityWithNullableField>("id").first() shouldBe
        EntityWithNullableField("id", null)

    db.dispose()
  }

  @Test fun testTransaction() = runBlocking {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      )
    )

    db.transaction {
      val entity = MyEntity("Manuel", 25)
      db.execute("INSERT INTO MyEntity ${entity.toSqlColumnsAndArgsString(db.schema)}", "MyEntity")
    }

    db.selectById<MyEntity>("Manuel").first() shouldBe MyEntity("Manuel", 25)

    db.dispose()
  }

  @Test fun testNonSuccessfulTransactionRollsBackChanges() = runBlocking {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      )
    )

    catch {
      db.transaction {
        val entity = MyEntity("Manuel", 25)
        db.execute("INSERT INTO MyEntity ${entity.toSqlColumnsAndArgsString(db.schema)}", "MyEntity")
        throw RuntimeException()
      }
    }

    db.selectById<MyEntity>("Manuel").first() shouldBe null

    db.dispose()
  }

  @Test fun testNonSuccessfulChildTransactionRollsBackChanges() = runBlocking {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      )
    )

    catch {
      db.transaction {
        val entity = MyEntity("Manuel", 25)
        db.execute("INSERT INTO MyEntity ${entity.toSqlColumnsAndArgsString(db.schema)}", "MyEntity")
        throw RuntimeException()
      }
    }

    db.selectById<MyEntity>("Manuel").first() shouldBe null

    db.dispose()
  }

  @Test fun testAutoIncrementId() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<EntityWithAutoIncrementId>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    val insertedId1 = db.insert(EntityWithAutoIncrementId(name = "Manuel"))

    db.selectById<EntityWithAutoIncrementId>(insertedId1).first() shouldBe
        EntityWithAutoIncrementId(1L, "Manuel")

    val insertedId2 = db.insert(EntityWithAutoIncrementId(name = "Cindy"))

    db.selectById<EntityWithAutoIncrementId>(insertedId2).first() shouldBe
        EntityWithAutoIncrementId(2L, "Cindy")

    db.dispose()
  }

  @Test fun testTableNames() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<MyEntity>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    db.tableNames().first() shouldBe listOf("MyEntity")

    db.dispose()
  }

  @Test fun testDropTable() = runCancellingBlockingTest {
    val v1Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(EntityDescriptor<Migration3MyEntityV1>(tableName = "MyEntity"))
      ),
      coroutineContext = coroutineContext
    )

    v1Db.tableNames().first() shouldBe listOf("MyEntity")

    v1Db.dispose()

    val v2Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 2,
        entities = emptyList(),
        migrations = listOf(
          Migration(1, 2) { db, _, _ ->
            db.dropTable("MyEntity")
          }
        )
      ),
      coroutineContext = coroutineContext
    )

    v2Db.tableNames().first() shouldBe emptyList()

    v2Db.dispose()
  }

  @Test fun testDropAllAndRecreateTables() = runCancellingBlockingTest {
    val v1Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 2,
        entities = listOf(
          EntityDescriptor<Migration3MyEntityV1>(tableName = "MyEntity1"),
          EntityDescriptor<Migration3MyEntityV2>(tableName = "MyEntity2")
        )
      ),
      coroutineContext = coroutineContext
    )

    v1Db.tableNames().first() shouldBe listOf("MyEntity1", "MyEntity2")

    v1Db.dispose()

    val v2Db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(
          EntityDescriptor<Migration3MyEntityV1>(tableName = "MyEntity1")
        ),
        migrations = listOf(
          Migration(2, 1) { db, _, _ ->
            db.dropAllAndRecreateTables()
          }
        )
      ),
      coroutineContext = coroutineContext
    )

    v2Db.tableNames().first() shouldBe listOf("MyEntity1")

    v2Db.dispose()
  }

  @Serializable data class UserWithDog(
    val name: String,
    val dog: Dog
  )

  @Serializable data class Dog(val name: String)

  @Serializable data class UserWithMultipleDogs(
    val name: String,
    val dogs: List<Dog>
  )

  @Serializable data class EntityWithNullableStringField(
    @PrimaryKey val id: String,
    val name: String?
  )

  @Serializable data class EntityWithNullableField(
    @PrimaryKey val id: String,
    val value: Long?
  )

  @Serializable data class EntityWithEnum(
    @PrimaryKey val id: String,
    val enum: MyEnum
  )

  @Serializable data class EntityWithEnumId(
    @PrimaryKey val enum: MyEnum
  )

  enum class MyEnum {
    A, B, C
  }

  @Serializable data class MyEntity(
    @PrimaryKey val name: String,
    val age: Int
  )

  @Serializable data class MyEntity2(
    @PrimaryKey val name: String,
    val age: Int
  )

  @Serializable data class Migration1MyEntityV1(
    @PrimaryKey val name: String,
    val age: Int
  )

  @Serializable data class Migration1MyEntityV2(
    @PrimaryKey val name: String,
    val height: Int = 183,
    val age: Int
  )

  @Serializable data class Migration2MyEntityV1(
    @PrimaryKey val name: String,
    val age: Int
  )

  @Serializable data class Migration2MyEntityV2(
    @PrimaryKey val name: String,
    val age: Int,
    val height: Int = 183
  )

  @Serializable data class Migration3MyEntityV1(
    @PrimaryKey val name: String,
    val age: Int
  )

  @Serializable data class Migration3MyEntityV2(@PrimaryKey val name: String)

  @Serializable data class EntityWithAutoIncrementId(
    @PrimaryKey @AutoIncrement val id: Long = 0L,
    val name: String
  )
}

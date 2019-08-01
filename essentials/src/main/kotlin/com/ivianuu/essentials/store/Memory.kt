package com.ivianuu.essentials.store

import hu.akarnokd.kotlin.flow.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicReference

fun <T> NullableBox(initialValue: T? = null): Box<T?> = MemoryBox(initialValue)

fun <T> MemoryBox(initialValue: T): Box<T> = MemoryBoxImpl(initialValue)

internal class MemoryBoxImpl<T>(initialValue: T) : Box<T> {

    private val value = AtomicReference(initialValue)
    private val subject = PublishSubject<T>()

    override suspend fun set(value: T) {
        this.value.set(value)
        subject.emit(value)
    }

    override suspend fun get(): T = value.get()

    override suspend fun exists(): Boolean = true

    override suspend fun delete() {
    }

    override fun asFlow(): Flow<T> = flow {
        emit(get())
        subject.collect { emit(it) }
    }

}
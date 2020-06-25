package com.ivianuu.essentials.sample

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
class MyClass(val string: String)

@JsonClass(generateAdapter = true)
class MyOtherClass(val int: Int)

fun main() {
    val moshi = Moshi.Builder().build()
    val json = MyClass("hello").let { moshi.adapter<MyClass>(it::class.java).toJson(it) }
    moshi.adapter<MyOtherClass>(MyOtherClass::class.java).fromJson(json)
}

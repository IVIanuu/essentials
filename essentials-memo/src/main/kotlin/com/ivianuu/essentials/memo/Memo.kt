package com.ivianuu.essentials.memo

fun <R> (() -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): () -> R {
    return {  ->
        var key = 0
        cache.memo(key) {
            this()
        }
    }
}
fun <P1, R> ((P1) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1) -> R {
    return { p1 ->
        var key = p1.hashCode()
        cache.memo(key) {
            this(p1)
        }
    }
}
fun <P1, P2, R> ((P1, P2) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2) -> R {
    return { p1, p2 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        cache.memo(key) {
            this(p1, p2)
        }
    }
}
fun <P1, P2, P3, R> ((P1, P2, P3) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3) -> R {
    return { p1, p2, p3 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        cache.memo(key) {
            this(p1, p2, p3)
        }
    }
}
fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4) -> R {
    return { p1, p2, p3, p4 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4)
        }
    }
}
fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5) -> R {
    return { p1, p2, p3, p4, p5 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6) -> R {
    return { p1, p2, p3, p4, p5, p6 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7) -> R {
    return { p1, p2, p3, p4, p5, p6, p7 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        key += 31 * key + p16.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        key += 31 * key + p16.hashCode()
        key += 31 * key + p17.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        key += 31 * key + p16.hashCode()
        key += 31 * key + p17.hashCode()
        key += 31 * key + p18.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        key += 31 * key + p16.hashCode()
        key += 31 * key + p17.hashCode()
        key += 31 * key + p18.hashCode()
        key += 31 * key + p19.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        key += 31 * key + p16.hashCode()
        key += 31 * key + p17.hashCode()
        key += 31 * key + p18.hashCode()
        key += 31 * key + p19.hashCode()
        key += 31 * key + p20.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20)
        }
    }
}
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).memoize(cache: MutableMap<Int, R> = hashMapOf()): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R {
    return { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        key += 31 * key + p3.hashCode()
        key += 31 * key + p4.hashCode()
        key += 31 * key + p5.hashCode()
        key += 31 * key + p6.hashCode()
        key += 31 * key + p7.hashCode()
        key += 31 * key + p8.hashCode()
        key += 31 * key + p9.hashCode()
        key += 31 * key + p10.hashCode()
        key += 31 * key + p11.hashCode()
        key += 31 * key + p12.hashCode()
        key += 31 * key + p13.hashCode()
        key += 31 * key + p14.hashCode()
        key += 31 * key + p15.hashCode()
        key += 31 * key + p16.hashCode()
        key += 31 * key + p17.hashCode()
        key += 31 * key + p18.hashCode()
        key += 31 * key + p19.hashCode()
        key += 31 * key + p20.hashCode()
        key += 31 * key + p21.hashCode()
        cache.memo(key) {
            this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21)
        }
    }
}

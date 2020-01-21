package androidx.compose.plugins.kotlin

import org.jetbrains.kotlin.name.Name

object KtxNameConventions {
    val COMPOSER = Name.identifier("composer")
    val CALL = Name.identifier("call")
    val START_EXPR = Name.identifier("startExpr")
    val END_EXPR = Name.identifier("endExpr")
    val JOINKEY = Name.identifier("joinKey")
    val STARTRESTARTGROUP = Name.identifier("startRestartGroup")
    val ENDRESTARTGROUP = Name.identifier("endRestartGroup")
    val UPDATE_SCOPE = Name.identifier("updateScope")

    val CALL_KEY_PARAMETER = Name.identifier("key")
    val CALL_INVALID_PARAMETER = Name.identifier("invalid")
    val CALL_BLOCK_PARAMETER = Name.identifier("block")
}

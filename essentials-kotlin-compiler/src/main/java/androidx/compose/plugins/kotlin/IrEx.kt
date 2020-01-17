package androidx.compose.plugins.kotlin

import androidx.compose.plugins.kotlin.compiler.lower.ComposeObservePatcher
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class IrEx : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.files.forEach {
            (null as ComposeObservePatcher).lower(it)
        }
    }
}
/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.kotlin.compiler

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class SourceKeyClassBuilderInterceptorExtension : ClassBuilderInterceptorExtension {
    override fun interceptClassBuilderFactory(
        interceptedFactory: ClassBuilderFactory,
        bindingContext: BindingContext,
        diagnostics: DiagnosticSink
    ): ClassBuilderFactory = SourceKeyClassBuilderFactory(interceptedFactory)
}

private class SourceKeyClassBuilderFactory(
    private val delegateFactory: ClassBuilderFactory
) : ClassBuilderFactory {

    override fun newClassBuilder(origin: JvmDeclarationOrigin): ClassBuilder {
        return SourceKeyClassBuilder(
            delegateFactory.newClassBuilder(origin)
        )
    }

    override fun getClassBuilderMode() = delegateFactory.classBuilderMode

    override fun asText(builder: ClassBuilder?): String? {
        return delegateFactory.asText((builder as SourceKeyClassBuilder).delegateClassBuilder)
    }

    override fun asBytes(builder: ClassBuilder?): ByteArray? {
        return delegateFactory.asBytes((builder as SourceKeyClassBuilder).delegateClassBuilder)
    }

    override fun close() {
        delegateFactory.close()
    }
}

private class SourceKeyClassBuilder(
    val delegateClassBuilder: ClassBuilder
) : DelegatingClassBuilder() {

    override fun getDelegate(): ClassBuilder = delegateClassBuilder

    override fun newMethod(
        origin: JvmDeclarationOrigin,
        access: Int,
        name: String,
        desc: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val original = super.newMethod(origin, access, name, desc, signature, exceptions)

        // do not replace with s inline functions
        if (origin.descriptor != null && InlineUtil.isInline(origin.descriptor)) return original

        var lineNumber = 0

        val fqName = "$thisName:$name:$desc:$signature"

        return object : MethodVisitor(Opcodes.ASM5, original) {
            override fun visitLineNumber(line: Int, start: Label?) {
                super.visitLineNumber(line, start)
                lineNumber = line
            }

            override fun visitMethodInsn(
                opcode: Int,
                owner: String?,
                name: String?,
                descriptor: String?,
                isInterface: Boolean
            ) {
                if (opcode == Opcodes.INVOKESTATIC &&
                    owner == "com/ivianuu/essentials/util/SourceKeyKt" &&
                    name == "_sourceKey" &&
                    descriptor == "()Ljava/lang/Object;"
                ) {
                    InstructionAdapter(this).apply {
                        aconst("$fqName:$lineNumber")
                    }
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
            }
        }
    }
}

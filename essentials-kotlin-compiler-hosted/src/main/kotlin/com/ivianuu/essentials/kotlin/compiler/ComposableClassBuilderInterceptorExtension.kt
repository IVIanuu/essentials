/*
 * Copyright 2019 Manuel Wrage
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

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

class ComposableClassBuilderInterceptorExtension : ClassBuilderInterceptorExtension {
    override fun interceptClassBuilderFactory(
        interceptedFactory: ClassBuilderFactory,
        bindingContext: BindingContext,
        diagnostics: DiagnosticSink
    ): ClassBuilderFactory =
        ComposableClassBuilderFactory(interceptedFactory)
}

private class ComposableClassBuilderFactory(
    private val delegateFactory: ClassBuilderFactory
) : ClassBuilderFactory {

    override fun newClassBuilder(origin: JvmDeclarationOrigin): ClassBuilder {
        return ComposableClassBuilder(
            delegateFactory.newClassBuilder(
                origin
            )
        )
    }

    override fun getClassBuilderMode() = delegateFactory.classBuilderMode

    override fun asText(builder: ClassBuilder?): String? {
        return delegateFactory.asText((builder as ComposableClassBuilder).delegateClassBuilder)
    }

    override fun asBytes(builder: ClassBuilder?): ByteArray? {
        return delegateFactory.asBytes((builder as ComposableClassBuilder).delegateClassBuilder)
    }

    override fun close() {
        delegateFactory.close()
    }
}


private class ComposableClassBuilder(val delegateClassBuilder: ClassBuilder) :
    DelegatingClassBuilder() {

    override fun getDelegate(): ClassBuilder = delegateClassBuilder

    private var currentClass: KtClass? = null
    private var currentClassName: String? = null

    override fun defineClass(
        origin: PsiElement?,
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String,
        interfaces: Array<out String>
    ) {
        if (origin is KtClass) {
            currentClass = origin
            currentClassName = name
            println("defining class $name $origin")
        }
        super.defineClass(origin, version, access, name, signature, superName, interfaces)
    }

    override fun newMethod(
        origin: JvmDeclarationOrigin,
        access: Int,
        name: String,
        desc: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val original = super.newMethod(origin, access, name, desc, signature, exceptions)

        val functionDescriptor = origin.descriptor as? FunctionDescriptor ?: return original

        if (!functionDescriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return original
        if (functionDescriptor.returnType?.isUnit() != true) return original

        return object : MethodVisitor(Opcodes.ASM5, original) {
            /*override fun visitCode() {
                with(InstructionAdapter(this)) {
                    getComposer()
                    invokestatic(
                        "com/ivianuu/essentials/util/SourceLocationKt",
                        "sourceLocation",
                        "()Ljava/lang/Object;",
                        false
                    )
                    invokevirtual(
                        "androidx/compose/ViewComposer",
                        "startRestartGroup",
                        "(Ljava/lang/Object;)V",
                        false
                    )
                }

                super.visitCode()
            }

            override fun visitInsn(opcode: Int) {
                if (opcode == Opcodes.RETURN) {
                    with(InstructionAdapter(this)) {
                        getComposer()
                        invokevirtual(
                            "androidx/compose/ViewComposer",
                            "endRestartGroup",
                            "()Landroidx/compose/ScopeUpdateScope;",
                            false
                        )
                    }
                }
                super.visitInsn(opcode)
            }*/
        }
    }
}
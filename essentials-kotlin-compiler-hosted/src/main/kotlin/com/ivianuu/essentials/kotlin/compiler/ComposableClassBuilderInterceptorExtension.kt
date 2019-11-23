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
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class ComposableClassBuilderInterceptorExtension : ClassBuilderInterceptorExtension {
    override fun interceptClassBuilderFactory(
        interceptedFactory: ClassBuilderFactory,
        bindingContext: BindingContext,
        diagnostics: DiagnosticSink
    ): ClassBuilderFactory = ComposableClassBuilderFactory(interceptedFactory)
}

private class ComposableClassBuilderFactory(
    private val delegateFactory: ClassBuilderFactory
) : ClassBuilderFactory {

    override fun newClassBuilder(origin: JvmDeclarationOrigin): ClassBuilder {
        return ComposableClassBuilder(
            delegateFactory.newClassBuilder(
                origin
            ),
            origin
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


private class ComposableClassBuilder(
    val delegateClassBuilder: ClassBuilder,
    val origin: JvmDeclarationOrigin
) :
    DelegatingClassBuilder() {

    override fun getDelegate(): ClassBuilder = delegateClassBuilder

    private var currentClassName: String? = null

    init {
        println("origin ${origin.descriptor}")
    }

    override fun defineClass(
        origin: PsiElement?,
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String,
        interfaces: Array<out String>
    ) {
        super.defineClass(origin, version, access, name, signature, superName, interfaces)
        currentClassName = name
        println("current class name $currentClassName")
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

        return original

        if (currentClassName == null) return original
        val functionDescriptor = origin.descriptor as? FunctionDescriptor ?: return original
        if (!functionDescriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return original
        if (InlineUtil.isInline(functionDescriptor)) return original
        if (!functionDescriptor.returnType!!.isUnit()) return original

        var lineNumber = 0

        return object : MethodVisitor(Opcodes.ASM5, original) {
            override fun visitLineNumber(line: Int, start: Label?) {
                super.visitLineNumber(line, start)
                lineNumber = line
            }

            override fun visitCode() {
                super.visitCode()
                InstructionAdapter(this).apply {
                    getComposer()
                    iconst(origin.descriptor!!.fqNameSafe.hashCode() xor lineNumber)
                    val objectType = Type.getType("Ljava/lang/Object;")
                    StackValue.coerce(Type.INT_TYPE, objectType, this)
                    invokevirtual(
                        "androidx/compose/ViewComposer",
                        "startRestartGroup",
                        "(Ljava/lang/Object;)V",
                        false
                    )
                }
            }

            override fun visitInsn(opcode: Int) {
                if (opcode == Opcodes.RETURN) {
                    InstructionAdapter(this).apply {
                        getComposer()
                        invokevirtual(
                            "androidx/compose/ViewComposer",
                            "endRestartGroup",
                            "()V",
                            false
                        )
                        dup()
                        val returnLabel = Label()
                        val nullLabel = Label()
                        ifnull(nullLabel)
                        val updateScopeName =
                            "${currentClassName!!}/${functionDescriptor.name}__UpdateScope"
                        anew(Type.getType("L$updateScopeName;"))
                        dup()
                        invokespecial(updateScopeName, "<init>", "()V", false)
                        invokeinterface(
                            "androidx/compose/ScopeUpdateScope",
                            "updateScope",
                            "(Lkotlin/jvm/functions/Function0;)V"
                        )
                        goTo(returnLabel)

                        mark(nullLabel)
                        pop()

                        mark(returnLabel)
                    }
                }

                super.visitInsn(opcode)
            }
        }
    }
}
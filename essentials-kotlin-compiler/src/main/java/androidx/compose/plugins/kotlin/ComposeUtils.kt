/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.plugins.kotlin

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

object ComposeUtils {

    fun generateComposePackageName() = "androidx.compose"

    fun composeFqName(cname: String) = FqName("${generateComposePackageName()}.$cname")

    fun setterMethodFromPropertyName(name: String): String {
        return "set${name[0].toUpperCase()}${name.slice(1 until name.length)}"
    }

}

fun KtFunction.isEmitInline(bindingContext: BindingContext): Boolean {
    if (this !is KtFunctionLiteral) return false
    if (parent?.parent !is KtLambdaArgument) return false
    val call = parent?.parent?.parent as? KtCallExpression
    val resolvedCall = call?.getResolvedCall(bindingContext)
    return resolvedCall != null &&
            resolvedCall.candidateDescriptor is ComposableEmitDescriptor
}

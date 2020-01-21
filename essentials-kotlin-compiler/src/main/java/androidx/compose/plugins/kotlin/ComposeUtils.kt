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

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.LambdaArgument
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.scopes.receivers.Receiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

object ComposeUtils {

    fun generateComposePackageName() = "androidx.compose"

    fun composeFqName(cname: String) = FqName("${generateComposePackageName()}.$cname")

}

fun makeCall(
    callElement: KtElement,
    calleeExpression: KtExpression? = null,
    valueArguments: List<ValueArgument> = emptyList(),
    receiver: Receiver? = null,
    dispatchReceiver: ReceiverValue? = null
): Call {
    return object : Call {
        override fun getDispatchReceiver(): ReceiverValue? = dispatchReceiver
        override fun getValueArgumentList(): KtValueArgumentList? = null
        override fun getTypeArgumentList(): KtTypeArgumentList? = null
        override fun getExplicitReceiver(): Receiver? = receiver
        override fun getCalleeExpression(): KtExpression? = calleeExpression
        override fun getValueArguments(): List<ValueArgument> = valueArguments
        override fun getCallElement(): KtElement = callElement
        override fun getFunctionLiteralArguments(): List<LambdaArgument> = emptyList()
        override fun getTypeArguments(): List<KtTypeProjection> = emptyList()
        override fun getCallType(): Call.CallType = Call.CallType.DEFAULT
        override fun getCallOperationNode(): ASTNode? = null
    }
}
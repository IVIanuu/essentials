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

import androidx.compose.plugins.kotlin.analysis.ComposeWritableSlices
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.context.CheckArgumentTypesMode
import org.jetbrains.kotlin.resolve.calls.model.DataFlowInfoForArgumentsImpl
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.KotlinType

class ComposerMetadata(
    val type: KotlinType,
    val callDescriptors: List<FunctionDescriptor>
) {

    companion object {
        private fun resolveComposerMethodCandidates(
            name: Name,
            context: BasicCallResolutionContext,
            composerType: KotlinType,
            callResolver: CallResolver,
            psiFactory: KtPsiFactory
        ): Collection<ResolvedCall<*>> {
            val calleeExpression = psiFactory.createSimpleName(name.asString())

            val methodCall = makeCall(
                callElement = context.call.callElement,
                calleeExpression = calleeExpression,
                receiver = TransientReceiver(
                    composerType
                )
            )

            val contextForVariable =
                BasicCallResolutionContext.create(
                    context,
                    methodCall,
                    CheckArgumentTypesMode.CHECK_VALUE_ARGUMENTS,
                    DataFlowInfoForArgumentsImpl(
                        context.dataFlowInfo,
                        methodCall
                    )
                )

            val results = callResolver.resolveCallWithGivenName(
                // it's important that we use "collectAllCandidates" so that extension functions get included
                contextForVariable.replaceCollectAllCandidates(true),
                methodCall,
                calleeExpression,
                name
            )

            return results.allCandidates ?: emptyList()
        }

        fun build(
            composerType: KotlinType,
            callResolver: CallResolver,
            psiFactory: KtPsiFactory,
            resolutionContext: BasicCallResolutionContext
        ): ComposerMetadata {
            val callCandidates = resolveComposerMethodCandidates(
                KtxNameConventions.CALL,
                resolutionContext,
                composerType,
                callResolver,
                psiFactory
            )

            val callDescriptors = callCandidates.mapNotNull {
                it.candidateDescriptor as? FunctionDescriptor
            }

            return ComposerMetadata(composerType, callDescriptors)
        }

        fun getOrBuild(
            descriptor: VariableDescriptor,
            callResolver: CallResolver,
            psiFactory: KtPsiFactory,
            resolutionContext: BasicCallResolutionContext
        ): ComposerMetadata {
            val meta = resolutionContext.trace.bindingContext[
                    ComposeWritableSlices.COMPOSER_METADATA,
                    descriptor
            ]
            return if (meta == null) {
                val built = build(descriptor.type, callResolver, psiFactory, resolutionContext)
                resolutionContext.trace.record(
                    ComposeWritableSlices.COMPOSER_METADATA,
                    descriptor,
                    built
                )
                built
            } else {
                meta
            }
        }
    }

}

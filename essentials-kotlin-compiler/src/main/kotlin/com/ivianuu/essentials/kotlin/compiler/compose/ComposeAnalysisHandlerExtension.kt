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

package com.ivianuu.essentials.kotlin.compiler.compose

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.SingleRootFileViewProvider
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.JavaClassesTrackerImpl
import org.jetbrains.kotlin.load.java.JavaClassesTracker
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.LazyTopDownAnalyzer
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

class ComposeAnalysisHandlerExtension(
    private val outputDir: File
) : AnalysisHandlerExtension {

    private var runComplete = false

    private lateinit var container: ComponentProvider

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        if (runComplete) return null

        try {
            container.get<LazyTopDownAnalyzer>().apply {
                analyzeDeclarations(TopDownAnalysisMode.TopLevelDeclarations, files)
            }
        } catch (e: Exception) {

        }

        files as ArrayList<KtFile>

        files.toList()
            .forEachIndexed { index, file ->
                val changed = test(bindingTrace, container.get(), file)
                if (changed != null && changed != file) {
                    files.removeAt(index)
                    files.add(index, changed)
                }
            }
        runComplete = true

        // fixes IC duplicate exception
        container.get<JavaClassesTracker>().let { tracker ->
            tracker as JavaClassesTrackerImpl
            tracker.javaClass.getDeclaredField("classDescriptors")
                .also { it.isAccessible = true }
                .get(tracker)
                .let { (it as MutableList<Any>).clear() }
        }

        return null
    }

    private var analyzed = false

    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {
        if (analyzed) return null
        analyzed = true
        container = componentProvider
        return AnalysisResult.RetryWithAdditionalRoots(
            bindingTrace.bindingContext,
            module,
            emptyList(),
            emptyList()
        )
    }
}

fun KtFile.withNewSource(newSource: String): KtFile {
    return KtFile(
        viewProvider = MetaFileViewProvider(manager, virtualFile) {
            it?.also {
                it.setText(newSource)
            }
        },
        isCompiled = false
    )
}

class MetaFileViewProvider(
    psiManager: PsiManager,
    virtualFile: VirtualFile,
    val transformation: (Document?) -> Document?
) : SingleRootFileViewProvider(psiManager, virtualFile) {
    private val cachedDocument by lazy {
        transformation(super.getDocument())
    }
    override fun getDocument(): Document? {
        return cachedDocument
    }
}

package androidx.compose.plugins.kotlin.model.analysis

import androidx.compose.plugins.kotlin.model.FrameRecordClassDescriptor
import androidx.compose.plugins.kotlin.model.SyntheticFramePackageDescriptor
import androidx.compose.plugins.kotlin.model.abstractRecordClassName
import androidx.compose.plugins.kotlin.model.findTopLevel
import androidx.compose.plugins.kotlin.model.modelClassName
import androidx.compose.plugins.kotlin.model.recordClassName
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.lazy.ResolveSession

private fun doAnalysis(
    module: ModuleDescriptor,
    bindingTrace: BindingTrace,
    files: Collection<KtFile>,
    resolveSession: ResolveSession
) {
    for (file in files) {
        analyseDeclarations(module, bindingTrace, file.declarations, resolveSession)
    }
}

private fun analyseDeclarations(
    module: ModuleDescriptor,
    bindingTrace: BindingTrace,
    declarations: List<KtDeclaration>,
    resolveSession: ResolveSession
) {
    for (declaration in declarations) {
        val ktClass = declaration as? KtClassOrObject ?: continue

        analyseDeclarations(module, bindingTrace, ktClass.declarations, resolveSession)

        val framedDescriptor = resolveSession.resolveToDescriptor(declaration) as?
                ClassDescriptor ?: continue
        if (!framedDescriptor.hasModelAnnotation()) continue

        val classFqName = ktClass.fqName!!
        val recordFqName = classFqName.parent().child(Name.identifier(
            "${classFqName.shortName()}\$Record")
        )
        val recordSimpleName = recordFqName.shortName()
        val recordPackage =
            SyntheticFramePackageDescriptor(
                module,
                recordFqName.parent()
            )
        val baseTypeDescriptor = module.findTopLevel(abstractRecordClassName)
        val recordDescriptor = module.findTopLevel(recordClassName)
        val baseType = baseTypeDescriptor.defaultType
        val frameClass =
            FrameRecordClassDescriptor(
                recordSimpleName,
                recordPackage,
                recordDescriptor,
                framedDescriptor,
                listOf(baseType),
                bindingTrace.bindingContext
            )

        recordPackage.setClassDescriptor(frameClass)
        bindingTrace.record(FrameWritableSlices.RECORD_CLASS, classFqName, frameClass)
        bindingTrace.record(
            FrameWritableSlices.FRAMED_DESCRIPTOR,
            classFqName,
            framedDescriptor
        )
    }
}

class FramePackageAnalysisHandlerExtension : AnalysisHandlerExtension {
    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {
        val resolveSession = componentProvider.get<ResolveSession>()
        doAnalysis(
            module,
            bindingTrace,
            files,
            resolveSession
        )
        return null
    }
}

private fun Annotated.hasModelAnnotation() = annotations.findAnnotation(modelClassName) != null

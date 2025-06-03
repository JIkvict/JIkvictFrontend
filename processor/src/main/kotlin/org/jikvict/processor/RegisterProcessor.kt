package org.jikvict.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

class RegisterProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String> = emptyMap(),
) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
//        val isCommonMain = resolver.getAllFiles().any { file ->
//            file.filePath.replace("\\", "/").contains("/commonMain/")
//        }
//        logger.warn("isCommonMain: $isCommonMain")
//        logger.warn("Example file path: ${resolver.getAllFiles().firstOrNull()?.filePath}")
//
//        if (!isCommonMain) {
//            return emptyList()
//        }
//
//        val annotationName = "org.jikvict.browser.annotation.Register"
//
//
//        val allAnnotations =
//            resolver
//                .getDeclarationsFromPackage("org.jikvict.browser")
//                .filterIsInstance<KSClassDeclaration>()
//                .filter { it.classKind == ClassKind.ANNOTATION_CLASS }
//
//
//        val screens =
//            resolver
//                .getSymbolsWithAnnotation(annotationName)
//                .filterIsInstance<KSClassDeclaration>()
//                .toList()
//
//        if (screens.isEmpty()) {
//            return emptyList()
//        }
//        try {
//            val content =
//                buildString {
//                    appendLine("package org.jikvict.browser")
//                    appendLine()
//                    appendLine("import org.jikvict.browser.screens.NavigableScreen")
//                    appendLine("import kotlin.reflect.KClass")
//                    screens.forEach { screen ->
//                        val packageName = screen.packageName.asString()
//                        appendLine("import $packageName.${screen.simpleName.asString()}")
//                    }
//                    appendLine()
//                    appendLine("val SCREENS: List<KClass<out NavigableScreen>> = listOf<KClass<out NavigableScreen>>(")
//                    screens.forEach { screen ->
//                        appendLine("    ${screen.simpleName.asString()}::class,")
//                    }
//                    appendLine("  )")
//                }
//
//            val dependencies =
//                Dependencies(
//                    aggregating = true,
//                    *screens.map { it.containingFile!! }.toList().toTypedArray(),
//                )
//
//            codeGenerator
//                .createNewFile(
//                    dependencies = dependencies,
//                    packageName = "org.jikvict.browser",
//                    fileName = "GeneratedScreenRegistry",
//                ).use { file ->
//                    file.write(content.toByteArray())
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        return emptyList()
    }
}

class RegisterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        RegisterProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            options = environment.options,
        )
}

package org.jikvict.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class InitializeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String> = emptyMap(),
) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("Options: $options")
        val isCommonMain = resolver.getAllFiles().any { file ->
            file.filePath.replace("\\", "/").contains("/commonMain/")
        }
        logger.warn("isCommonMain: $isCommonMain")
        logger.warn("Example file path: ${resolver.getAllFiles().firstOrNull()?.filePath}")

        if (!isCommonMain) {
            logger.warn("[InitializeProcessor] Skipping because not commonMain")
            return emptyList()
        }

        logger.warn("[InitializeProcessor] Processing commonMain source set")

        val annotationName = "org.jikvict.browser.annotation.Initialize"

        logger.info("[InitializeProcessor] Looking for annotation: $annotationName")

        val allAnnotations =
            resolver
                .getDeclarationsFromPackage("org.jikvict.browser")
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.ANNOTATION_CLASS }

        logger.info("[InitializeProcessor] Available annotations: ${allAnnotations.map { it.qualifiedName?.asString() }}")

        val screens =
            resolver
                .getSymbolsWithAnnotation(annotationName)
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.OBJECT }
                .toList()

        logger.info("[InitializeProcessor] Found ${screens.count()} screens with @Initialize annotation")

        logger.info("[InitializeProcessor] Found ${screens.count()} screens with @Initialize annotation")

        if (screens.isEmpty()) {
            return emptyList()
        }
        try {
            val content =
                buildString {
                    appendLine("package org.jikvict.browser")
                    appendLine()
                    appendLine("import org.jikvict.browser.screens.NavigableScreen")
                    screens.forEach { screen ->
                        val packageName = screen.packageName.asString()
                        appendLine("import $packageName.${screen.simpleName.asString()}")
                    }
                    appendLine()
                    appendLine("object GeneratedScreenRegistry {")
                    appendLine("  val allScreens = listOf<NavigableScreen>(")
                    screens.forEach { screen ->
                        appendLine("    ${screen.simpleName.asString()},")
                    }
                    appendLine("  )")
                    appendLine("}")
                }

            val dependencies =
                Dependencies(
                    aggregating = true,
                    *screens.map { it.containingFile!! }.toList().toTypedArray(),
                )

            codeGenerator
                .createNewFile(
                    dependencies = dependencies,
                    packageName = "org.jikvict.browser",
                    fileName = "GeneratedScreenRegistry",
                ).use { file ->
                    file.write(content.toByteArray())
                }
        } catch (e: Exception) {
            logger.error("[InitializeProcessor] Error generating file: ${e.message}")
            e.printStackTrace()
        }

        return emptyList()
    }
}

class InitializeProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        InitializeProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            options = environment.options,
        )
}

package com.aleyn.processor

import com.aleyn.processor.generator.generatorClass
import com.aleyn.processor.generator.generatorModule
import com.aleyn.processor.scanner.LRouterMetaDataScanner
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

/**
 * @author: Aleyn
 * @date: 2023/6/13 18:51
 */
private const val L_ROUTER_MODULE_NAME = "L_ROUTER_MODULE_NAME"

class RouterSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    options: Map<String, String>,
) : SymbolProcessor {

    private val moduleName = options[L_ROUTER_MODULE_NAME]

    private val metaDataScanner = LRouterMetaDataScanner(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.logging("LRouter start check symbols")

        val invalidSymbols = metaDataScanner.scanSymbols(resolver)

        if (invalidSymbols.isNotEmpty()) return invalidSymbols

        logger.logging("LRouter start handle")

        val routerAutowired = metaDataScanner.getAutowiredDeclaration()
        routerAutowired.forEach {
            it.generatorClass(codeGenerator, logger)
        }

        metaDataScanner.getModuleDeclaration()
            .generatorModule(codeGenerator, logger, moduleName, routerAutowired)

        return emptyList()
    }
}


class RouterProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return RouterSymbolProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }
}




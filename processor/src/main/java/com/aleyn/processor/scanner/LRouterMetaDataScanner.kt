package com.aleyn.processor.scanner

import com.aleyn.annotation.Autowired
import com.aleyn.annotation.Factory
import com.aleyn.annotation.Route
import com.aleyn.annotation.Singleton
import com.aleyn.processor.data.RouterMeta
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate

/**
 * @author : Aleyn
 * @date : 2023/07/23 : 17:01
 */

val MODULE_INJECT = listOf(Singleton::class, Factory::class)

class LRouterMetaDataScanner(
    private val logger: KSPLogger
) {

    private val injectClassScanner = ClassInjectScanner(logger)

    private var validRouterSymbols = mutableListOf<KSAnnotated>()
    private var validInjectedSymbols = mutableListOf<KSAnnotated>()
    private var validAutowiredSymbols = mutableListOf<KSAnnotated>()

    fun scanSymbols(resolver: Resolver): List<KSAnnotated> {

        val moduleInjectSymbols = MODULE_INJECT.flatMap { annotation ->
            resolver.getSymbolsWithAnnotation(annotation.qualifiedName!!)
        }.groupBy { it.validate() }

        val autowiredSymbols = resolver.getSymbolsWithAnnotation(Autowired::class.qualifiedName!!)
            .groupBy { it.validate() }

        validRouterSymbols.addAll(resolver.getSymbolsWithAnnotation(Route::class.qualifiedName!!))

        moduleInjectSymbols[true]?.let { validInjectedSymbols.addAll(it) }
        autowiredSymbols[true]?.let { validAutowiredSymbols.addAll(it) }

        val invalidInject = moduleInjectSymbols[false] ?: emptyList()
        val invalidAutowired = autowiredSymbols[false] ?: emptyList()
        val allInvalid = invalidInject + invalidAutowired
        if (allInvalid.isNotEmpty()) return allInvalid.onEach { logger.warn("Invalid symbols: $it") }

        logger.logging("All symbols are valid")
        return emptyList()
    }

    fun getModuleDeclaration(): RouterMeta.Module {
        val router = arrayListOf<RouterMeta.ModuleRouter>()

        logger.logging("scan router ...")

        validRouterSymbols
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { it.createModuleRouter() }
            .forEach(router::add)

        logger.logging("scan definitions ...")

        val definition = validInjectedSymbols
            .filterIsInstance<KSClassDeclaration>()
            .map { injectClassScanner.createClassDefinition(it) }
            .toList()

        return RouterMeta.Module(router, definition)
    }


    fun getAutowiredDeclaration(): List<RouterMeta.RouterAutowired> {
        return validAutowiredSymbols
            .asSequence()
            .filterIsInstance<KSPropertyDeclaration>()
            .mapNotNull { it.parentDeclaration }
            .toSet()
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { it.createAutowiredClass() }
            .toList()
    }

}
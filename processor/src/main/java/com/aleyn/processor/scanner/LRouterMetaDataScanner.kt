package com.aleyn.processor.scanner

import com.aleyn.annotation.Autowired
import com.aleyn.annotation.Factory
import com.aleyn.annotation.Initializer
import com.aleyn.annotation.Interceptor
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
    private var validInterceptorSymbols = mutableListOf<KSAnnotated>()
    private var validInitializerSymbols = mutableListOf<KSAnnotated>()

    fun scanSymbols(resolver: Resolver): List<KSAnnotated> {

        val moduleInjectSymbols = MODULE_INJECT.flatMap { annotation ->
            resolver.getSymbolsWithAnnotation(annotation.qualifiedName!!)
        }.groupBy { it.validate() }

        val autowiredSymbols = resolver.getSymbolsWithAnnotation(Autowired::class.qualifiedName!!)
            .groupBy { it.validate() }

        val interceptorSymbols =
            resolver.getSymbolsWithAnnotation(Interceptor::class.qualifiedName!!)
                .groupBy { it.validate() }

        val initializerSymbols =
            resolver.getSymbolsWithAnnotation(Initializer::class.qualifiedName!!)
                .groupBy { it.validate() }

        validRouterSymbols.addAll(resolver.getSymbolsWithAnnotation(Route::class.qualifiedName!!))

        moduleInjectSymbols[true]?.let { validInjectedSymbols.addAll(it) }
        autowiredSymbols[true]?.let { validAutowiredSymbols.addAll(it) }
        interceptorSymbols[true]?.let { validInterceptorSymbols.addAll(it) }
        initializerSymbols[true]?.let(validInitializerSymbols::addAll)

        val invalidInject = moduleInjectSymbols[false] ?: emptyList()
        val invalidAutowired = autowiredSymbols[false] ?: emptyList()
        val invalidInterceptor = interceptorSymbols[false] ?: emptyList()
        val invalidInitializer = initializerSymbols[false] ?: emptyList()
        val allInvalid = invalidInject + invalidAutowired + invalidInterceptor + invalidInitializer
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

        val interceptors = validInterceptorSymbols
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { it.createInterceptor() }
            .toList()

        val initializers = validInitializerSymbols
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { it.createInitializer() }
            .toList()

        return RouterMeta.Module(router, definition, interceptors, initializers)
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
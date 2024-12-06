package com.aleyn.router.plug.entension

import com.android.build.api.variant.VariantExtension
import com.android.build.api.variant.VariantExtensionConfig
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * @author: Aleyn
 * @date: 2024/12/5 15:55
 */

abstract class VariantRouterDslExtension @Inject constructor(extensionConfig: VariantExtensionConfig<*>) :
    VariantExtension, java.io.Serializable {
    abstract val variantOpenASM: Property<Boolean>

    init {
        variantOpenASM.set(extensionConfig.buildTypeExtension(BuildTypeDslExtension::class.java).openASM)
    }
}
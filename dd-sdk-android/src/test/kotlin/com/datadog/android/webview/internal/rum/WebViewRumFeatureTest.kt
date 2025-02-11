/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.webview.internal.rum

import com.datadog.android.core.configuration.Configuration
import com.datadog.android.core.internal.CoreFeature
import com.datadog.android.core.internal.SdkFeatureTest
import com.datadog.android.rum.internal.net.RumOkHttpUploaderV2
import com.datadog.android.utils.forge.Configurator
import com.datadog.tools.unit.extensions.ApiLevelExtension
import com.datadog.tools.unit.extensions.TestConfigurationExtension
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(ApiLevelExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
internal class WebViewRumFeatureTest : SdkFeatureTest<Any,
    Configuration.Feature.RUM, WebViewRumFeature>() {

    override fun createTestedFeature(): WebViewRumFeature {
        return WebViewRumFeature
    }

    override fun forgeConfiguration(forge: Forge): Configuration.Feature.RUM {
        return forge.getForgery()
    }

    override fun featureDirName(): String {
        return "web-rum"
    }

    override fun doesFeatureNeedMigration(): Boolean = false

    @Test
    fun `𝕄 initialize persistence strategy 𝕎 initialize()`() {
        // When
        testedFeature.initialize(appContext.mockInstance, fakeConfigurationFeature)

        // Then
        assertThat(testedFeature.persistenceStrategy)
            .isInstanceOf(WebViewRumFilePersistenceStrategy::class.java)
    }

    @Test
    fun `𝕄 create a logs uploader 𝕎 createUploader()`() {
        // When
        val uploader = testedFeature.createUploader(fakeConfigurationFeature)

        // Then
        assertThat(uploader).isInstanceOf(RumOkHttpUploaderV2::class.java)
        val rumUploader = uploader as RumOkHttpUploaderV2
        assertThat(rumUploader.intakeUrl).startsWith(fakeConfigurationFeature.endpointUrl)
        assertThat(rumUploader.intakeUrl).endsWith("/api/v2/rum")
        assertThat(rumUploader.callFactory).isSameAs(CoreFeature.okHttpClient)
    }
}

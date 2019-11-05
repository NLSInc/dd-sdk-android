/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-2019 Datadog, Inc.
 */

package com.datadog.gradle.plugin

import java.io.File

open class ThirdPartyLicensesExtension(
    var output: File = File(DEFAULT_TP_LICENCE_FILENAME),
    var transitiveDependencies: Boolean = false
) {
    companion object {
        const val DEFAULT_TP_LICENCE_FILENAME = "LICENSE-3rdparty.csv"
    }
}

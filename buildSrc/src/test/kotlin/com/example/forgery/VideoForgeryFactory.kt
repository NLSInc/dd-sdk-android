/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.example.forgery

import com.example.model.Video
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class VideoForgeryFactory : ForgeryFactory<Video> {
    override fun getForgery(forge: Forge): Video {
        return Video(
            title = forge.anAlphabeticalString(),
            links = forge.aNullable { aList { anAlphabeticalString() }.toSet() },
            tags = forge.aNullable { aList { anAlphabeticalString() }.toSet() }
        )
    }
}

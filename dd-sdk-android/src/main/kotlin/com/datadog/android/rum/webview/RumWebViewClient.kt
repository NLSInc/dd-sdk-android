/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.rum.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumAttributes
import com.datadog.android.rum.RumErrorSource
import com.datadog.android.rum.RumResourceKind

/**
 * A [WebViewClient] propagating all relevant events to the [GlobalRum] monitor.
 *
 * This will map the page loading, and webview errors into RUM Resource and
 * Error events respectively.
 *
 * This class is deprecated and you should use the new
 * [com.datadog.android.webview.DatadogEventBridge] to track your WebViews:
 *
 * ```kotlin
 * val configuration = Configuration.Builder().setWebViewTrackingHosts(listOf(<YOUR_HOSTS>))
 * Datadog.initialize(this, credentials, configuration, trackingConsent)
 *
 * // By default, link navigation will be delegated to a third party app.
 * // If you want all navigation to happen inside of the webview, uncomment the following line.
 *
 * // webView.webViewClient = WebViewClient()
 * webView.settings.javaScriptEnabled = true
 * webView.addJavascriptInterface(DatadogEventBridge(), "DatadogEventBridge")
 * ```
 * [See more](https://developer.android.com/guide/webapps/webview#HandlingNavigation)
 *
 */
@Deprecated("You should use the DatadogEventBridge JavaScript interface instead.")
open class RumWebViewClient : WebViewClient() {

    // region WebViewClient

    /** @inheritdoc */
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if (url != null) {
            val key = url
            GlobalRum.get().startResource(
                key,
                METHOD_GET,
                url
            )
        }
    }

    /** @inheritdoc */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null) {
            GlobalRum.get().stopResource(
                url,
                200,
                null,
                RumResourceKind.DOCUMENT,
                onProvideRumResourceAttributes(view, url)
            )
        }
    }

    /** @inheritdoc */
    @Suppress("DEPRECATION")
    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        GlobalRum.get().addError(
            "Error $errorCode: $description",
            RumErrorSource.WEBVIEW,
            null,
            mapOf(RumAttributes.ERROR_RESOURCE_URL to failingUrl)
        )
    }

    /** @inheritdoc */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        GlobalRum.get().addError(
            "Error ${error?.errorCode}: ${error?.description}",
            RumErrorSource.WEBVIEW,
            null,
            mapOf(RumAttributes.ERROR_RESOURCE_URL to request?.url)
        )
    }

    /** @inheritdoc */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        GlobalRum.get().addError(
            "Error ${errorResponse?.statusCode}: ${errorResponse?.reasonPhrase}",
            RumErrorSource.WEBVIEW,
            null,
            mapOf(RumAttributes.ERROR_RESOURCE_URL to request?.url)
        )
    }

    /** @inheritdoc */
    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        super.onReceivedSslError(view, handler, error)
        GlobalRum.get().addError(
            "SSL Error ${error?.primaryError}",
            RumErrorSource.WEBVIEW,
            null,
            mapOf(RumAttributes.ERROR_RESOURCE_URL to error?.url)
        )
    }

    // endregion

    // region Internal

    /**
     * Offers a possibility to create custom attributes collection which later will be attached to
     * the RUM resource event associated with the request.
     * @param view The WebView that is initiating the callback.
     * @param url The url of the page.
     */
    open fun onProvideRumResourceAttributes(view: WebView?, url: String?): Map<String, Any?> {
        return emptyMap()
    }

    // endregion

    companion object {
        internal const val METHOD_GET = "GET"
    }
}

package com.papay.themoviedb.core.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayer(
    videoKey: String,
    title: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                loadYouTubeVideo(
                    videoKey = videoKey,
                    title = title
                )
            }
        },
        update = { webView ->
            if (webView.tag != videoKey) {
                webView.loadYouTubeVideo(
                    videoKey = videoKey,
                    title = title
                )
            }
        }
    )
}

private fun WebView.loadYouTubeVideo(
    videoKey: String,
    title: String
) {
    tag = videoKey
    loadDataWithBaseURL(
        YouTubePlayerOriginUrl,
        youtubeEmbedHtml(
            videoKey = videoKey,
            title = title
        ),
        "text/html",
        "UTF-8",
        null
    )
}

private fun youtubeEmbedHtml(
    videoKey: String,
    title: String
): String {
    val embedUrl = "https://www.youtube.com/embed/$videoKey?playsinline=1&rel=0"
    return """
        <!doctype html>
        <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="referrer" content="strict-origin-when-cross-origin">
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        width: 100%;
                        height: 100%;
                        background: #000;
                        overflow: hidden;
                    }
                    iframe {
                        width: 100%;
                        height: 100%;
                        border: 0;
                    }
                </style>
            </head>
            <body>
                <iframe
                    src="$embedUrl"
                    title="${title.escapeHtml()}"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    referrerpolicy="strict-origin-when-cross-origin"
                    allowfullscreen>
                </iframe>
            </body>
        </html>
    """.trimIndent()
}

private fun String.escapeHtml(): String {
    return replace("&", "&amp;")
        .replace("\"", "&quot;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}

private const val YouTubePlayerOriginUrl = "https://themoviedb.papay.app/"

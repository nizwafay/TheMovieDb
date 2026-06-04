package com.papay.themoviedb.core.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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
                webChromeClient = FullscreenWebChromeClient(context)
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

private class FullscreenWebChromeClient(
    context: Context
) : WebChromeClient() {
    private val activity = context.findActivity()
    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null
    private var backCallback: OnBackPressedCallback? = null
    private var previousRequestedOrientation: Int? = null

    override fun onShowCustomView(
        view: View,
        callback: CustomViewCallback
    ) {
        val decorView = activity?.window?.decorView as? ViewGroup ?: run {
            callback.onCustomViewHidden()
            return
        }

        if (customView != null) {
            callback.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallback = callback
        previousRequestedOrientation = activity.requestedOrientation

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        activity.hideSystemBars()
        activity.registerFullscreenBackCallback()
        view.closeFullscreenOnBackKey()
        decorView.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onHideCustomView() {
        val decorView = activity?.window?.decorView as? ViewGroup ?: return
        customView?.let(decorView::removeView)
        customView = null
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        backCallback?.remove()
        backCallback = null
        previousRequestedOrientation?.let { orientation ->
            activity.requestedOrientation = orientation
        }
        previousRequestedOrientation = null
        activity.showSystemBars()
    }

    private fun Activity.registerFullscreenBackCallback() {
        val componentActivity = this as? ComponentActivity ?: return
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onHideCustomView()
            }
        }.also { callback ->
            componentActivity.onBackPressedDispatcher.addCallback(callback)
        }
    }

    private fun View.closeFullscreenOnBackKey() {
        isFocusableInTouchMode = true
        requestFocus()
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onHideCustomView()
                true
            } else {
                false
            }
        }
    }
}

private fun Activity.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
    }
}

private fun Activity.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowCompat.getInsetsController(window, window.decorView)
        .show(WindowInsetsCompat.Type.systemBars())
}

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
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
    val embedUrl = "https://www.youtube.com/embed/$videoKey?playsinline=1&rel=0&fs=1"
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
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; fullscreen; gyroscope; picture-in-picture; web-share"
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

package org.poc.app

import androidx.compose.ui.window.ComposeUIViewController
import org.poc.app.di.initKoin

fun MainViewController() =
    ComposeUIViewController(
        configure = {
            initKoin()
        },
    ) { App() }

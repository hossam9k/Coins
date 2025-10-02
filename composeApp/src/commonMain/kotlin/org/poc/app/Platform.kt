package org.poc.app

sealed class Platform {
    data object Android : Platform()

    data object Ios : Platform()
}

expect val platform: Platform

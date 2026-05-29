package com.kaanf.crew

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

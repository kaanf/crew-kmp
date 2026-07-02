package com.kaanf.crew.androidapp

import android.app.Application

// Wiretap ships only in debug (see build.gradle debugImplementation); release is a no-op.
fun Application.installDevTools() = Unit

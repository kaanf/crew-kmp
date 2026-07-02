package com.kaanf.crew.androidapp

import android.app.Application
import dev.skymansandy.wiretap.helper.launcher.enableWiretapLauncher

fun Application.installDevTools() {
    enableWiretapLauncher()
}

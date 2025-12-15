package com.github.smykla.monokaiislands.utils

import com.intellij.openapi.application.ApplicationManager

object DevModeDetector {

    fun isDevMode(): Boolean = ApplicationManager.getApplication().isInternal
}

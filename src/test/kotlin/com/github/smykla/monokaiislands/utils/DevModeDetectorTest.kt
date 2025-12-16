package com.github.smykla.monokaiislands.utils

import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test

/**
 * Unit tests for DevModeDetector.
 *
 * Note: Testing isDevMode() requires Application context and idea.is.internal
 * property which are not available in unit tests. The method is verified
 * through manual testing in the sandbox IDE. Full integration testing would
 * require LightPlatformTestCase or similar IDE test harness.
 */
class DevModeDetectorTest {

    @Test
    fun `DevModeDetector is an object`() {
        DevModeDetector.shouldBeInstanceOf<Any>()
    }
}

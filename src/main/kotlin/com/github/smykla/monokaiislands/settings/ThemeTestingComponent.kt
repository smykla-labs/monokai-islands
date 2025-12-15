package com.github.smykla.monokaiislands.settings

import com.github.smykla.monokaiislands.ui.debug.bordersAndSeparatorsGroup
import com.github.smykla.monokaiislands.ui.debug.buttonsGroup
import com.github.smykla.monokaiislands.ui.debug.inputGalleryGroup
import com.github.smykla.monokaiislands.ui.debug.notificationDemoGroup
import com.github.smykla.monokaiislands.ui.debug.progressAndStatusGroup
import com.github.smykla.monokaiislands.ui.debug.statusIndicatorsGroup
import com.github.smykla.monokaiislands.ui.debug.tablesAndDataGroup
import com.github.smykla.monokaiislands.ui.debug.toolbarAndSplitterGroup
import com.github.smykla.monokaiislands.ui.debug.treeListGalleryGroup
import com.github.smykla.monokaiislands.ui.debug.validationFeedbackGroup
import com.intellij.ui.dsl.builder.panel

class ThemeTestingComponent {

    val panel = panel {
        group("Theme Testing Panel") {
            row {
                text(
                    """
                    Visual testing for theme changes during development.
                    This panel is only visible in dev mode (idea.is.internal=true).
                    """.trimIndent()
                )
            }
        }

        buttonsGroup()
        toolbarAndSplitterGroup()
        inputGalleryGroup()
        treeListGalleryGroup()
        tablesAndDataGroup()
        progressAndStatusGroup()
        validationFeedbackGroup()
        notificationDemoGroup()
        bordersAndSeparatorsGroup()
        statusIndicatorsGroup()
    }
}

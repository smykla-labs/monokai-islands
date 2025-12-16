package com.github.smykla.monokaiislands.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JPanel

class WrapLayoutTest {

    @Test
    fun `single row layout when width sufficient`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // Add 5 buttons, each 40px wide
        repeat(5) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
            })
        }

        // Set panel width to accommodate all buttons in one row
        // 5 buttons * 40px + 4 gaps * 5px + 2 * hgap (5px) = 200 + 20 + 10 = 230px
        panel.size = Dimension(230, 100)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Should fit all in one row
        // Width: 5 * 40 + 4 * 5 + horizontal insets (2 * 5) = 200 + 20 + 10 = 230
        // Height: 40 + vertical insets (2 * 5) = 50
        assertEquals(230, preferredSize.width)
        assertEquals(50, preferredSize.height)
    }

    @Test
    fun `multi-row wrapping when width constrained`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // Add 6 buttons, each 40px wide
        repeat(6) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
            })
        }

        // Set panel width to fit only 3 buttons per row
        // 3 buttons * 40px + 2 gaps * 5px + 2 * hgap (5px) = 120 + 10 + 10 = 140px
        panel.size = Dimension(140, 200)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Should create 2 rows of 3 buttons each
        // Width: 3 * 40 + 2 * 5 + 2 * 5 = 140
        // Height: 2 rows * 40 + 1 vgap * 5 + 2 * vgap (5) = 80 + 5 + 10 = 95
        assertEquals(140, preferredSize.width)
        assertEquals(95, preferredSize.height)
    }

    @Test
    fun `preferred size calculation with varying button sizes`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // Add buttons with different heights
        panel.add(JButton().apply { preferredSize = Dimension(40, 30) })
        panel.add(JButton().apply { preferredSize = Dimension(40, 50) })
        panel.add(JButton().apply { preferredSize = Dimension(40, 40) })

        // Set panel width to fit all in one row
        panel.size = Dimension(200, 100)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Row height should be the maximum button height (50)
        // Height: 50 + 2 * vgap (5) = 60
        assertEquals(60, preferredSize.height)
    }

    @Test
    fun `minimum size calculation`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // Add 3 buttons
        repeat(3) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
                minimumSize = Dimension(30, 30)
            })
        }

        panel.size = Dimension(200, 100)

        val minimumSize = layout.minimumLayoutSize(panel)

        // Minimum size should use minimum button sizes
        // Width: 3 * 30 (90) + 2 * 5 (internal gaps) + 2 * 5 (edge gaps) = 110
        // WrapLayout subtracts (hgap + 1) = 6.
        // Result: 104.
        assertEquals(104, minimumSize.width)
        assertEquals(40, minimumSize.height)
    }

    @Test
    fun `gap handling with different gap values`() {
        val layout = WrapLayout(FlowLayout.LEFT, 10, 15)
        val panel = JPanel(layout)

        // Add 2 buttons
        repeat(2) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
            })
        }

        // Set panel width to fit both in one row
        panel.size = Dimension(200, 100)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Width: 2 * 40 + 1 * 10 (hgap between) + 2 * 10 (horizontal insets) = 80 + 10 + 20 = 110
        // Height: 40 + 2 * 15 (vertical insets) = 70
        assertEquals(110, preferredSize.width)
        assertEquals(70, preferredSize.height)
    }

    @Test
    fun `empty container edge case`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // No components added
        panel.size = Dimension(200, 100)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Should return minimal size with just insets
        // Width: 0 + 2 * 5 = 10
        // Height: 0 + 2 * 5 = 10
        assertEquals(10, preferredSize.width)
        assertEquals(10, preferredSize.height)
    }

    @Test
    fun `zero width container uses maximum width`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // Add 3 buttons
        repeat(3) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
            })
        }

        // Panel with zero width (initial state)
        panel.size = Dimension(0, 0)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Should layout all components in single row when width is 0 (uses Int.MAX_VALUE)
        // Width: 3 * 40 + 2 * 5 + 2 * 5 = 140
        // Height: 40 + 2 * 5 = 50
        assertEquals(140, preferredSize.width)
        assertEquals(50, preferredSize.height)
    }

    @Test
    fun `wrapping with single button per row when very narrow`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout)

        // Add 3 buttons
        repeat(3) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
            })
        }

        // Set panel width to fit only 1 button per row
        // 1 button * 40px + 2 * hgap (5px) = 50px
        panel.size = Dimension(50, 200)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Should create 3 rows of 1 button each
        // Width: 1 * 40 + 2 * 5 = 50
        // Height: 3 rows * 40 + 2 vgaps * 5 + 2 * vgap (5) = 120 + 10 + 10 = 140
        assertEquals(50, preferredSize.width)
        assertEquals(140, preferredSize.height)
    }

    @Test
    fun `preferred size respects panel insets`() {
        val layout = WrapLayout(FlowLayout.LEFT, 5, 5)
        val panel = JPanel(layout).apply {
            border = javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        }

        // Add 2 buttons
        repeat(2) {
            panel.add(JButton().apply {
                preferredSize = Dimension(40, 40)
            })
        }

        panel.size = Dimension(200, 100)

        val preferredSize = layout.preferredLayoutSize(panel)

        // Width: 2 * 40 + 1 * 5 (hgap) + 2 * 5 (layout hgap) + 15 + 15 (border) = 80 + 5 + 10 + 30 = 125
        // Height: 40 + 2 * 5 (layout vgap) + 10 + 10 (border) = 40 + 10 + 20 = 70
        assertTrue(preferredSize.width >= 120) // Account for border
        assertTrue(preferredSize.height >= 60) // Account for border
    }
}

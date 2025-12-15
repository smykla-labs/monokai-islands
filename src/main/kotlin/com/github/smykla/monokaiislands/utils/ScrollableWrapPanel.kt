package com.github.smykla.monokaiislands.utils

import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.HierarchyEvent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.Scrollable
import javax.swing.SwingConstants
import javax.swing.SwingUtilities

/**
 * A JPanel that implements Scrollable to work with WrapLayout inside JScrollPane or UI DSL.
 *
 * Key behavior:
 * - Listens to the TOP-LEVEL WINDOW for resize events (including shrinking)
 * - getPreferredSize/getMinimumSize return SMALL widths so parent can shrink us
 * - getScrollableTracksViewportWidth() returns true → panel width matches viewport
 */
@Suppress("MagicNumber")
class ScrollableWrapPanel : JPanel(), Scrollable {

    private var lastWindowWidth = 0
    private var windowListener: ComponentAdapter? = null

    init {
        // Listen for hierarchy changes to attach window listener
        addHierarchyListener { e ->
            if (e.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong() != 0L) {
                if (isShowing) {
                    attachWindowListener()
                }
            }
        }
    }

    private fun attachWindowListener() {
        // Find the top-level window (Settings dialog)
        val window = SwingUtilities.getWindowAncestor(this) ?: return

        // Remove an old listener if attached to the different window
        windowListener?.let { window.removeComponentListener(it) }

        // Listen to window resize events - this catches ALL resizing including shrinking
        windowListener = object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                val currentWidth = window.width
                if (currentWidth != lastWindowWidth) {
                    lastWindowWidth = currentWidth
                    // Force complete relayout of our panel
                    SwingUtilities.invokeLater {
                        invalidate()
                        revalidate()
                        repaint()
                    }
                }
            }
        }
        window.addComponentListener(windowListener)
    }

    /**
     * Return SMALL preferred width so the parent layout can shrink us.
     * Height is calculated by WrapLayout based on actual width.
     */
    override fun getPreferredSize(): Dimension {
        val layout = layout ?: return Dimension(10, 50)
        // Use viewport width when available so the preferred width can shrink with the dialog.
        val viewportWidth = (SwingUtilities.getAncestorOfClass(JScrollPane::class.java, this) as? JScrollPane)
            ?.viewport
            ?.width
            ?: 0

        val widthForLayout = when {
            viewportWidth > 0 -> viewportWidth
            width > 0 -> width
            else -> 10
        }

        val prefSize = layout.preferredLayoutSize(this)
        // Clamp width to the width we want the parent to respect.
        return Dimension(widthForLayout, prefSize.height)
    }

    /**
     * Return SMALL minimum size to allow shrinking.
     */
    override fun getMinimumSize(): Dimension {
        return Dimension(10, 10)
    }

    override fun getPreferredScrollableViewportSize(): Dimension {
        val layout = layout
        if (layout != null) {
            // Report a tiny width so the parent scrollpane knows we can shrink;
            // height still comes from the layout's preferred size.
            val pref = layout.preferredLayoutSize(this)
            return Dimension(10, pref.height)
        }
        return Dimension(10, 100)
    }

    override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int {
        return if (orientation == SwingConstants.VERTICAL) 20 else 10
    }

    override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int {
        return if (orientation == SwingConstants.VERTICAL) visibleRect.height else visibleRect.width
    }

    override fun getScrollableTracksViewportWidth(): Boolean = true

    override fun getScrollableTracksViewportHeight(): Boolean = false
}

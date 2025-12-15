package com.github.smykla.monokaiislands.utils

import java.awt.Container
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

/**
 * A FlowLayout subclass that properly wraps components to multiple rows.
 *
 * Based on Rob Camick's WrapLayout from https://tips4java.wordpress.com/2008/11/06/wrap-layout/
 *
 * The standard FlowLayout has a flaw: preferredLayoutSize() assumes all components fit in one row,
 * but layoutContainer() wraps them. This causes invisible components when the container
 * is constrained.
 *
 * WrapLayout synchronizes preferred size calculation with actual layout behavior.
 */
class WrapLayout(
    align: Int = LEFT,
    hgap: Int = 5,
    vgap: Int = 5
) : FlowLayout(align, hgap, vgap) {

    override fun preferredLayoutSize(target: Container): Dimension {
        return layoutSize(target, true)
    }

    override fun minimumLayoutSize(target: Container): Dimension {
        val minimum = layoutSize(target, false)
        minimum.width -= hgap + 1
        return minimum
    }

    private fun layoutSize(target: Container, preferred: Boolean): Dimension {
        return synchronized(target.treeLock) {
            val targetWidth = effectiveTargetWidth(target)
            val insets = target.insets
            val horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2)

            if (targetWidth == 0) {
                return@synchronized calculateSingleRowSize(target, preferred, insets)
            }

            val maxWidth = targetWidth - horizontalInsetsAndGap
            val dim = computeWrappedSize(target, preferred, maxWidth)

            dim.width += horizontalInsetsAndGap
            dim.height += insets.top + insets.bottom + vgap * 2

            clampWidthToViewport(target, dim)
        }
    }

    private fun effectiveTargetWidth(target: Container): Int {
        val viewportWidth = getScrollPaneWidth(target)
        val currentWidth = target.width

        if (viewportWidth <= 0) return currentWidth
        if (currentWidth == 0) return viewportWidth

        val narrowed = minOf(currentWidth, viewportWidth)
        return if (narrowed == 0) viewportWidth else narrowed
    }

    private fun computeWrappedSize(target: Container, preferred: Boolean, maxWidth: Int): Dimension {
        val dim = Dimension(0, 0)
        var rowWidth = 0
        var rowHeight = 0

        for (i in 0 until target.componentCount) {
            val comp = target.getComponent(i)
            if (!comp.isVisible) continue

            val size = if (preferred) comp.preferredSize else comp.minimumSize
            val willOverflow = rowWidth + size.width > maxWidth && rowWidth > 0

            if (willOverflow) {
                addRow(dim, rowWidth, rowHeight)
                rowWidth = 0
                rowHeight = 0
            }

            if (rowWidth != 0) {
                rowWidth += hgap
            }
            rowWidth += size.width
            rowHeight = maxOf(rowHeight, size.height)
        }

        addRow(dim, rowWidth, rowHeight)
        return dim
    }

    private fun clampWidthToViewport(target: Container, dim: Dimension): Dimension {
        val scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane::class.java, target) as? JScrollPane
        val viewportWidth = scrollPane?.viewport?.width ?: 0
        if (scrollPane != null && target.isValid && viewportWidth > 0) {
            dim.width = minOf(dim.width, viewportWidth)
        }
        return dim
    }

    private fun calculateSingleRowSize(target: Container, preferred: Boolean, insets: java.awt.Insets): Dimension {
        val dim = Dimension(0, 0)
        var maxHeight = 0

        for (i in 0 until target.componentCount) {
            val m = target.getComponent(i)
            if (m.isVisible) {
                val d = if (preferred) m.preferredSize else m.minimumSize
                if (dim.width > 0) {
                    dim.width += hgap
                }
                dim.width += d.width
                maxHeight = maxOf(maxHeight, d.height)
            }
        }

        dim.width += insets.left + insets.right + (hgap * 2)
        dim.height = maxHeight + insets.top + insets.bottom + vgap * 2

        return dim
    }

    private fun addRow(dim: Dimension, rowWidth: Int, rowHeight: Int) {
        dim.width = maxOf(dim.width, rowWidth)
        if (dim.height > 0) {
            dim.height += vgap
        }
        dim.height += rowHeight
    }

    private fun getScrollPaneWidth(target: Container): Int {
        val scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane::class.java, target) as? JScrollPane
        return scrollPane?.viewport?.width ?: 0
    }
}

/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(TM). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.limegroup.gnutella.gui.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.TableUI;
import javax.swing.table.DefaultTableCellRenderer;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceRenderer;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker.StateContributionInfo;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI.TableCellId;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceStripingUtils;
import org.pushingpixels.substance.internal.utils.UpdateOptimizationInfo;
import org.pushingpixels.substance.internal.utils.border.SubstanceTableCellBorder;

import com.limegroup.gnutella.gui.themes.SkinTableCellRenderer;

/**
 * 
 * @author gubatron
 * @author aldenml
 * 
 */
public final class SearchResultNameRenderer extends DefaultTableCellRenderer implements SkinTableCellRenderer{

    private static final long serialVersionUID = -1624943333769190212L;

    private JLabel labelMore;
    private JLabel labelText;

    public SearchResultNameRenderer() {
        setupUI();
    }

    private void setupUI() {
        //setLayout(new BorderLayout());
        putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);

        //        labelMore = new JLabel(SubstanceIconFactory.getTreeIcon(null, true));
        //        add(labelMore, BorderLayout.LINE_START);
        //
        //        labelText = new JLabel();
        //        add(labelText, BorderLayout.LINE_START);
    }

    private void setValue(SearchResultNameHolder value, JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        labelText.setText(fixText(value.getName()));

    }

    private String fixText(String text) {
        if (text != null) {
            text = text.replace("<html>", "<html><div width=\"1000000px\">");
            text = text.replace("</html>", "</div></html>");
        }
        return text;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (!SubstanceLookAndFeel.isCurrentLookAndFeel())
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TableUI tableUI = table.getUI();
        SubstanceTableUI ui = (SubstanceTableUI) tableUI;

        // Recompute the focus indication to prevent flicker - JTable
        // registers a listener on selection changes and repaints the
        // relevant cell before our listener (in TableUI) gets the
        // chance to start the fade sequence. The result is that the
        // first frame uses full opacity, and the next frame starts the
        // fade sequence. So, we use the UI delegate to compute the
        // focus indication.
        hasFocus = ui.isFocusedCell(row, column);

        TableCellId cellId = new TableCellId(row, column);

        StateTransitionTracker.ModelStateInfo modelStateInfo = ui.getModelStateInfo(cellId);
        ComponentState currState = ui.getCellState(cellId);
        // special case for drop location
        JTable.DropLocation dropLocation = table.getDropLocation();
        boolean isDropLocation = (dropLocation != null && !dropLocation.isInsertRow() && !dropLocation.isInsertColumn() && dropLocation.getRow() == row && dropLocation.getColumn() == column);

        if (!isDropLocation && (modelStateInfo != null)) {
            if (ui.hasRolloverAnimations() || ui.hasSelectionAnimations()) {
                Map<ComponentState, StateContributionInfo> activeStates = modelStateInfo.getStateContributionMap();
                SubstanceColorScheme colorScheme = getColorSchemeForState(table, ui, currState);
                if (currState.isDisabled() || (activeStates == null) || (activeStates.size() == 1)) {
                    super.setForeground(new ColorUIResource(colorScheme.getForegroundColor()));
                } else {
                    float aggrRed = 0;
                    float aggrGreen = 0;
                    float aggrBlue = 0;
                    for (Map.Entry<ComponentState, StateTransitionTracker.StateContributionInfo> activeEntry : modelStateInfo.getStateContributionMap().entrySet()) {
                        ComponentState activeState = activeEntry.getKey();
                        SubstanceColorScheme scheme = getColorSchemeForState(table, ui, activeState);
                        Color schemeFg = scheme.getForegroundColor();
                        float contribution = activeEntry.getValue().getContribution();
                        aggrRed += schemeFg.getRed() * contribution;
                        aggrGreen += schemeFg.getGreen() * contribution;
                        aggrBlue += schemeFg.getBlue() * contribution;
                    }
                    super.setForeground(new ColorUIResource(new Color((int) aggrRed, (int) aggrGreen, (int) aggrBlue)));
                }
            } else {
                SubstanceColorScheme scheme = getColorSchemeForState(table, ui, currState);
                super.setForeground(new ColorUIResource(scheme.getForegroundColor()));
            }
        } else {
            SubstanceColorScheme scheme = getColorSchemeForState(table, ui, currState);
            if (isDropLocation) {
                scheme = SubstanceColorSchemeUtilities.getColorScheme(table, ColorSchemeAssociationKind.TEXT_HIGHLIGHT, currState);
            }
            super.setForeground(new ColorUIResource(scheme.getForegroundColor()));
        }

        SubstanceStripingUtils.applyStripedBackground(table, row, this);

        this.setFont(table.getFont());

        TableCellId cellFocusId = new TableCellId(row, column);

        StateTransitionTracker focusStateTransitionTracker = ui.getStateTransitionTracker(cellFocusId);

        Insets regInsets = ui.getCellRendererInsets();
        if (hasFocus || (focusStateTransitionTracker != null)) {
            SubstanceTableCellBorder border = new SubstanceTableCellBorder(regInsets, ui, cellFocusId);

            // System.out.println("[" + row + ":" + column + "] hasFocus : "
            // + hasFocus + ", focusState : " + focusState);
            if (focusStateTransitionTracker != null) {
                border.setAlpha(focusStateTransitionTracker.getFocusStrength(hasFocus));
            }

            // special case for tables with no grids
            if (!table.getShowHorizontalLines() && !table.getShowVerticalLines()) {
                this.setBorder(new CompoundBorder(new EmptyBorder(table.getRowMargin() / 2, 0, table.getRowMargin() / 2, 0), border));
            } else {
                this.setBorder(border);
            }
        } else {
            this.setBorder(new EmptyBorder(regInsets.top, regInsets.left, regInsets.bottom, regInsets.right));
        }

        this.setValue(value);
        this.setOpaque(false);
        this.setEnabled(table.isEnabled());
        return this;
    }

    private SubstanceColorScheme getColorSchemeForState(JTable table, SubstanceTableUI ui, ComponentState state) {
        UpdateOptimizationInfo updateOptimizationInfo = ui.getUpdateOptimizationInfo();
        if (state == ComponentState.ENABLED) {
            if (updateOptimizationInfo == null) {
                return SubstanceColorSchemeUtilities.getColorScheme(table, state);
            } else {
                return updateOptimizationInfo.getDefaultScheme();
            }
        } else {
            if (updateOptimizationInfo == null) {
                return SubstanceColorSchemeUtilities.getColorScheme(table, ColorSchemeAssociationKind.HIGHLIGHT, state);
            } else {
                return updateOptimizationInfo.getHighlightColorScheme(state);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public final void paint(Graphics g) {
        super.paint(g);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    protected final void paintBorder(Graphics g) {
        super.paintBorder(g);
    }

    @Override
    public final void paintComponents(Graphics g) {
    }
}
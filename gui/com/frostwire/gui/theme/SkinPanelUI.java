/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(R). All rights reserved.
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

package com.frostwire.gui.theme;

import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.synth.SynthPanelUI;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class SkinPanelUI extends SynthPanelUI {

    public static ComponentUI createUI(JComponent comp) {
        ThemeMediator.testComponentCreationThreadingViolation();
        return new SkinPanelUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        UIDefaults defaults = new UIDefaults();
        defaults.put("Panel.background", SkinColors.LIGHT_BACKGROUND_COLOR);

        c.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
        c.putClientProperty("Nimbus.Overrides", defaults);
    }
}

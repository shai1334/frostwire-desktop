/*
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

package com.limegroup.gnutella.settings;

import org.limewire.setting.BooleanSetting;
import org.limewire.setting.IntSetting;

/**
 * Bittorrent settings
 */
public class BittorrentSettings extends LimeProps {

    private BittorrentSettings() {
    }

    /** maximum simultaneous torrent downloads */
    public static IntSetting TORRENT_MAX_ACTIVE_DOWNLOADS = FACTORY.createIntSetting("TORRENT_MAX_ACTIVE_DOWNLOADS", 10);

    /**
     * Records what was the last sorting order of the sort column for the transfer manager.
     * false -> Descending
     * true -> Ascending
     */
    public static BooleanSetting BTMEDIATOR_COLUMN_SORT_ORDER = FACTORY.createBooleanSetting("BTMEDIATOR_COLUMN_SORT_ORDER", true);

    /**
     * Records what was the last column you used to sort the transfers table.
     */
    public static IntSetting BTMEDIATOR_COLUMN_SORT_INDEX = FACTORY.createIntSetting("BTMEDIATOR_COLUMN_SORT_INDEX", -1);
}

/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/
package eu.engys.util.filechooser.gui;

import eu.engys.util.connection.SshParameters;
import eu.engys.util.filechooser.AbstractFileChooser;
import eu.engys.util.filechooser.util.HelyxFileFilter;

public class BrowserFactory {
    
    public static FileChooserPanel createOpenBrowser(AbstractFileChooser chooser) {
        return new FileChooserPanel(chooser, null, null, null, false);
    }

    public static FileChooserPanel createOpenBrowser(AbstractFileChooser chooser, HelyxFileFilter... filters) {
        return new FileChooserPanel(chooser, null, null, null, false, filters);
    }

    public static FileChooserPanel createOpenBrowser(AbstractFileChooser chooser, Accessory accessory) {
        return new FileChooserPanel(chooser, accessory, null, null, false);
    }

    public static FileChooserPanel createOpenBrowser(AbstractFileChooser chooser, Options options) {
        return new FileChooserPanel(chooser, null, options, null, false);
    }

    public static FileChooserPanel createOpenBrowser(AbstractFileChooser chooser, Accessory accessory, HelyxFileFilter... filters) {
        return new FileChooserPanel(chooser, accessory, null, null, false, filters);
    }

    public static FileChooserPanel createSaveAsBrowser(AbstractFileChooser chooser) {
        return new FileChooserPanel(chooser, null, null, null, true);
    }

    public static FileChooserPanel createSaveAsBrowser(AbstractFileChooser chooser, HelyxFileFilter... filters) {
        return new FileChooserPanel(chooser, null, null, null, true, filters);
    }

    public static FileChooserPanel createOpenRemoteBrowser(AbstractFileChooser chooser, SshParameters sshParameters) {
        return new FileChooserPanel(chooser, null, null, sshParameters, false);
    }
}

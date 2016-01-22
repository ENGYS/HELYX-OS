/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

package eu.engys.gui.mesh.actions.geometry;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.geometry.Surface;
import eu.engys.gui.mesh.panels.AbstractGeometryPanel;
import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;

public class PasteSurfaceAction extends AbstractAction {

    public static final String PASTE = "Paste";
    private Surface[] surfaces;
    private AbstractGeometryPanel panel;

    public PasteSurfaceAction(AbstractGeometryPanel panel) {
        super(PASTE);
        this.panel = panel;
    }

    public void update(boolean enabled, Surface[] surfaces) {
        this.surfaces = surfaces;
        // Type type = surfaces[0].getType();
        setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.getTreeNodeManager().getTree().clearSelection();
        new PasteSurface(surfaces).execute();
    }

    private class PasteSurface {
        private Surface[] surfaces;

        public PasteSurface(Surface[] surfaces) {
            this.surfaces = surfaces;
        }

        public void execute() {
            if (Util.isVarArgsNotNull(surfaces)) {
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
                try {
                    String dictionaryString = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    Dictionary dictionary = DictionaryUtils.readDictionary(dictionaryString).getDictionaries().get(0);
                    if (dictionary.isDictionary("surface") && dictionary.isDictionary("layer")) {
                        for (Surface surface : surfaces) {
                            surface.fromDictionary(dictionary);
                        }
                    } else {
                        JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Cannot Paste: Invalid Format", "Copy/Paste Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Cannot Paste: An Error Occurred", "Copy/Paste Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Cannot Paste: Empty Selection", "Copy/Paste Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

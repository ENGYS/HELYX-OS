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

package eu.engys.gui.mesh.panels;

import static eu.engys.gui.mesh.actions.geometry.ExtractLineAction.EXTRACT_NAME;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.lowagie.text.Font;

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.gui.mesh.actions.geometry.CloneSurfaceAction;
import eu.engys.gui.mesh.actions.geometry.CopySurfaceAction;
import eu.engys.gui.mesh.actions.geometry.ExtractLineAction;
import eu.engys.gui.mesh.actions.geometry.PasteSurfaceAction;
import eu.engys.gui.mesh.actions.geometry.RemoveSurfaceAction;
import eu.engys.gui.tree.TreeNodeManager.PopUpBuilder;
import eu.engys.util.Util;

public class DefaultGeometryActions implements PopUpBuilder {

    public static final String GENERAL = "General";
    public static final String LINES = "Lines";
    public static final String SURFACES = "Surfaces";

    protected final Model model;
    private final AbstractGeometryPanel panel;

    private final RemoveSurfaceAction remove;
    private final CloneSurfaceAction clone;
    private final CopySurfaceAction copy;
    private final PasteSurfaceAction paste;

    private final ExtractLineAction extractLines;

    private boolean enabled = true;

    public DefaultGeometryActions(AbstractGeometryPanel panel, Controller controller) {
        this.panel = panel;
        this.model = panel.getModel();

        this.remove = new RemoveSurfaceAction(model);
        this.clone = new CloneSurfaceAction(model, panel);
        this.copy = new CopySurfaceAction(panel);
        this.paste = new PasteSurfaceAction(panel);

        // Lines
        this.extractLines = new ExtractLineAction(model, controller, this);
    }

    @Override
    public void populate(JPopupMenu popUp) {
        populateGeneralActions(popUp);
        populateSurfaceActions(popUp);
        populateLinesActions(popUp);
    }

    private void populateGeneralActions(JPopupMenu popUp) {
        popUp.add(new TitledSeparator(GENERAL));
        popUp.add(remove).setName(RemoveSurfaceAction.REMOVE);
        popUp.add(clone).setName(CloneSurfaceAction.CLONE);
        popUp.add(copy).setName(CopySurfaceAction.COPY);
        popUp.add(paste).setName(PasteSurfaceAction.PASTE);
    }

    protected void populateSurfaceActions(JPopupMenu popUp) {
    }

    protected void populateLinesActions(JPopupMenu popUp) {
        popUp.add(new TitledSeparator(LINES));
        popUp.add(extractLines).setName(EXTRACT_NAME);
    }

    protected void updateActions(Surface[] surfaces) {
        if (Util.isVarArgsNotNull(surfaces)) {
            remove.update(isEnabled(), surfaces);
            clone.update(isEnabled(), surfaces);
            copy.update(isEnabled(), surfaces);
            paste.update(isEnabled(), surfaces);

            extractLines.update(isEnabled(), surfaces);
        }
    }

    public static class TitledSeparator extends JPanel {
        public TitledSeparator(String title) {
            super(new GridBagLayout());
            setOpaque(false);
            JLabel label = new JLabel(title);
            label.setOpaque(false);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            add(new JSeparator(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            add(label, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            add(new JSeparator(), new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static class Enable implements Runnable {
        private DefaultGeometryActions actions;

        public Enable(DefaultGeometryActions actions) {
            this.actions = actions;
        }

        @Override
        public void run() {
            actions.setEnabled(true);
        }
    }

    public static class Disable implements Runnable {
        private DefaultGeometryActions actions;

        public Disable(DefaultGeometryActions actions) {
            this.actions = actions;
        }

        @Override
        public void run() {
            actions.setEnabled(false);
        }
    }
}

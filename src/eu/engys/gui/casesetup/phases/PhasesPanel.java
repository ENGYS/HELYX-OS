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
package eu.engys.gui.casesetup.phases;

import javax.swing.JComponent;

import eu.engys.core.project.Model;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.builder.PanelBuilder;

public class PhasesPanel extends DefaultGUIPanel {

    public static final String PHASES = "Phases";
    
    private PhasesView phasesView;

    public PhasesPanel(Model model, PhasesView phasesView) {
        super(PHASES, model);
        this.phasesView = phasesView;
    }

    @Override
    public String getKey() {
        return PHASES + "_" + phasesView.getClass().getCanonicalName();
    }

    @Override
    public void load() {
        phasesView.load(model);
    }

    @Override
    public void save() {
        phasesView.save(model);
    }

    @Override
    public void stateChanged() {
        rebuildPanel();
    }

    @Override
    public void materialsChanged() {
        rebuildPanel();
    }

    private void rebuildPanel() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeAll();
                layoutPanel();
                load();
            }
        });
    }

    @Override
    protected JComponent layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        phasesView.layoutComponents(builder);
        return builder.removeMargins().getPanel();
    }

    @Override
    public int getIndex() {
        return 2;
    }
}

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

package eu.engys.gui.solver.postprocessing.panels.residuals;

import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import eu.engys.core.controller.Controller;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.gui.solver.postprocessing.panels.actions.ExportToCSVAction;
import eu.engys.gui.solver.postprocessing.panels.actions.ExportToExcelAction;
import eu.engys.gui.solver.postprocessing.panels.actions.ExportToPNGAction;
import eu.engys.gui.solver.postprocessing.panels.actions.ShowCrosshairForResidualsAction;
import eu.engys.gui.solver.postprocessing.panels.actions.ShowLogFileAction;
import eu.engys.gui.solver.postprocessing.panels.utils.SelectedViewProvider;
import eu.engys.util.ui.UiUtil;

public class ResidualsPanel extends DefaultGUIPanel implements SelectedViewProvider {

    private static final String TITLE = "Residuals";
    private ResidualsView residualsView;
    private JToggleButton showCrosshairButton;

    @Inject
    public ResidualsPanel(Model model, Controller controller) throws Exception {
        super(TITLE, model);
        this.residualsView = (ResidualsView) controller.getResidualView();
    }

    @Override
    protected JComponent layoutComponents() {
        populateToolbar();
        return residualsView.getPanel();
    }

    @Override
    public void stop() {
        super.stop();
        stopCrosshair();
    }

    private void stopCrosshair() {
        if (showCrosshairButton.isSelected()) {
            showCrosshairButton.doClick();
        }
    }

    private void populateToolbar() {
        titleToolbar.add(showCrosshairButton = UiUtil.createToolBarToggleButton(new ShowCrosshairForResidualsAction(residualsView), true));
        titleToolbar.addSeparator();
        titleToolbar.add(UiUtil.createToolBarButton(new ShowLogFileAction(this)));
        titleToolbar.add(UiUtil.createToolBarButton(new ExportToExcelAction(this)));
        titleToolbar.add(UiUtil.createToolBarButton(new ExportToCSVAction(this)));
        titleToolbar.add(UiUtil.createToolBarButton(new ExportToPNGAction(this)));

    }

    @Override
    public ParserView getSelectedView() {
        return residualsView;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void load() {
        residualsView.reset();
    }

}

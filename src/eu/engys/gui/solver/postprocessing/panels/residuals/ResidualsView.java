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
package eu.engys.gui.solver.postprocessing.panels.residuals;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JTabbedPane;

import eu.engys.core.modules.AbstractChart;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.residuals.ResidualsViewConfigurator;
import eu.engys.core.modules.residuals.ResidualsViewManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.report.Exporter;
import eu.engys.gui.solver.postprocessing.panels.AbstractParserView;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsParser;
import eu.engys.util.progress.ProgressMonitor;

public class ResidualsView extends AbstractParserView {

    private ResidualsViewManager manager;
    private Set<ApplicationModule> modules;

    public ResidualsView(Model model, Set<ApplicationModule> modules, ProgressMonitor monitor) {
        super(model, null, monitor);
        setName(getKey());
        this.modules = modules;
        ResidualsViewConfigurator simpleResiduals = new SimpleResidualsViewConfigurator(model, tabbedPane, monitor);
        this.manager = new ResidualsViewManager(model, tabbedPane, simpleResiduals);
    }

    public void updateConfiguration() {
        manager.updateConfiguration(modules);
        manager.getActiveConfigurator().reloadPanel();
    }

    @Override
    public AbstractChart getSelectedChart() {
        if(activeConfiguratorNotnull()) {
            return manager.getActiveConfigurator().getSelectedChart();
        }
        return null;
    }

    @Override
    public void handleStateChanged(State state) {
        if(activeConfiguratorNotnull()) {
            manager.getActiveConfigurator().handleStateChanged(state);
        }
    }

    @Override
    public List<Parser> getReportParsersList() {
        if(activeConfiguratorNotnull()) {
            return manager.getActiveConfigurator().getReportParsersList();
        }
        return Collections.emptyList();
    }

    @Override
    public Exporter getExporter() {
        if(activeConfiguratorNotnull()) {
            return manager.getActiveConfigurator().getExporter();
        }
        return null;
    }

    @Override
    public String getKey() {
        return ResidualsParser.KEY;
    }

    @Override
    public void clearData() {
        if(activeConfiguratorNotnull()) {
            manager.getActiveConfigurator().clearData();
        }
    }

    @Override
    public void handleFunctionObjectChanged() {
    }

    @Override
    public void updateParsing(List<TimeBlocks> newTimeBlocks) {
        if (!newTimeBlocks.isEmpty()) {
        	if(activeConfiguratorNotnull()){
        		manager.getActiveConfigurator().updateParsing(newTimeBlocks);
        	}
        }
    }

    private boolean activeConfiguratorNotnull() {
        return manager.getActiveConfigurator() != null;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    // For test purposes only
    public ResidualsViewManager getManager() {
        return manager;
    }

	@Override
	public void deleteLogFiles() {
	}

}

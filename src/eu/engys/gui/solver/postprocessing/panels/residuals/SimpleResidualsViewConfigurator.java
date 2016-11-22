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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import eu.engys.core.modules.AbstractChart;
import eu.engys.core.modules.residuals.ResidualsViewConfigurator;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.report.Exporter;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsParser;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsUtils;
import eu.engys.gui.solver.postprocessing.parsers.SimpleResidualsParser;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public class SimpleResidualsViewConfigurator implements ResidualsViewConfigurator {

    private Model model;
    private AbstractChart chart;
    private ProgressMonitor monitor;

    private JTabbedPane tabbedPane;

    public SimpleResidualsViewConfigurator(Model model, JTabbedPane tabbedPane, ProgressMonitor monitor) {
        this.model = model;
        this.tabbedPane = tabbedPane;
        this.monitor = monitor;
        this.chart = new ResidualsChart();
        chart.layoutComponents();
    }

    @Override
    public AbstractChart getSelectedChart() {
        return chart;
    }

    @Override
    public void handleStateChanged(State state) {
        chart.handleStateChanged(state);
    }

    @Override
    public void reloadPanel() {
        tabbedPane.removeAll();
        tabbedPane.addTab(ResidualsPanel.RESIDUALS, chart.getPanel());
        UiUtil.setOneTabHide(tabbedPane);
    }

    @Override
    public String getKey() {
        return ResidualsParser.KEY;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public List<Parser> getReportParsersList() {
        List<Parser> reportParsersList = new ArrayList<>();
        reportParsersList.add(new SimpleResidualsParser(ResidualsUtils.fileToParse(model)));
        return reportParsersList;
    }

    @Override
    public void clearData() {
        chart.clear();
    }

    @Override
    public void updateParsing(List<TimeBlocks> newTimeBlocks) {
        chart.addToDataSet(newTimeBlocks.get(0));
    }

    @Override
    public Exporter getExporter() {
        return new ResidualsExporter(getReportParsersList(), monitor);
    }

}

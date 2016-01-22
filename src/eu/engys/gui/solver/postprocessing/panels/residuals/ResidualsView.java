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

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.project.Model;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.TimeBlocks;
import eu.engys.core.report.Exporter;
import eu.engys.gui.solver.postprocessing.panels.AbstractParserView;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsParser;
import eu.engys.gui.solver.postprocessing.parsers.ResidualsUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public class ResidualsView extends AbstractParserView {

    private ResidualsChartPanel chartPanel;

    // public static void main(String[] args) {
    // new HelyxLookAndFeel().init();
    // Model model = new Model();
    // model.init();
    // ResidualsView panel = new ResidualsView(model, null);
    // JFrame f = UiUtil.defaultTestFrame("a", panel);
    // f.setSize(600, 500);
    // f.setVisible(true);
    // }

    public ResidualsView(Model model, ProgressMonitor monitor) {
        super(model, null, monitor);
        this.chartPanel = new ResidualsChartPanel();
        chartPanel.layoutComponents();

        tabbedPane.addTab("Residuals", chartPanel);
        UiUtil.setOneTabHide(tabbedPane);
    }

    @Override
    public List<Parser> gerReportParsersList() {
        List<Parser> reportParsersList = new ArrayList<>();
        reportParsersList.add(new ResidualsParser(ResidualsUtils.fileToParse(model)));
        return reportParsersList;
    }

    @Override
    public Exporter getExporter() {
        return new ResidualsExporter(gerReportParsersList(), monitor);
    }

    @Override
    public String getKey() {
        return ResidualsParser.KEY;
    }

    @Override
    public void clearData() {
        chartPanel.clearData();
    }

    @Override
    public void handleFunctionObjectChanged() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void updateParsing(List<TimeBlocks> newTimeBlocks) {
        if (!newTimeBlocks.isEmpty()) {
            chartPanel.addToDataSet(newTimeBlocks.get(0));
        }
    }

}

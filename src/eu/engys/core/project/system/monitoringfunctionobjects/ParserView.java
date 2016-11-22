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
package eu.engys.core.project.system.monitoringfunctionobjects;

import java.util.List;

import javax.swing.JComponent;

import eu.engys.core.project.state.State;
import eu.engys.core.report.Exporter;

public interface ParserView {

    public void reset();
    public void showLoading();
    public void stopLoading();
    public void handleSolverStarted();
    void clearData();

    public void handleStateChanged(State state);
    public void handleFunctionObjectChanged();

    public JComponent getPanel();
    
    public boolean isParsingEnabled();
    
    public void setParsingEnabled(boolean parsingEnabled);

    void updateParsing(List<TimeBlocks> newTimeBlocks);

    public String getKey();

    public void stop();

    void showLogFile();

    void deleteLogFiles();
    
    void exportToExcel();

    void exportToCSV();

    void exportToPNG();

    List<Parser> getReportParsersList();

    Exporter getExporter();

}

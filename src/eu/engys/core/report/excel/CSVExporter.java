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

package eu.engys.core.report.excel;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import eu.engys.core.executor.FileManagerSupport;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.util.progress.ProgressMonitor;

public abstract class CSVExporter {

    protected List<Parser> parsers;
    protected ProgressMonitor monitor;

    public CSVExporter(List<Parser> parsers, ProgressMonitor monitor) {
        this.parsers = parsers;
        this.monitor = monitor;
    }

    public CSVExporter(Parser parser) {
        List<Parser> parsers = new ArrayList<>();
        parsers.add(parser);
        this.parsers = parsers;
    }

    public void create(File reportFile) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(reportFile), ',');
        populate(writer);
        end(writer);
    }

    protected abstract void populate(CSVWriter writer) throws Exception;

    private void end(CSVWriter writer) throws Exception {
        writer.close();
    }

    public void show(File reportFile) {
        FileManagerSupport.open(reportFile);
    }

}

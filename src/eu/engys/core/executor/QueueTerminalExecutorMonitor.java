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

package eu.engys.core.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

public class QueueTerminalExecutorMonitor extends TerminalExecutorMonitor {

    private File[] outputFiles;
    private List<Tailer> tailers;

    public QueueTerminalExecutorMonitor(File... outputFile) {
        this.outputFiles = outputFile;
        this.tailers = new ArrayList<>();
    }

    @Override
    public void start() {
        for (File outputFile : outputFiles) {
            tailers.add(Tailer.create(outputFile, new MyListener(), 500L));
        }
        super.start();
    }

    @Override
    public void finish(int returnValue) {
        super.finish(returnValue);
        for (Tailer tailer : tailers) {
            tailer.stop();
        }
    }

    @Override
    public void error(int returnValue, String msg) {
        super.error(returnValue, msg);
        for (Tailer tailer : tailers) {
            tailer.stop();
        }
        // System.err.println(msg);
    }

    @Override
    public void refresh() {
        super.refresh();
        // System.out.print(flushLinesBuffer());
    }

    class MyListener extends TailerListenerAdapter {

        @Override
        public void handle(String line) {
            area.append("[out] " + line + "\n");
        }

        @Override
        public void handle(Exception exception) {
            area.append("[err] " + exception.getMessage() + "\n");
        }

    }
}

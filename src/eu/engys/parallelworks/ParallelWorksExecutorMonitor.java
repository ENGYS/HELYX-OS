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
package eu.engys.parallelworks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import eu.engys.core.executor.TerminalExecutorMonitor;
import eu.engys.core.executor.TerminalManager;

public class ParallelWorksExecutorMonitor extends TerminalExecutorMonitor {

    private File logFile;
    private List<Tailer> tailers;

    public ParallelWorksExecutorMonitor(TerminalManager terminalManager, File logFile) {
        super(terminalManager, logFile);
        this.logFile = logFile;
        this.tailers = new ArrayList<>();
    }

    @Override
    public void start() {
        tailers.add(Tailer.create(logFile, new MyListener(), 100L));
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
    }

    class MyListener extends TailerListenerAdapter {

        @Override
        public void handle(String line) {
            _refresh(line + "\n", "");
        }

        @Override
        public void handle(Exception exception) {
            _refresh("", exception.getMessage() + "\n");
        }

    }
}

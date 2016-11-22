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

package eu.engys.core.project.geometry.stl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eu.engys.util.progress.ProgressMonitor;

public class STLJoiner implements Runnable {

    private final ProgressMonitor monitor;

    private BufferedWriter flatWriter = null;

    private int lineCounter = 0;

    private File destination;
    private List<File> children;

    public STLJoiner(File destination, List<File> children, ProgressMonitor monitor) {
        this.destination = destination;
        this.children = children;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            joinFiles();
        } catch (Exception e) {
            monitor.error(e.getMessage());
        } finally {
        }
    }

    public void joinFiles() throws Exception {
        monitor.setIndeterminate(true);
        String line = null;
        try {
            flatWriter = new BufferedWriter(new FileWriter(destination));
            for (File child : children) {
                monitor.info("Copying " + child.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(child), 2000);) {
                    if (reader.ready()) {
                        while ((line = reader.readLine()) != null && !line.isEmpty()) {
                            writeln(line);
                            increaseCounter();
                        }
                    }
                } catch (Exception e) {
                    
                }
            }
        } finally {
            if (flatWriter != null)
                flatWriter.close();
        }
    }

    private void writeln(String string) throws IOException {
        if (flatWriter != null)
            flatWriter.write(string + "\n");
    }


    protected void increaseCounter() {
        lineCounter++;
        if (lineCounter % 30000 == 0)
            monitor.setCurrent(null, lineCounter);
    }

    public int count(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
            }
            return count;
        } finally {
            is.close();
        }
    }
}

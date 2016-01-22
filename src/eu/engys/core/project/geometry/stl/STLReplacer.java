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


package eu.engys.core.project.geometry.stl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;

public class STLReplacer implements Runnable {

    private final ProgressMonitor monitor;

    private BufferedReader reader = null;
    private BufferedWriter flatWriter = null;

    private int lineCounter = 0;

    private File source;
    private String solidName;
    private File replacement;

    public STLReplacer(File source, String name, File replacement, ProgressMonitor monitor) {
        this.source = source;
        this.solidName = name;
        this.replacement = replacement;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            replaceFile();
        } catch (Exception e) {
            monitor.error(e.getMessage());
        } finally {
        }
    }

    public void replaceFile() throws Exception {
        String sourceName = source.getName();
        File source_org = new File(source.getParent(), sourceName + ".org");
        source.renameTo(source_org);
        File replacedFile = new File(source.getParent(), sourceName);
        monitor.info(source.getAbsolutePath() + " -> " + source_org.getAbsolutePath());

        int totalLines = count(source_org);
        monitor.setTotal(totalLines);
        monitor.setIndeterminate(false);
        monitor.info("Reading " + source.getAbsolutePath());

        String line = null;
        try {
            reader = new BufferedReader(new FileReader(source_org), 2000);
            flatWriter = new BufferedWriter(new FileWriter(replacedFile));

            // writeln("solid "+flattenedFile.getName());
            if (reader.ready()) {
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    exceptionIfLineIsntSolid(lineCounter, line);
                    parseLine(line);
                    increaseCounter();
                }
            }
            // write("endsolid "+flattenedFile.getName());
        } finally {
            if (reader != null)
                reader.close();
            if (flatWriter != null)
                flatWriter.close();
        }
    }

    private String regionName = "";

    private boolean replacing;

    protected void parseLine(String line) throws IOException {
        if (line.startsWith("solid " + solidName)) {
            replacing = true;
            writeReplacement();
            return;
        } else if (line.startsWith("endsolid") && replacing) {
            replacing = false;
        } else if (!replacing) {
            // System.out.println("STLReplacer.parseLine() COPY: "+line);
            writeln(line);
        }
    }

    private void writeReplacement() throws FileNotFoundException, IOException {
        String line = null;
        int lineCounter = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(replacement), 2000)) {
            writeln("solid " + solidName);
            if (reader.ready()) {
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    //exceptionIfLineIsntSolid(lineCounter, line);
                    // System.out.println("STLReplacer.parseLine() REPLACE: "+line);
                    if (line.startsWith("solid") || line.startsWith("endsolid"))
                        continue;
                    writeln(line);
                    lineCounter++;
                }
            }
            write("endsolid " + solidName);
        }
    }

    private void writeln(String string) throws IOException {
        if (flatWriter != null)
            flatWriter.write(string + "\n");
    }

    private void write(String string) throws IOException {
        if (flatWriter != null)
            flatWriter.write(string);
    }

    protected void increaseCounter() {
        lineCounter++;
        if (lineCounter % 30000 == 0)
            monitor.setCurrent(null, lineCounter);
    }

    protected void exceptionIfLineIsntSolid(int counter, String line) {
        if (counter == 0 && !line.startsWith("solid"))
            throw new IllegalArgumentException("Binary STL format not supported");
    }

    private int counter = 0;

    public void setValidRegionName(String line) {
        int startIndex = line.indexOf(" ");
        if (startIndex < 0) {
            regionName = "solid" + counter++;
            monitor.info(" - " + "Found empty name. Set to " + regionName);
            return;
        }
        String name = line.substring(startIndex).trim();
        if (name.isEmpty()) {
            regionName = "solid" + counter++;
            monitor.info(" - " + "Found empty name. Set to " + regionName);
            return;
        }
        regionName = Util.replaceForbiddenCharacters(name);
        if (regionName.equals(name)) {
            monitor.info(" - " + regionName + " found");
        } else {
            monitor.info(String.format(" - " + "Found invalid name \"%s\". Set to \"%s\".", name, regionName));
        }
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

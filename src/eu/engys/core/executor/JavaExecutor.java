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
package eu.engys.core.executor;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.exec.CommandLine;

public class JavaExecutor extends AbstractScriptExecutor {

    private String className;
    private String[] args;

    public JavaExecutor(String className, String... args) {
        super();
        this.className = className;
        this.args = args;
    }

    @Override
    public CommandLine getCommandLine() {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String java = System.getProperty("java.home") + separator + "bin" + separator + "java";

        final CommandLine cmdLine = new CommandLine(java);
        cmdLine.addArgument("-classpath");
        cmdLine.addArgument(relativize(classpath));
        addSystemProperties(cmdLine);
        cmdLine.addArgument(className);
        cmdLine.addArguments(args);

        return cmdLine;
    }

    private String relativize(String classpath) {
        if (currentDir != null) {
            String relativizedClassPath = "";
            for (String path : classpath.split(System.getProperty("path.separator"))) {
                Path p = Paths.get(path);
                if (!p.isAbsolute()) {
                    p = p.toAbsolutePath();
                }

                Path relativize;
                if (canRelativize(currentDir.getAbsoluteFile().toPath(), p)) {
                    relativize = p;
                } else {
                    relativize = currentDir.getAbsoluteFile().toPath().relativize(p);
                }

                relativizedClassPath += (relativize + System.getProperty("path.separator"));
            }
            return relativizedClassPath;
        } else {
            return classpath;
        }
    }

    private boolean canRelativize(Path p1, Path p2) {
        // windows case where you have C:\ and D:\
        return p1.getRoot() != p2.getRoot();
    }

    private void addSystemProperties(CommandLine cmdLine) {
        if (properties != null) {
            for (String key : properties.stringPropertyNames()) {
                cmdLine.addArgument("-D" + key + "=" + properties.getProperty(key));
            }
        }
    }

    @Override
    protected void internalDeleteOnEnd() {

    }
}

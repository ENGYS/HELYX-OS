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
        return buildCommandLineScript();
    }

    private CommandLine buildCommandLineScript() {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String java = System.getProperty("java.home") + separator + "bin" + separator + "java";

        final CommandLine cmdLine = new CommandLine(java);
        cmdLine.addArgument("-classpath");
        cmdLine.addArgument(reletivize(classpath));
        addSystemProperties(cmdLine);
        cmdLine.addArgument(className);
        cmdLine.addArguments(args);

        return cmdLine;
    }

    private String reletivize(String classpath) {
        if (currentDir != null) {
            String relative = "";
            String[] paths = classpath.split(System.getProperty("path.separator"));
            for (String path : paths) {
                Path p = Paths.get(path);
                if(!p.isAbsolute()){
                    p = p.toAbsolutePath();
                }
                Path relativize = currentDir.getAbsoluteFile().toPath().relativize(p);
                relative += (relativize + System.getProperty("path.separator"));
            }
            return relative;
        } else {
            return classpath;
        }
    }

    private void addSystemProperties(CommandLine cmdLine) {
        if (properties != null) {
            for (String key : properties.stringPropertyNames()) {
                cmdLine.addArgument("-D"+key+"="+properties.getProperty(key));
            }
        }
    }

    @Override
    protected void internalDeleteOnEnd() {

    }
}

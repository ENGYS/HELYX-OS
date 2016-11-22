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

import static eu.engys.core.OpenFOAMEnvironment.cleanEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.loadEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.printHeader;
import static eu.engys.core.OpenFOAMEnvironment.printVariables;

import java.io.File;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;

import eu.engys.core.controller.ScriptBuilder;
import eu.engys.util.IOUtils;
import eu.engys.util.Util;

public class CommandExecutor extends AbstractScriptExecutor {

    private CommandLine commandLine;
    private File supportFile;

    public CommandExecutor(String command, String... arguments) {
        this.commandLine = new CommandLine(command);
        commandLine.addArguments(arguments);
    }

    @Override
    protected CommandLine getCommandLine() {
        this.supportFile = IOUtils.getSupportFile(currentDir);
        writeCommandInSupportFile(supportFile);
        return new CommandLine(supportFile);
    }

    @Override
    protected void internalDeleteOnEnd() {
        if(keepFileOnEnd){
            //do nothing
        } else {
            FileUtils.deleteQuietly(supportFile);
        }
    }

    private void writeCommandInSupportFile(File supportFile) {
        IOUtils.writeLinesToFile(supportFile, getCommand());
        supportFile.setExecutable(true);
    }

    private List<String> getCommand() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, description);
        printVariables(sb);
        if (Util.isWindows()) {
            if (loadOpenFoamEnv) {
                loadEnvironment(sb);
                sb.append("cd /D \"" + currentDir + "\"");
            } else {
                cleanEnvironment(sb);
            }
            sb.append(commandLine.toString());
        } else {
            if (loadOpenFoamEnv) {
                loadEnvironment(sb);
            } else {
                cleanEnvironment(sb);
            }
            sb.append(commandLine.toString());
        }
        return sb.getLines();
    }

}

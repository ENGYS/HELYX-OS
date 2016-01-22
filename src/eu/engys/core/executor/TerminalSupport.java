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

import static eu.engys.core.OpenFOAMEnvironment.getEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.getTestEnvironment;

import java.io.File;
import java.io.IOException;

import eu.engys.core.project.Model;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;

public class TerminalSupport {

    private static final String ACTION_NAME = "Open Terminal";

    public static void openTerminal(Model model) {
        if (Util.isWindows()) {
            Executor.command("start", "cmd", "/K").inFolder(model.getProject().getBaseDir()).withOpenFoamEnv().env(getEnvironment(model)).description(ACTION_NAME).exec();
        } else {
            Executor.command(getTerminal() + "$SHELL").inFolder(model.getProject().getBaseDir()).withOpenFoamEnv().env(getEnvironment(model)).description(ACTION_NAME).exec();
        }
    }

    public static void openTerminal(File baseDir) {
        if (Util.isWindows()) {
            Executor.command("start", "cmd", "/K").inFolder(baseDir).withOpenFoamEnv().env(getTestEnvironment()).description(ACTION_NAME).exec();
        } else {
            Executor.command(getTerminal() + "$SHELL").inFolder(baseDir).withOpenFoamEnv().env(getTestEnvironment()).description(ACTION_NAME).exec();
        }
    }

    private static String getTerminal() {
        String preferredTerminal = PrefUtil.getString(PrefUtil.HELYX_DEFAULT_TERMINAL);
        if (preferredTerminal.isEmpty()) {
            if (checkTerminal("gnome-terminal")) {
                return "gnome-terminal --disable-factory --geometry 80x40 -e ";
            } else if (checkTerminal("konsole")) {
                return "konsole --geometry 80x40 -e ";
            } else if (checkTerminal("xterm")) {
                return "xterm -sb -font -*-fixed-medium-r-*-*-18-*-*-*-*-*-iso8859-* -geometry 80x40 -hold -e ";
            } else {
                System.err.println("No terminal found");
            }
        } else {
            return preferredTerminal + " -e ";
        }

        return "";
    }

    private static boolean checkTerminal(String terminal) {
        try {
            new ProcessBuilder(terminal, "--help").start().waitFor();
            return true;
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }
}

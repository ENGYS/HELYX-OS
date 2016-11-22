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
package eu.engys.core;

import static eu.engys.util.OpenFOAMCommands.PARA_FOAM;
import static eu.engys.util.PrefUtil.DOCKER_IMAGE;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;

import eu.engys.util.PrefUtil;
import eu.engys.util.Util;

public class DockerUtil {

    private static final String USER = PrefUtil.USER_NAME;
    private static final String HOME = PrefUtil.USER_HOME;
    private static final String USER_UID = Util.getUID();
    private static final String DISPLAY = PrefUtil.DISPLAY;
    private static final String SHARE = "/share";

    private static final String ENGYS_22_BETA_VENDOR_HOME = "/opt";
    private static final String ENGYS_22_BETA_ENV_LOADER = "/opt/OpenFOAM-2.2_engysEdition-beta/etc/bashrc";

    private static final String OF_PLUS_VENDOR_HOME = "/opt/OpenFOAM";
    private static final String OF_PLUS_ENV_LOADER = "/opt/OpenFOAM/OpenFOAM-v1606+/etc/bashrc";

    private static final String PV_VERSION = "4.1.0";
    private static final String MACHINEFILE = "";

    private static final String WINDOWS_DOCKER_ENV = "FOR /f \"tokens=*\" %%i IN ('\"C:\\Program Files\\Docker Toolbox\\docker-machine\" env default --shell=cmd') DO %%i";

    public static CommandLine getCommandLine(Map<String, String> environment, String script) {
        String dockerImage = PrefUtil.getString(DOCKER_IMAGE);

        String casePath = ensureLinuxPath(environment.get("CASE"));
        String scriptPath = ensureLinuxPath(casePath + "/" + script);

        List<String> arguments = new LinkedList<>();

        addRunCommand(arguments, false, false);
        addScriptVariables(arguments, environment);
        addDisplayVariables(arguments);
        addUserVariables(arguments);
        addWorkDir(arguments, Util.isWindows() ? SHARE : HOME);

        arguments.add(dockerImage);

        // the actual command
        arguments.add(scriptPath);

        CommandLine commandLine = new CommandLine("docker");
        commandLine.addArguments(arguments.toArray(new String[0]), false);
        return Util.isWindows() ? windowsWrapper(environment.get("CASE"), commandLine) : commandLine;
    }

    public static String terminal(File baseDir) {
        String dockerImage = PrefUtil.getString(DOCKER_IMAGE);
        boolean isOFPlus = dockerImage.contains("openfoamplus");

        String ENV_LOADER = isOFPlus ? OF_PLUS_ENV_LOADER : ENGYS_22_BETA_ENV_LOADER;

        List<String> arguments = new LinkedList<>();

        addRunCommand(arguments, true, true);
        addDisplayVariables(arguments);
        addUserVariables(arguments);
        addWorkDir(arguments, baseDir.getAbsolutePath());

        arguments.add(dockerImage);

        // the actual command
        arguments.add("/bin/bash");
        arguments.add("--rcfile");
        arguments.add(ENV_LOADER);

        CommandLine commandLine = new CommandLine("docker");
        commandLine.addArguments(arguments.toArray(new String[0]), false);
        return Util.isWindows() ? windowsWrapper(baseDir.getAbsolutePath(), commandLine).toString() : commandLine.toString();
    }

    public static String paraview(File baseDir) {
        String dockerImage = PrefUtil.getString(DOCKER_IMAGE);
        boolean isOFPlus = dockerImage.contains("openfoamplus");

        String ENV_LOADER = isOFPlus ? OF_PLUS_ENV_LOADER : ENGYS_22_BETA_ENV_LOADER;

        List<String> arguments = new LinkedList<>();

        addRunCommand(arguments, true, false);
        addDisplayVariables(arguments);
        addUserVariables(arguments);
        addWorkDir(arguments, baseDir.getAbsolutePath());

        arguments.add(dockerImage);

        // the actual command
        arguments.add("/bin/bash");
        arguments.add("--rcfile");
        arguments.add(ENV_LOADER);
        arguments.add("-ci");
        arguments.add(PARA_FOAM);

        CommandLine commandLine = new CommandLine("docker");
        commandLine.addArguments(arguments.toArray(new String[0]), false);
        return Util.isWindows() ? windowsWrapper(baseDir.getAbsolutePath(), commandLine).toString() : commandLine.toString();
    }

    /*
     * Utils
     */
    private static String ensureLinuxPath(String winPath) {
        if (Util.isWindows()) {
            String linuxPath = winPath;
            linuxPath = linuxPath.replace(HOME, SHARE);
            if (linuxPath.endsWith("bat")) {
                linuxPath = linuxPath.replace("bat", "run");
            }
            return linuxPath.replace("\\", "/");
        } else {
            return winPath;
        }
    }

    private static void addWorkDir(List<String> arguments, String workDir) {
        arguments.add("-w");
        arguments.add(ensureLinuxPath(workDir));
    }

    private static void addScriptVariables(List<String> arguments, Map<String, String> environment) {
        String dockerImage = PrefUtil.getString(DOCKER_IMAGE);
        boolean isOFPlus = dockerImage.contains("openfoamplus");

        String NP = environment.get("NP");
        String SOLVER = environment.get("SOLVER");

        String VENDOR_HOME = isOFPlus ? OF_PLUS_VENDOR_HOME : ENGYS_22_BETA_VENDOR_HOME;
        String ENV_LOADER = isOFPlus ? OF_PLUS_ENV_LOADER : ENGYS_22_BETA_ENV_LOADER;

        String CASE = ensureLinuxPath(environment.get("CASE"));
        String LOG = ensureLinuxPath(environment.get("LOG"));

        arguments.add("-e");
        arguments.add("CASE=" + CASE);
        arguments.add("-e");
        arguments.add("NP=" + NP);
        arguments.add("-e");
        arguments.add("LOG=" + LOG);
        arguments.add("-e");
        arguments.add("ENV_LOADER=" + ENV_LOADER);
        arguments.add("-e");
        arguments.add("VENDOR_HOME=" + VENDOR_HOME);
        arguments.add("-e");
        arguments.add("SOLVER=" + SOLVER);
        arguments.add("-e");
        arguments.add("PV_VERSION=" + PV_VERSION);
        arguments.add("-e");
        arguments.add("MACHINEFILE=" + MACHINEFILE);
    }

    private static void addDisplayVariables(List<String> arguments) {
        arguments.add("-e");
        arguments.add("QT_X11_NO_MITSHM=1");
        arguments.add("-e");
        arguments.add("DISPLAY=" + DISPLAY);
    }

    private static void addUserVariables(List<String> arguments) {
        arguments.add("-e");
        arguments.add("USER=" + USER);
        arguments.add("-v");
        arguments.add(Util.isWindows() ? (SHARE + ":" + SHARE) : (HOME + ":" + HOME));
        arguments.add("-v");
        arguments.add("/etc/group:/etc/group:ro");
        arguments.add("-v");
        arguments.add("/etc/passwd:/etc/passwd:ro");
        arguments.add("-v");
        arguments.add("/etc/shadow:/etc/shadow:ro");
        arguments.add("-v");
        arguments.add("/etc/sudoers.d:/etc");
        arguments.add("-v");
        arguments.add("/tmp:/tmp");
    }

    private static void addRunCommand(List<String> arguments, boolean interactive, boolean tty) {
        arguments.add("run");
        arguments.add("--rm");
        if (interactive && tty) {
            arguments.add("-it");
        } else if (interactive) {
            arguments.add("-i");
        }
        arguments.add("-u");
        arguments.add(USER_UID);
    }

    private static CommandLine windowsWrapper(String baseDir, CommandLine commandLine) {
        File f = new File(baseDir, "dockerWrapper.bat");
        List<String> lines = new LinkedList<>();
        lines.add("@echo off");
        lines.add(WINDOWS_DOCKER_ENV);
        lines.add(commandLine.toString());
        try {
            FileUtils.writeLines(f, lines, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CommandLine(f.getAbsolutePath());
    }

}

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

package eu.engys.core;

import static eu.engys.core.project.openFOAMProject.LOG;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.ScriptBuilder;
import eu.engys.core.executor.Executor;
import eu.engys.core.executor.JavaExecutor;
import eu.engys.core.project.Model;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;

public class OpenFOAMEnvironment {

    private static final Logger logger = LoggerFactory.getLogger(OpenFOAMEnvironment.class);

    private static final String[] VARIABLES_TO_UNSET = new String[] { "LD_LIBRARY_PATH", "WM_COMPILER", "WM_PROJECT_VERSION", "WM_THIRDPARTY_VERSION", "ParaView_MAJOR", "ParaView_VERSION", "WM_MPLIB", "FOAM_MPI" };

    public static void loadEnvironment(ScriptBuilder sb) {
        cleanEnvironment(sb);
        sb.newLine();
        if (Util.isWindowsScriptStyle()) {
            sb.append("call \"%ENV_LOADER%\"");
        } else {
            sb.append("export ParaView_VERSION=$PV_VERSION");
            sb.append("export FOAM_INST_DIR=$VENDOR_HOME");
            sb.append(". $ENV_LOADER");
            sb.newLine();
            sb.append("set -e");
            sb.append("set -o pipefail");
        }
        sb.newLine();
    }

    public static void cleanEnvironment(ScriptBuilder sb) {
        for (String var : VARIABLES_TO_UNSET) {
            if (Util.isWindowsScriptStyle()) {
                sb.append("set " + var + "=");
            } else {
                sb.append("unset " + var);
            }
        }
    }

    public static void printVariables(ScriptBuilder sb) {
        if (Util.isWindowsScriptStyle()) {
            sb.append("echo \"Case         : %CASE%\"");
            sb.append("echo \"Procs        : %NP%\"");
            sb.append("echo \"Log          : %LOG%\"");
            sb.append("echo \"Env          : %ENV_LOADER%\"");
            sb.append("echo \"MachineFile  : %MACHINEFILE%\"");
            sb.append("echo \"Solver       : %SOLVER%\"");
        } else {
            sb.append("echo \"Case         : $CASE\"");
            sb.append("echo \"Procs        : $NP\"");
            sb.append("echo \"Log          : $LOG\"");
            sb.append("echo \"Env          : $ENV_LOADER\"");
            sb.append("echo \"Vendor       : $VENDOR_HOME\"");
            sb.append("echo \"Paraview     : $PV_VERSION\"");
            sb.append("echo \"MachineFile  : $MACHINEFILE\"");
            sb.append("echo \"Solver       : $SOLVER\"");
        }
        sb.newLine();
    }

    public static Map<String, String> getEnvironment(Model model) {
        return getEnvironment(model, "");
    }

//    public static Map<String, String> getEnvironment(Model model, File baseDir) {
//        return getEnvironment(model, baseDir, null, null);
//    }

    public static Map<String, String> getEnvironment(Model model, String logFileName) {
        return getEnvironment(model, model.getProject().getBaseDir(), logFileName, null);
    }

    public static Map<String, String> getEnvironment(Model model, File baseDir, String logFileName) {
        return getEnvironment(model, baseDir, logFileName, null);
    }

    public static Map<String, String> getEnvironment(Model model, String logFileName, String option) {
        return getEnvironment(model, model.getProject().getBaseDir(), logFileName, option);
    }

    private static Map<String, String> getEnvironment(Model model, File baseDir, String logFileName, String option) {
        Map<String, String> map = new HashMap<>();

        Path hostfilePath = Paths.get(model.getProject().getBaseDir().getAbsolutePath()).resolve(model.getSolverModel().getHostfilePath());

        map.put("CASE", baseDir.getAbsolutePath());

        map.put("NP", String.valueOf(model.getProject().getProcessors()));

        map.put("SOLVER", model.getState().getSolver().getName());

        if (logFileName != null) {
            map.put("LOG", Paths.get(model.getProject().getBaseDir().getAbsolutePath(), LOG, logFileName).toString());
        }

        map.put("VENDOR_HOME", getVendorHome().getAbsolutePath());

        map.put("PV_VERSION", getParaviewVersion());

        map.put("ENV_LOADER", getEnvLoader().getAbsolutePath());

        if (option != null) {
            JavaExecutor executor = Executor.jvm("eu.engys.launcher.Launcher", option, "-V");
            executor.inFolder(baseDir);
            
            CommandLine cmdLine = executor.getCommandLine();
            map.put("APPLICATION", cmdLine.toString());
        }

        // DPC
        if (model.getSolverModel().isQueue()) {
            map.put("MACHINEFILE", "-machinefile " + System.getenv("HOSTFILE"));
        } else {
            map.put("MACHINEFILE", model.getSolverModel().getMultiMachine() ? "-machinefile " + hostfilePath : "");
        }

        return map;
    }

    public static Map<String, String> getTestEnvironment() {
        Map<String, String> map = new HashMap<>();
        map.put("VENDOR_HOME", getVendorHome().getAbsolutePath());
        map.put("ENV_LOADER", getEnvLoader().getAbsolutePath());
        return map;
    }

    public static void printHeader(ScriptBuilder sb, String header) {
        if (Util.isWindowsScriptStyle()) {
            sb.append("@echo off");
        } else {
            sb.append("#!/bin/bash");
        }
        sb.append(getHeaderDelimiter(header.length()));
        sb.append(getHeaderTitle(header));
        sb.append(getHeaderDelimiter(header.length()));
        sb.newLine();
    }

    private static String getHeaderTitle(String header) {
        StringBuilder sb = new StringBuilder("echo \"");
        sb.append("*");
        sb.append(" ");
        sb.append(" ");
        sb.append(" ");
        sb.append(" ");
        sb.append(header);
        sb.append(" ");
        sb.append(" ");
        sb.append(" ");
        sb.append(" ");
        sb.append("*");
        sb.append("\"");
        return sb.toString();
    }

    private static String getHeaderDelimiter(int headerLength) {
        StringBuilder sb = new StringBuilder("echo \"");
        for (int i = 0; i < headerLength + 10; i++) {
            sb.append("*");
        }
        sb.append("\"");
        return sb.toString();
    }

    /*
     * Other
     */

    public static void trySettingOpenFoamFolder(JFrame frame) {
        if (!OpenFOAMEnvironment.isEnvironementLoaded()) {
            File[] openFoamDir = getOpenFoamDir();
            if (Util.isVarArgsNotNullAndOfSize(1, openFoamDir)) {
                PrefUtil.setOpenFoamEntry(openFoamDir[0]);
                logger.info("Environment set to {}", PrefUtil.getOpenFoamEntry());
            } else {
                UiUtil.showCoreEnvironmentNotLoadedWarning(frame);
            }
        }
    }

    public static void trySettingOpenFoamFolderOS(JFrame frame) {
        if (!OpenFOAMEnvironment.isEnvironementLoaded()) {
            File[] openFoamDir = null;
            if (Util.isUnix()) {
                openFoamDir = getOpenFoamDirOS_onUnix();
            } else {
                openFoamDir = getOpenFoamDir();
            }
            if (Util.isVarArgsNotNullAndOfSize(1, openFoamDir)) {
                PrefUtil.setOpenFoamEntry(openFoamDir[0]);
                logger.info("Environment set to {}", PrefUtil.getOpenFoamEntry());
            } else {
                UiUtil.showCoreEnvironmentNotLoadedWarning(frame);
            }
        }
    }

    public static void trySettingParaviewExecutable() {
        if (!OpenFOAMEnvironment.isParaviewPathSet() && OpenFOAMEnvironment.isEnvironementLoaded()) {
            File paraviewExecutable = getParaViewExecutablePath();
            if (paraviewExecutable != null) {
                PrefUtil.setParaViewEntry(paraviewExecutable);
                logger.info("ParaView path set to {}", PrefUtil.getParaViewEntry());
            } else {
                logger.warn("ParaView path NOT set");
            }
        }
    }

    public static File[] getOpenFoamDir() {
        File[] openFoamFolders = new File[0];
        String jarPath = "";
        try {
            jarPath = new File(OpenFOAMEnvironment.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            File vendorHome = Paths.get(jarPath).getParent().getParent().getParent().getParent().toFile();
            logger.info("Check for valid OpenFOAM folder in {}", vendorHome);
            openFoamFolders = vendorHome.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    return file.isDirectory() && file.getName().startsWith("OpenFOAM");
                }
            });
        } catch (URISyntaxException e) {
            logger.error("JarPath {}", jarPath);
        }
        return openFoamFolders;
    }

    private static File getParaViewExecutablePath() {
        File[] paraviewFolders = new File[0];
        File[] thirdPartyDirs = getThirdPartyDir();
        if (Util.isVarArgsNotNullAndOfSize(1, thirdPartyDirs)) {
            File thirdPartyDir = thirdPartyDirs[0];
            logger.info("Check for valid ParaView folder in {}", thirdPartyDir);
            paraviewFolders = thirdPartyDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() && file.getName().startsWith("ParaView");
                }
            });
        }
        if (Util.isVarArgsNotNull(paraviewFolders)) {
            for (File pvFolder : paraviewFolders) {
                File pvExecutable = Paths.get(pvFolder.getAbsolutePath(), "platforms", "linux64Gcc", "bin", "paraview").toFile();
                if (pvExecutable.exists()) {
                    return pvExecutable;
                }
            }
        }
        return null;
    }

    private static File[] getThirdPartyDir() {
        File[] openFoamFolders = getOpenFoamDir();
        File[] thirdPartyFolders = new File[0];
        if (Util.isVarArgsNotNullAndOfSize(1, openFoamFolders)) {
            File vendorHome = getOpenFoamDir()[0].getParentFile();
            logger.info("Check for valid ThirdParty folder in {}", vendorHome);
            thirdPartyFolders = vendorHome.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() && file.getName().startsWith("ThirdParty");
                }
            });
        }
        return thirdPartyFolders;
    }

    private static File[] getOpenFoamDirOS_onUnix() {
        File[] openFoamFolders = new File[0];
        File optFolder = new File("/opt");
        logger.info("Check for valid OpenFOAM folder in {}", optFolder);
        openFoamFolders = optFolder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                boolean isDir = file.isDirectory();
                String fileName = file.getName();
                boolean ubuntuNameCheck = fileName.startsWith("openfoam");
                boolean fedoraNameCheck = fileName.startsWith("OpenFOAM") && !fileName.contains("ParaView") && !fileName.contains("scotch");
                boolean suseNameCheck = fileName.startsWith("OpenFOAM") && !fileName.contains("ParaView") && !fileName.contains("scotch");
                return isDir && (ubuntuNameCheck || fedoraNameCheck || suseNameCheck);
            }
        });
        return openFoamFolders;
    }

    public static File[] getDocumentationDir() {
        File[] documentationFolders = new File[0];
        String jarPath = "";
        try {
            jarPath = new File(OpenFOAMEnvironment.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            File vendorHome = Paths.get(jarPath).getParent().getParent().getParent().getParent().toFile();
            logger.info("Check for valid documentation folder in {}", vendorHome);
            documentationFolders = vendorHome.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    return file.isDirectory() && file.getName().equals("doc");
                }
            });
        } catch (URISyntaxException e) {
            logger.error("JarPath {}", jarPath);
        }
        return documentationFolders;
    }

    public static boolean isEnvironementLoaded() {
        File openFoamDir = PrefUtil.getOpenFoamEntry();
        if (openFoamDir == null || !openFoamDir.exists() || !openFoamDir.isDirectory()) {
            return false;
        }
        return true;
    }

    public static boolean isParaviewPathSet() {
        File paraView = PrefUtil.getParaViewEntry();
        if (paraView == null || !paraView.exists() || !paraView.isFile() || !paraView.canExecute()) {
            return false;
        }
        return true;
    }

    public static boolean isFieldViewPathSet() {
        File fieldView = PrefUtil.getFieldViewEntry();
        if (fieldView == null || !fieldView.exists() || !fieldView.isFile() || !fieldView.canExecute()) {
            return false;
        }
        return true;
    }

    public static boolean isEnSightPathSet() {
        File enSight = PrefUtil.getEnsightEntry();
        if (enSight == null || !enSight.exists() || !enSight.isFile() || !enSight.canExecute()) {
            return false;
        }
        return true;
    }

    private static File getEnvLoader() {
        File openFoamEntry = PrefUtil.getOpenFoamEntry();
        if (openFoamEntry != null) {
            if (Util.isWindowsScriptStyle()) {
                return openFoamEntry.toPath().resolve("etc").resolve("batchrc.bat").toFile();
            } else {
                return openFoamEntry.toPath().resolve("etc").resolve("bashrc").toFile();
            }
        }
        return new File("");
    }

    public static File getVendorHome() {
        File openFoamEntry = PrefUtil.getOpenFoamEntry();
        if (openFoamEntry != null) {
            return openFoamEntry.getParentFile();
        }
        return new File("");
    }

    public static String getParaviewVersion() {
        File pvHome = PrefUtil.getParaViewEntry();
        if (pvHome != null && pvHome.exists()) {
            String name = pvHome.getName();
            if (name.startsWith("ParaView-")) {
                String version = name.substring("ParaView-".length());
                return version;
            }
        }
        return "";
    }

}

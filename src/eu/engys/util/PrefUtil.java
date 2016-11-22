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

package eu.engys.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

public class PrefUtil {

    public static final String USER_NAME = System.getProperty("user.name");
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String DISPLAY = System.getenv("DISPLAY");

    public static final String FAVORITES_KEY = "filechooser.favorites";

    // public static final String DOC_KEY = "doc.basedir";
    public static final String OPENFOAM_KEY = "openfoam.basedir";
    public static final String PARAVIEW_KEY = "paraview.basedir";
    public static final String PARAVIEW_BATCH_KEY = "paraview.batch.basedir";
    public static final String FIELDVIEW_KEY = "fieldview.basedir";
    public static final String ENSIGHT_KEY = "ensight.basedir";
    public static final String USE_DOCKER = "use.docker";
    public static final String DOCKER_IMAGE = "docker.image";

    // batch
    public static final String SERVER_CONNECTION_MAX_TRIES = "batch.connection.max.tries";
    public static final String SERVER_CONNECTION_REFRESH_TIME = "batch.connection.wait.time";
    public static final String SERVER_WAIT_FOR_RUN_REFRESH_TIME = "batch.running.wait.time";

    public static final String SCRIPT_RUN_REFRESH_TIME = "batch.script.refresh.time";
    public static final String SCRIPT_WAIT_FOR_KILL_REFRESH_TIME = "batch.script.kill.wait.time";

    public static final String BATCH_MONITOR_DIALOG_MAX_ROW = "batch.monitor.dialog.max.row";

    // 3d
    public static final String _3D_LOCK_INTRACTIVE_MEMORY = "3d.lock.intractive.memory";
    public static final String _3D_LOCK_INTRACTIVE_TIME = "3d.lock.intractive.time";
    public static final String _3D_TRANSPARENCY_MEMORY = "3d.transparency.memory";

    // misc
    public static final String RECENT_PROJECTS = "recent.projects";
    public static final String RECENT_STUDIES = "recent.studies";
    public static final String HELYX_DEFAULT_TERMINAL = "helyx.default.terminal";
    public static final String DEFAULT_HOSTFILE_NONE = "default.hostfile.none";
    public static final String HELYX_DEFAULT_FILE_MANAGER = "default.file.manager";
    public static final String HELYX_DEFAULT_FILE_OPENER = "default.file.opener";
    public static final String MATERIALS_USER_LIB = "materials.user.lib.";
    public static final String HIDE_EMPTY_PATCHES = "hide.empty.patches";
    public static final String HIDE_PROCESSOR_PATCHES = "hide.processor.patches";
    public static final String HIDE_PROCESSOR_CYCLIC_PATCHES = "hide.processor.cyclic.patches";

    // files
    public static final String WORK_DIR = "last.open.dir";
    public static final String LAST_IMPORT_DIR = "last.import.dir";
    public static final String LAST_OPEN_EXPORT_DIR = "last.export.dir";

    // license
    public static final String LICENSE_SERVER_NAME = "license.server.name";
    public static final String LICENSE_SERVER_PORT = "license.server.port";

    // Parallel Works
    public static final String PW_DRIVER = "parallel.works.driver";
    public static final String PW_APIKEY = "parallel.works.apikey";
    public static final String PW_WORKSPACE = "parallel.works.workspace";
    public static final String PW_WORKFLOW = "parallel.works.workflow";
    public static final String PW_PULL_RESULTS = "parallel.works.pull";

    private static CompositeConfiguration configuration;

    private static CompositeConfiguration configuration() {
        if (configuration == null) {
            reload();
        }
        return configuration;
    }

    public static void reload() {
        try {
            deleteOldPrefsFolders();
            removeDuplicatedLines(ApplicationInfo.getPrefsFile());

            PropertiesConfiguration defaults = new PropertiesConfiguration("eu/engys/resources/application.properties");

            PropertiesConfiguration preferences = new PropertiesConfiguration(ApplicationInfo.getPrefsFile());
            preferences.setDelimiterParsingDisabled(true);
            preferences.setAutoSave(true);

            configuration = new CompositeConfiguration();
            configuration.setDelimiterParsingDisabled(true);
            configuration.addConfiguration(preferences, true);
            configuration.addConfiguration(defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteOldPrefsFolders() throws IOException {
        File home = new File(PrefUtil.USER_HOME);
        File java = new File(home, ".java");
        if (java.exists()) {
            File userPrefs = new File(java, ".userPrefs");
            if (userPrefs.exists()) {
                File eu = new File(userPrefs, "eu");
                if (eu.exists()) {
                    FileUtils.deleteQuietly(eu);
                }
            }
        }
        File helyx = new File(home, ".HELYX");
        if (helyx.exists()) {
            File userPrefs = new File(helyx, ".userPrefs");
            if (userPrefs.exists()) {
                FileUtils.deleteQuietly(userPrefs);
            }
        }
    }

    private static void removeDuplicatedLines(File file) throws IOException {
        if (file.exists()) {
            List<String> fileLines = FileUtils.readLines(file);
            Set<String> lines = new LinkedHashSet<>(fileLines);
            boolean hasDuplicateLines = fileLines.size() > lines.size();
            if (hasDuplicateLines) {
                String lineEnding = Util.isWindowsScriptStyle() ? IOUtils.WIN_EOL : IOUtils.LNX_EOL;
                FileUtils.writeLines(file, null, lines, lineEnding);
            }
        }
    }

    public static File getWorkDir(String key) {
        return Util.isWindows() ? getFile(key, USER_HOME) : new File(USER_DIR);
    }

    private static File getFile(String key) {
        return getFile(key, null);
    }

    private static File getFile(String key, String def) {
        String path = configuration().getString(key, def);
        return path == null ? null : new File(path);
    }

    public static void putFile(String key, File file) {
        configuration().setProperty(key, file == null ? "" : file.getAbsolutePath());
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String def) {
        return configuration().getString(key, def);
    }

    public static void putString(String key, String value) {
        configuration().setProperty(key, value);
    }

    public static int getInt(String key) {
        return configuration().getInt(key, 0);
    }

    public static int getInt(String key, int def) {
        return configuration().getInt(key, def);
    }

    public static void putInt(String key, int value) {
        configuration().setProperty(key, String.valueOf(value));
    }

    public static void putBoolean(String key, boolean value) {
        configuration().setProperty(key, Boolean.valueOf(value));
    }

    public static Boolean getBoolean(String key) {
        return configuration().getBoolean(key, false);
    }

    public static Boolean getBoolean(String key, boolean b) {
        return configuration().getBoolean(key, b);
    }

    public static InetAddress getInetAddress(String key, InetAddress def) {
        try {
            return InetAddress.getByName(configuration().getString(key, def.getHostAddress()));
        } catch (Exception e) {
            return def;
        }
    }

    public static void putInetAddress(String key, InetAddress value) {
        configuration().setProperty(key, value.getHostAddress());
    }

    public static File getFieldViewEntry() {
        return getFile(FIELDVIEW_KEY);
    }

    public static void setFieldViewEntry(File value) {
        putFile(FIELDVIEW_KEY, value);
    }

    public static File getEnsightEntry() {
        return getFile(ENSIGHT_KEY);
    }

    public static void setEnsightEntry(File value) {
        putFile(ENSIGHT_KEY, value);
    }

    public static File getParaViewEntry() {
        return getFile(PARAVIEW_KEY);
    }

    public static void setParaViewEntry(File value) {
        putFile(PARAVIEW_KEY, value);
    }

    public static File getParaViewBatchEntry() {
        return getFile(PARAVIEW_BATCH_KEY);
    }

    public static void setParaViewBatchEntry(File value) {
        putFile(PARAVIEW_BATCH_KEY, value);
    }

    public static File getOpenFoamEntry() {
        return getFile(OPENFOAM_KEY);
    }

    public static void setOpenFoamEntry(File value) {
        putFile(OPENFOAM_KEY, value);
    }

    public static void remove(String key) {
        configuration().clearProperty(key);
    }

    public static boolean isUsingDocker() {
        if (Util.isWindows()) {
            return false;
        } else {
            boolean useDocker = getBoolean(USE_DOCKER);
            String dockerImage = getString(DOCKER_IMAGE);
            return useDocker && !dockerImage.isEmpty();
        }
    }

    public static boolean isRunningOnCloud() {
        String driver = PrefUtil.getString(PW_DRIVER);
        return !driver.isEmpty() && !driver.equals("Localhost");
    }
    
    public static void cloudOff() {
        putString(PW_DRIVER, "");
    }
    
    public static void dockerOn(String dockerImage) {
        putBoolean(USE_DOCKER, true);
        putString(DOCKER_IMAGE, dockerImage);
    }

    public static void dockerOff() {
        putBoolean(USE_DOCKER, false);
        putString(DOCKER_IMAGE, "");
    }

    public static Object getDefaultValue(String key) {
        try {
            PropertiesConfiguration defaults = new PropertiesConfiguration("eu/engys/resources/application.properties");
            Object defaultProp = defaults.getProperty(key);
            return defaultProp;
        } catch (ConfigurationException e) {
            return null;
        }

    }
}

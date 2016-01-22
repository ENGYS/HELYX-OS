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

package eu.engys.core.project.zero;

import static eu.engys.core.project.system.ControlDict.START_FROM_KEY;
import static eu.engys.core.project.system.ControlDict.START_TIME_VALUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.system.ControlDict;
import eu.engys.util.RegexpUtils;

public class ZeroFolderUtil {

    private static final Logger logger = LoggerFactory.getLogger(ZeroFolderUtil.class);

    public static final String CELL_ZONES = "cellZones";
    public static final String FACE_ZONES = "faceZones";
    public static final String BOUNDARY = "boundary";
    public static final String POLY_MESH = "polyMesh";
    public static final String CONSTANT = "constant";
    public static final String PROCESSOR = "processor";
    public static final String ZERO = "0";

    public static String PROCESSOR(int i) {
        return PROCESSOR + i;
    }

    public static void clearFiles(File parent) {
        if (parent.list().length != 0) {
            File[] files = parent.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    logger.debug("Deleting {}", file);
                    file.delete();
                }
            }
        }
    }

    public static void newZeroDir(File parent) {
        File zeroDir = getZeroDir(parent);
        if (!zeroDir.exists()) {
            zeroDir.mkdir();
            logger.warn("New Folder {}", zeroDir);
        }
        File constantDir = getConstantDir(parent);
        if (!constantDir.exists()) {
            constantDir.mkdir();
            logger.warn("New Folder {}", constantDir);
        }
        File polyMesh = getPolyMeshDir(zeroDir);
        if (!polyMesh.exists()) {
            polyMesh.mkdir();
            logger.warn("New Folder {}", polyMesh);
        }
    }

    public static File getZeroDir(File parent) {
        return new File(parent, ZERO);
    }

    public static File getPolyMeshDir(File zeroDir) {
        return new File(zeroDir, POLY_MESH);
    }

    public static File getConstantDir(File zeroDir) {
        return new File(zeroDir, CONSTANT);
    }

    public static File getRegionDir(File zeroDir, String region) {
        return new File(zeroDir, region);
    }

    public static File getBoundaryFile(File polyMesh) {
        File boundary = new File(polyMesh, BOUNDARY);
        File boundary_gz = new File(polyMesh, BOUNDARY + ".gz");
        if (boundary.exists() && boundary.length() > 0) {
            return boundary;
        } else if (boundary_gz.exists()) {
            gunzip(boundary_gz, boundary);
            return boundary;
        } else {
            return boundary;
        }
    }

    public static File getCellZonesFile(File polyMesh) {
        File cellZones = new File(polyMesh, CELL_ZONES);
        File cellZones_gz = new File(polyMesh, CELL_ZONES + ".gz");
        if (cellZones.exists() && cellZones.length() > 0) {
            return cellZones;
        } else if (cellZones_gz.exists()) {
            gunzip(cellZones_gz, cellZones);
            return cellZones;
        } else {
            return cellZones;
        }
    }

    public static File getFaceZonesFile(File polyMesh) {
        File faceZones = new File(polyMesh, FACE_ZONES);
        File faceZones_gz = new File(polyMesh, FACE_ZONES + ".gz");
        if (faceZones.exists() && faceZones.length() > 0) {
            return faceZones;
        } else if (faceZones_gz.exists()) {
            gunzip(faceZones_gz, faceZones);
            return faceZones;
        } else {
            return faceZones;
        }
    }

    public static String getActualTimeValue(openFOAMProject project) {
        return getTimeStepString(project.getZeroFolder().getTimeValue());
    }

    public static String findTimeValue(File proc0, ControlDict controlDict) {
        if (controlDict == null || !controlDict.isField(START_FROM_KEY)) {
            return getFirstFolderName(proc0);
        }

        String startFrom = controlDict.lookup(START_FROM_KEY);
        String time = "0";
        switch (startFrom) {
        case ControlDict.FIRST_TIME_VALUE:
            time = getFirstFolderName(proc0);
            break;
        case ControlDict.LATEST_TIME_VALUE:
            time = getLastFolderName(proc0);
            break;
        case ControlDict.START_TIME_VALUE:
            time = controlDict.lookup(START_TIME_VALUE);
            break;
        default:
            time = getFirstFolderName(proc0);
            break;
        }
        logger.info("Start From: " + startFrom + " T: " + time);
        return time;
    }

    private static String getFirstFolderName(File proc0) {
        String[] directories = getDirectories(proc0);
        String firstFolderName = "0";
        if (directories.length > 0) {
            Arrays.sort(directories, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return Double.valueOf(s1).compareTo(Double.valueOf(s2));
                }
            });
            firstFolderName = directories[0];
        }
        return firstFolderName;
    }

    private static String getLastFolderName(File proc0) {
        String[] directories = getDirectories(proc0);
        String lastFolderName = "0";
        if (directories != null && directories.length > 0) {
            Arrays.sort(directories, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return Double.valueOf(s1).compareTo(Double.valueOf(s2));
                }
            });
            lastFolderName = directories[directories.length - 1];
        }
        return lastFolderName;
    }

    private static String[] getDirectories(File proc0) {
        RegexFileFilter filter = new RegexFileFilter(RegexpUtils.DOUBLE);
        return proc0.list(filter);
    }

    // private static String getStartTimeValue(String value) {
    // return Double.parseDouble(value);
    // }

    public static String[] getRegions(File... zeroDirs) {
        if (zeroDirs == null || zeroDirs.length == 0) {
            return new String[0];
        }
        File zeroDir = zeroDirs[0];
        String[] regionDirs = zeroDir.list(new IsAValidRegionFolder());
        if (regionDirs == null) {
            return new String[0];
        }
        return regionDirs;
    }

    private static class IsAValidRegionFolder implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            if (name.equals(POLY_MESH)) {
                return false;
            }

            File folder = new File(dir, name);
            if (folder.isDirectory()) {
                File polyMesh = getPolyMeshDir(folder);
                if (polyMesh.exists()) {
                    File boundaryFile = getBoundaryFile(polyMesh);
                    if (boundaryFile.exists()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean exists(File... boundaryFiles) {
        if (boundaryFiles == null || boundaryFiles.length == 0) {
            return false;
        }
        boolean value = true;
        for (File file : boundaryFiles) {
            value = value && file.exists();
        }
        return value;
    }

    private static void gunzip(File inputFile, File outputFile) {
        try {
            GZIPInputStream gzipInputStream = null;

            gzipInputStream = new GZIPInputStream(new FileInputStream(inputFile));

            OutputStream out = new FileOutputStream(outputFile);

            byte[] buf = new byte[1024];
            int len;

            while ((len = gzipInputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            gzipInputStream.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimeStepString(String timeStep) {
        try {
            Integer i = Integer.valueOf(timeStep);
            return String.valueOf(i);
        } catch (NumberFormatException e) {
            return timeStep;
        }
    }

    public static void mkDirs(File[] dirs) {
        if (dirs != null) {
            for (File file : dirs) {
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
    }

    public static void delete(File[] dirs) {
        if (dirs != null) {
            for (File file : dirs) {
                logger.debug("Deleting {}", file);
                FileUtils.deleteQuietly(file);
            }
        }
    }
}

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

import static eu.engys.core.project.zero.ZeroFolderUtil.clearFiles;
import static eu.engys.core.project.zero.ZeroFolderUtil.delete;
import static eu.engys.core.project.zero.ZeroFolderUtil.getBoundaryFile;
import static eu.engys.core.project.zero.ZeroFolderUtil.getCellZonesFile;
import static eu.engys.core.project.zero.ZeroFolderUtil.getFaceZonesFile;
import static eu.engys.core.project.zero.ZeroFolderUtil.getPolyMeshDir;
import static eu.engys.core.project.zero.ZeroFolderUtil.getRegionDir;
import static eu.engys.core.project.zero.ZeroFolderUtil.newZeroDir;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import eu.engys.core.project.files.DefaultFileManager;
import eu.engys.core.project.system.ControlDict;

public class ParallelZeroFileManager extends DefaultFileManager implements ZeroFileManager {

    public ParallelZeroFileManager(File file, int nProcessors) {
        super(file);
        newZeroDirs(nProcessors);
    }

    @Override
    public void newZeroDirs(int nProcessors) {
        if (nProcessors > 1) {
            for (int i = 0; i < nProcessors; i++) {
                File processorDir = newFile("processor" + i);
                if (!processorDir.exists()) {
                    processorDir.mkdir();
                    logger.warn("-> New Folder {}", processorDir);
                }
                newZeroDir(processorDir);
            }
        }
    }

    @Override
    public void clearZeroDirs(String timeStep) {
        logger.debug("Clear zero folder!");
        for (File file : getZeroDirs(timeStep)) {
            clearFiles(file);
        }
    }

    @Override
    public String findTimeValue(ControlDict controlDict) {
        File processor = newFile("processor0");
        if (processor.exists()) {
            String zero = ZeroFolderUtil.findTimeValue(processor, controlDict);
            logger.debug("Find Time Value: {}", zero);
            return zero;
        } else {
            return "0";
        }
    }

    @Override
    public File[] getZeroDirs(String timeStep) {
        List<File> zeroFolders = new ArrayList<File>();
        for (int i = 0;; i++) {
            File processor = newFile("processor" + i);
            if (processor.exists()) {
                File zeroFolder = new File(processor, ZeroFolderUtil.getTimeStepString(timeStep));

                if (zeroFolder.exists() && zeroFolder.isDirectory()) {
                    zeroFolders.add(zeroFolder);
                } else {
                    logger.warn("Folder {} is missing", zeroFolder.getAbsolutePath());
                }

                File constantFolder = new File(processor, "constant");
                if (!constantFolder.exists()) {
                    constantFolder.mkdir();
                    logger.warn("Folder {} is missing", constantFolder.getAbsolutePath());
                }
            } else {
                break;
            }
        }
        return zeroFolders.toArray(new File[zeroFolders.size()]);
    }

    @Override
    public File[] getConstantDirs() {
        List<File> constantFolders = new ArrayList<File>();

        for (int i = 0;; i++) {
            File processor = newFile("processor" + i);
            if (processor.exists()) {
                File constantFolder = new File(processor, "constant");

                if (constantFolder.exists() && constantFolder.isDirectory()) {
                    constantFolders.add(constantFolder);
                }
            } else {
                break;
            }
        }
        return constantFolders.toArray(new File[constantFolders.size()]);
    }

    @Override
    public File[] getRegionDirs(String region, File[] zeroDir) {
        File[] polyMesh = new File[zeroDir.length];
        for (int i = 0; i < polyMesh.length; i++) {
            polyMesh[i] = getRegionDir(zeroDir[i], region);
        }
        return polyMesh;
    }

    @Override
    public File[] getPolyMeshDirs(File[] zeroDir) {
        File[] polyMesh = new File[zeroDir.length];
        for (int i = 0; i < polyMesh.length; i++) {
            polyMesh[i] = getPolyMeshDir(zeroDir[i]);
        }
        return polyMesh;
    }

    @Override
    public File[] getBoundaryFiles(File[] polyMesh) {
        File[] boundary = new File[polyMesh.length];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = getBoundaryFile(polyMesh[i]);
        }
        return boundary;
    }

    @Override
    public File[] getCellZonesFiles(File[] polyMesh) {
        File[] cellZones = new File[polyMesh.length];
        for (int i = 0; i < cellZones.length; i++) {
            cellZones[i] = getCellZonesFile(polyMesh[i]);
        }
        return cellZones;
    }

    @Override
    public File[] getFaceZonesFiles(File[] polyMesh) {
        File[] faceZones = new File[polyMesh.length];
        for (int i = 0; i < faceZones.length; i++) {
            faceZones[i] = getFaceZonesFile(polyMesh[i]);
        }
        return faceZones;
    }

    @Override
    public ZeroFolderStructure checkFileSystem() {
        ZeroFolderStructure check = new ZeroFolderStructure();

        String timeStep = findTimeValue(null);

        File[] zeroDirs = getZeroDirs(timeStep);
        File[] polyMeshes = getPolyMeshDirs(zeroDirs);
        File[] boundaryFiles = getBoundaryFiles(polyMeshes);

        if (ZeroFolderUtil.exists(boundaryFiles)) {
            check.setBoundaryFieldInZero(true);
        } else {
            File[] constantDirs = getConstantDirs();
            polyMeshes = getPolyMeshDirs(constantDirs);
            boundaryFiles = getBoundaryFiles(polyMeshes);

            if (ZeroFolderUtil.exists(boundaryFiles)) {
                check.setBoundaryFieldInConstant(true);
            }
        }
        return check;
    }

    @Override
    public void deleteAll() {
        removeRegionsZeroDirs();
        removeRegionsConstantDirs();
        removeZeroDirs("0");
        removeConstantDirs();
        removeNonZeroDirs("0");
    }

    private void removeRegionsZeroDirs() {
        File[] zeroDirs = getZeroDirs("0");
        String[] regionNames = ZeroFolderUtil.getRegions(zeroDirs);
        for (String regionName : regionNames) {
            File[] regionDirs = getRegionDirs(regionName, zeroDirs);
            delete(regionDirs);
        }
    }

    private void removeRegionsConstantDirs() {
        File[] constantDirs = getConstantDirs();
        String[] regionNames = ZeroFolderUtil.getRegions(constantDirs);
        for (String regionName : regionNames) {
            File[] regionDirs = getRegionDirs(regionName, constantDirs);
            delete(regionDirs);
        }
    }

    @Override
    public void removeZeroDirs(String timeStep) {
        File[] zeroDirs = getZeroDirs(timeStep);
        delete(zeroDirs);
    }

    public void removeConstantDirs() {
        File[] constantDirs = getConstantDirs();
        delete(constantDirs);
    }

    @Override
    public void removeNonZeroDirs(String timeStep) {
        File[] nonZeroDirs = getNonZeroDirs(timeStep);
        delete(nonZeroDirs);
    }

    @Override
    public File[] getNonZeroDirs(String timeStep) {
        List<File> nonZeroFolders = new ArrayList<File>();
        for (int i = 0;; i++) {
            File processor = newFile("processor" + i);
            if (processor.exists()) {
                File[] foldersWithANumberName = processor.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        try {
                            Double.parseDouble(name);
                            return true;
                        } catch (NumberFormatException nfee) {
                            return false;
                        }
                    }
                });
                if (foldersWithANumberName.length > 0) {
                    for (File folder : foldersWithANumberName) {
                        double folderValue = Double.parseDouble(folder.getName());
                        double timeStepValue = Double.parseDouble(timeStep);
                        if (folderValue > timeStepValue) {
                            nonZeroFolders.add(folder);
                        }
                    }
                }
            } else {
                break;
            }
        }
        return nonZeroFolders.toArray(new File[nonZeroFolders.size()]);
    }
}

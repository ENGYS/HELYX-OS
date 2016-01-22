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
import static eu.engys.core.project.zero.ZeroFolderUtil.getConstantDir;
import static eu.engys.core.project.zero.ZeroFolderUtil.getFaceZonesFile;
import static eu.engys.core.project.zero.ZeroFolderUtil.getPolyMeshDir;
import static eu.engys.core.project.zero.ZeroFolderUtil.getRegionDir;
import static eu.engys.core.project.zero.ZeroFolderUtil.newZeroDir;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import eu.engys.core.project.files.DefaultFileManager;
import eu.engys.core.project.system.ControlDict;

public class SerialZeroFileManager extends DefaultFileManager implements ZeroFileManager {

    public SerialZeroFileManager(File file) {
        super(file);
        newZeroDirs(1);
    }

    @Override
    public void newZeroDirs(int nProcessors) {
        if (nProcessors == 1) {
            newZeroDir(getFile());
        }
    }

    @Override
    public void clearZeroDirs(String timeStep) {
        for (File file : getZeroDirs(timeStep)) {
            clearFiles(file);
        }
    }

    @Override
    public String findTimeValue(ControlDict controlDict) {
        return ZeroFolderUtil.findTimeValue(getFile(), controlDict);
    }

    @Override
    public File[] getZeroDirs(String timeStep) {
        return arrayOf(newFile(ZeroFolderUtil.getTimeStepString(timeStep)));
    }

    @Override
    public File[] getConstantDirs() {
        return arrayOf(getConstantDir(getFile()));
    }

    @Override
    public File[] getRegionDirs(String region, File[] zeroDirs) {
        if (isOne(zeroDirs)) {
            File zeroDir = zeroDirs[0];
            return arrayOf(getRegionDir(zeroDir, region));
        } else {
            throw new RuntimeException("");
        }
    }

    @Override
    public File[] getPolyMeshDirs(File[] zeroDirs) {
        if (isOne(zeroDirs)) {
            File zeroDir = zeroDirs[0];
            return arrayOf(getPolyMeshDir(zeroDir));
        } else {
            throw new RuntimeException("");
        }
    }

    @Override
    public File[] getBoundaryFiles(File[] polyMeshDirs) {
        if (isOne(polyMeshDirs)) {
            File polyMesh = polyMeshDirs[0];
            return arrayOf(getBoundaryFile(polyMesh));
        } else {
            throw new RuntimeException("");
        }
    }

    @Override
    public File[] getCellZonesFiles(File[] polyMeshDirs) {
        if (isOne(polyMeshDirs)) {
            File polyMesh = polyMeshDirs[0];
            return arrayOf(getCellZonesFile(polyMesh));
        } else {
            throw new RuntimeException("");
        }
    }

    @Override
    public File[] getFaceZonesFiles(File[] polyMeshDirs) {
        if (isOne(polyMeshDirs)) {
            File polyMesh = polyMeshDirs[0];
            return arrayOf(getFaceZonesFile(polyMesh));
        } else {
            throw new RuntimeException("");
        }
    }

    private File[] arrayOf(File file) {
        return new File[] { file };
    }

    private boolean isOne(File[] files) {
        return files.length == 1;
    }

    @Override
    public ZeroFolderStructure checkFileSystem() {
        ZeroFolderStructure check = new ZeroFolderStructure();

        String timeStep = findTimeValue(null);

        File zeroDir = getZeroDirs(timeStep)[0];
        File polyMesh = getPolyMeshDir(zeroDir);
        File boundaryFile = getBoundaryFile(polyMesh);

        if (boundaryFile.exists()) {
            check.setBoundaryFieldInZero(true);
        } else {
            File constantDir = getConstantDir(getFile());
            polyMesh = getPolyMeshDir(constantDir);
            boundaryFile = getBoundaryFile(polyMesh);

            if (boundaryFile.exists()) {
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
        removeConstantPolyMeshDirs();
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
        File zeroDir = new File(getFile(), ZeroFolderUtil.getTimeStepString(timeStep));
        FileUtils.deleteQuietly(zeroDir);
    }

    @Override
    public void removeNonZeroDirs(String timeStep) {
        File[] nonZeroDirs = getNonZeroDirs(timeStep);
        delete(nonZeroDirs);
    }

    private void removeConstantPolyMeshDirs() {
        File[] constantDirs = getConstantDirs();
        File[] polyMeshes = getPolyMeshDirs(constantDirs);
        delete(polyMeshes);
    }

    @Override
    public File[] getNonZeroDirs(String timeStep) {
        List<File> nonZeroFolders = new ArrayList<File>();
        File[] foldersWithANumberName = getFile().listFiles(new FilenameFilter() {
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
        return nonZeroFolders.toArray(new File[nonZeroFolders.size()]);
    }
}

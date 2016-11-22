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
package eu.engys.core.project;

import java.io.File;

public class CaseParameters {
    private File baseDir;
    private boolean isParallel;
    private int nProcessors;
    private int[] nHierarchy;

    @Override
    public String toString() {
        return String.format("New %s case (%d processors [%d, %d, %d] ) - %s", isParallel() ? "parallel" : "serial", getnProcessors(), getnHierarchy()[0], getnHierarchy()[1], getnHierarchy()[2], getBaseDir().getAbsolutePath());
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean isParallel) {
        this.isParallel = isParallel;
    }

    public int getnProcessors() {
        return nProcessors;
    }

    public void setnProcessors(int nProcessors) {
        this.nProcessors = nProcessors;
    }

    public int[] getnHierarchy() {
        return nHierarchy;
    }

    public void setnHierarchy(int[] nHierarchy) {
        this.nHierarchy = nHierarchy;
    }
}

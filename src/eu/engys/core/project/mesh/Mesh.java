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
package eu.engys.core.project.mesh;

import java.util.LinkedList;
import java.util.List;

public class Mesh {

    private ExternalMesh externalMesh;
    private InternalMesh internalMesh;
    private MeshInfo meshInfo;

    private List<Double> timeSteps;

    public Mesh() {
        this.externalMesh = new ExternalMesh();
        this.internalMesh = new InternalMesh();
        this.meshInfo = new MeshInfo();
        this.timeSteps = new LinkedList<>();
    }

    public List<Double> getTimeSteps() {
        return timeSteps;
    }

    public void setTimeSteps(List<Double> timeSteps) {
        this.timeSteps = timeSteps;
    }

    public ExternalMesh getExternalMesh() {
        return externalMesh;
    }

    public InternalMesh getInternalMesh() {
        return internalMesh;
    }

    public MeshInfo getMeshInfo() {
        return meshInfo;
    }
    public void setMeshInfo(MeshInfo meshInfo) {
        this.meshInfo = meshInfo;
    }
    
    public boolean isEmpty() {
        return meshInfo.getCells() == 0;
    }

}

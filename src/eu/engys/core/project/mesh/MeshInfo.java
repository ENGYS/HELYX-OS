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


public class MeshInfo {

    static final String MESH_INFO = "mesh.info";
    
    static final String POINTS = "points";
    static final String CELLS = "cells";
    static final String FACES = "faces";
    static final String INTERNAL_FACES = "internal faces";

    static final String BOUNDARY_PATCHES = "boundary patches";
    static final String POINT_ZONES = "point zones";
    static final String FACE_ZONES = "face zones";
    static final String CELL_ZONES = "cell zones";

    static final String HEXAHEDRA = "hexahedra";
    static final String PRISMS = "prisms";
    static final String WEDGES = "wedges";
    static final String PYRAMIDS = "pyramids";
    static final String TET_WEDGES = "tet wedges";
    static final String TETRAHEDRA = "tetrahedra";
    static final String POLYHEDRA = "polyhedra";

    public static final long NONE = -1;
    
    private long points = NONE;
    private long cells = NONE;
    private long faces = NONE;
    private long internalFaces = NONE;
    
    private long boundaryPatches = NONE;
    private long pointZones = NONE;
    private long faceZones = NONE;
    private long cellZones = NONE;

    private long hexahedra = NONE;
    private long prisms = NONE;
    private long wedges = NONE;
    private long pyramids = NONE;
    private long tetWedges = NONE;
    private long tetrahedra = NONE;
    private long polyhedra = NONE;
    
    
    private double meshTime = 0;
//    private List<Integer> cellsPerRefinementLevel;

//    public MeshInfo() {
//        this.cellsPerRefinementLevel = new ArrayList<>();
//    }

    public long getPoints() {
        return points;
    }
    public void setPoints(long points) {
        this.points = points;
    }

    public long getCells() {
        return cells;
    }
    public void setCells(long cells) {
        this.cells = cells;
    }

    public long getFaces() {
        return faces;
    }
    public void setFaces(long faces) {
        this.faces = faces;
    }

    public long getInternalFaces() {
        return internalFaces;
    }
    public void setInternalFaces(long internalFaces) {
        this.internalFaces = internalFaces;
    }

    public long getBoundaryPatches() {
        return boundaryPatches;
    }
    public void setBoundaryPatches(long boundaryPatches) {
        this.boundaryPatches = boundaryPatches;
    }
    
    public long getPointZones() {
        return pointZones;
    }
    public void setPointZones(long pointZones) {
        this.pointZones = pointZones;
    }

    public long getFaceZones() {
        return faceZones;
    }
    public void setFaceZones(long faceZones) {
        this.faceZones = faceZones;
    }

    public long getCellZones() {
        return cellZones;
    }
    public void setCellZones(long cellZones) {
        this.cellZones = cellZones;
    }

    public long getHexahedra() {
        return hexahedra;
    }
    public void setHexahedra(long hexahedra) {
        this.hexahedra = hexahedra;
    }

    public long getPrisms() {
        return prisms;
    }
    public void setPrisms(long prisms) {
        this.prisms = prisms;
    }

    public long getWedges() {
        return wedges;
    }
    public void setWedges(long wedges) {
        this.wedges = wedges;
    }

    public long getPyramids() {
        return pyramids;
    }
    public void setPyramids(long pyramids) {
        this.pyramids = pyramids;
    }

    public long getTetWedges() {
        return tetWedges;
    }
    public void setTetWedges(long tetWedges) {
        this.tetWedges = tetWedges;
    }

    public long getTetrahedra() {
        return tetrahedra;
    }
    public void setTetrahedra(long tetrahedra) {
        this.tetrahedra = tetrahedra;
    }

    public long getPolyhedra() {
        return polyhedra;
    }
    public void setPolyhedra(long polyhedra) {
        this.polyhedra = polyhedra;
    }
    
    public boolean isValid() {
        return points > NONE;
    }

//    public double getMeshTime() {
//        return meshTime;
//    }
//    public void setMeshTime(double meshTime) {
//        this.meshTime = meshTime;
//    }
//
//    public void setCellsPerRefinementLevel(List<Integer> cellsPerRefinementLevel) {
//        this.cellsPerRefinementLevel = cellsPerRefinementLevel;
//    }
//    public List<Integer> getCellsPerRefinementLevel() {
//        return cellsPerRefinementLevel;
//    }

}

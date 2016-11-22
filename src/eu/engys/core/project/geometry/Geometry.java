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
package eu.engys.core.project.geometry;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.factory.GeometryFactory;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.geometry.surface.Plane;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkPolyData;

public class Geometry {

    public static MultiPlane FAKE_BLOCK = new MultiPlane("");

    private final List<Surface> surfaces = new ArrayList<Surface>();
    private final FeatureLines lines = new FeatureLines();
    private MultiPlane block = FAKE_BLOCK;
    private GeometryFactory geometryFactory;

    private boolean autoBoundingBox = true;
    private double[] cellSize;

    public Geometry(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
        surfaces.clear();
    }

    public BoundingBox computeBoundingBox() {
        if (Util.isVarArgsNotNull(surfaces.toArray(new Surface[0]))) {
            double xmin = Double.MAX_VALUE;
            double xmax = -Double.MAX_VALUE;
            double ymin = Double.MAX_VALUE;
            double ymax = -Double.MAX_VALUE;
            double zmin = Double.MAX_VALUE;
            double zmax = -Double.MAX_VALUE;

            for (Surface surface : getAllSurfaces()) {
                vtkPolyData dataSet = surface.getTransformedDataSet();
                if (dataSet != null) {
                    double[] bounds = dataSet.GetBounds();
                    xmin = Math.min(xmin, bounds[0]);
                    xmax = Math.max(xmax, bounds[1]);
                    ymin = Math.min(ymin, bounds[2]);
                    ymax = Math.max(ymax, bounds[3]);
                    zmin = Math.min(zmin, bounds[4]);
                    zmax = Math.max(zmax, bounds[5]);
                }
            }
            return new BoundingBox(xmin, xmax, ymin, ymax, zmin, zmax);
        } else {
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }
    }

    private List<Surface> getAllSurfaces() {
        List<Surface> allSurfaces = new ArrayList<Surface>();
        for (Surface surface : surfaces) {
            if (surface.getType().isPlane()) {
                continue;
            } else if (surface.getType().isStl()) {
                Solid[] l = (((Stl) surface).getSolids());
                for (Solid solid : l) {
                    allSurfaces.add(solid);
                }
            } else {
                allSurfaces.add(surface);
            }
        }
        return allSurfaces;
    }

    public FeatureLines getLines() {
        return lines;
    }

    public Surface[] getSurfaces() {
        return surfaces.toArray(new Surface[surfaces.size()]);
    }

    public List<Surface> getSurfacesList() {
        return surfaces;
    }

    public void loadGeometry(Model model, ProgressMonitor monitor) {
        new GeometryReader(this).loadGeometry(model, monitor);
    }

    public void saveGeometry(Model model) {
        new GeometrySaver(model, this).save();
    }

    public void writeGeometry(Model model, ProgressMonitor monitor) {
        new GeometryWriter(model, this, monitor).write();
    }

    public void loadBlock(BlockMeshDict blockMeshDict, SnappyHexMeshDict snappyHexMeshDict) {
        new BlockReader(this).loadBlock(blockMeshDict, snappyHexMeshDict);
    }

    public void saveUserDefinedBlock(Model model, MultiPlane block) {
        new BlockSaver(model).saveUserDefinedBlock(block);
    }

    public void saveAutoBlock(Model model, double spacing, boolean shouldConsiderSpacing) {
        new BlockSaver(model).saveAutomaticBlock(spacing, shouldConsiderSpacing);
    }

    public GeometryFactory getFactory() {
        return geometryFactory;
    }

    public void addSurface(Surface... surfaces) {
        for (Surface surface : surfaces) {
            this.surfaces.add(surface);
        }
    }

    public void addLine(FeatureLine... lines) {
        for (FeatureLine line : lines) {
            this.lines.addLine(line);
        }
    }

    public void removeSurfaces(Model model, Surface... surfaces) {
        for (Surface surface : surfaces) {
            geometryFactory.deleteSurface(model, surface);
            this.surfaces.remove(surface);
        }
    }

    public void removeLines(FeatureLine... lines) {
        for (FeatureLine line : lines) {
            this.lines.remove(line);
        }
    }

    public boolean contains(Surface surface) {
        return surfaces.contains(surface);
    }

    public boolean isAutoBoundingBox() {
        return autoBoundingBox;
    }

    public void setAutoBoundingBox(boolean autoBoundingBox) {
        this.autoBoundingBox = autoBoundingBox;
    }

    public Surface getABox() {
        return geometryFactory.newSurface(Box.class, getAName("box"));
    }

    public Surface getASphere() {
        return geometryFactory.newSurface(Sphere.class, getAName("sphere"));
    }

    public FeatureLine getALine() {
        return geometryFactory.newSurface(FeatureLine.class, getALineName("line"));
    }

    public Surface getARing() {
        return geometryFactory.newSurface(Ring.class, getAName("ring"));
    }

    public Surface getAPlane() {
        return geometryFactory.newSurface(Plane.class, getAName("plane"));
    }

    public Surface getACylinder() {
        return geometryFactory.newSurface(Cylinder.class, getAName("cylinder"));
    }

    public static String getAName(List<String> names, String initialName) {
        String finalName = initialName;
        int counter = 0;
        while (names.contains(finalName)) {
            finalName = initialName + counter++;
        }

        return finalName;
    }
    
    public String getAName(String name) {
        List<String> surfacesNames = new ArrayList<String>();
        for (Surface surface : surfaces) {
            surfacesNames.add(surface.getName());
        }

        return getAName(surfacesNames, name);
    }

    public String getALineName(String name) {
        List<String> linesNames = new ArrayList<String>();
        for (FeatureLine line : lines) {
            linesNames.add(line.getName());
        }

        return getAName(linesNames, name);
    }

    public MultiPlane getBlock() {
        return block;
    }

    public void setBlock(MultiPlane block) {
        this.block = block;
    }

    public boolean hasBlock() {
        return block != null && block != FAKE_BLOCK;
    }

    public void clear() {
        surfaces.clear();
    }

    public boolean isEmpty() {
        return !hasBlock() && surfaces.isEmpty();
    }

    public void hideSurfaces() {
        for (Surface surface : surfaces) {
            surface.setVisible(false);
        }
        for (FeatureLine line : lines) {
            line.setVisible(false);
        }
        if (hasBlock()) {
            block.setVisible(false);
        }
    }

    public Surface getSurfaceByPatchName(String patchName) {
        for (Surface surface : surfaces) {
            if (surface.getPatchName().equals(patchName)) {
                return surface;
            }
            if (surface.hasRegions()) {
                for (Surface region : surface.getRegions()) {
                    if (region.getPatchName().equals(patchName)) {
                        return region;
                    }
                }
            }
        }
        return null;
    }

    public Surface getSurfaceByName(String name) {
        for (Surface surface : surfaces) {
            if (surface.getName().equals(name)) {
                return surface;
            }
            if (surface.hasRegions()) {
                for (Surface region : surface.getRegions()) {
                    if (region.getName().equals(name)) {
                        return region;
                    }
                }
            }
        }
        return null;
    }

    public boolean contains(String name) {
        return getAllSurfacesNames().contains(name);
    }

    public List<String> getAllSurfacesNames() {
        List<String> names = new ArrayList<String>();
        for (Surface surface : surfaces) {
            names.add(surface.getName());
            if (surface.hasRegions()) {
                for (Surface region : surface.getRegions()) {
                    names.add(region.getName());
                }
            }
        }

        for (Surface region : block.getRegions()) {
            names.add(region.getName());
        }
        return names;
    }

    public double[] getCellSize(int level) {
        if (cellSize != null) {
            return new double[] { cellSize[0] / Math.pow(2, level), cellSize[1] / Math.pow(2, level), cellSize[2] / Math.pow(2, level) };
        } else {
            return new double[3];
        }
    }

    public void setCellSize(double[] cellSize) {
        this.cellSize = cellSize;
    }
}

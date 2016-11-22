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
package eu.engys.core.project.zero.fields;

import static eu.engys.core.project.geometry.Surface.BOX_KEY;
import static eu.engys.core.project.geometry.Surface.CENTRE_KEY;
import static eu.engys.core.project.geometry.Surface.INNER_RADIUS_KEY;
import static eu.engys.core.project.geometry.Surface.OUTER_RADIUS_KEY;
import static eu.engys.core.project.geometry.Surface.P1_KEY;
import static eu.engys.core.project.geometry.Surface.P2_KEY;
import static eu.engys.core.project.geometry.Surface.RADIUS_KEY;
import static eu.engys.core.project.system.SetFieldsDict.BOX_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.CURVATURE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.CYLINDER_ANNULUS_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.CYLINDER_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.FILE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.INCLUDE_CUT_KEY;
import static eu.engys.core.project.system.SetFieldsDict.INCLUDE_INSIDE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.INCLUDE_OUTSIDE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.NEAR_DISTANCE_KEY;
import static eu.engys.core.project.system.SetFieldsDict.OUTSIDE_POINTS_KEY;
import static eu.engys.core.project.system.SetFieldsDict.ROTATED_BOX_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.SPHERE_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.SURFACE_TO_CELL_KEY;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.CardanEulerSingularityException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.geometry.stl.STLAreaReader;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.RotatedBox;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkPolyData;

public class TopoSetReader {

    private static final Logger logger = LoggerFactory.getLogger(TopoSetReader.class);
    private ProgressMonitor monitor;
    private File baseDir;

    public TopoSetReader(File baseDir, ProgressMonitor monitor) {
        this.baseDir = baseDir;
        this.monitor = monitor;
    }

    public Surface read(Dictionary d) {
        switch (d.getName()) {
        case BOX_TO_CELL_KEY:
            return readBox(d);
        case ROTATED_BOX_TO_CELL_KEY:
            return readRotatedBox(d);
        case SPHERE_TO_CELL_KEY:
            return readSphere(d);
        case CYLINDER_TO_CELL_KEY:
            return readCylinder(d);
        case CYLINDER_ANNULUS_TO_CELL_KEY:
            return readRing(d);
        case SURFACE_TO_CELL_KEY:
            return readSurfaceToCell(d);
        default:
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private Surface readBox(Dictionary d) {
        if (d.found(BOX_KEY)) {
            String boxValue = d.lookup("box");
            String min = boxValue.substring(0, boxValue.indexOf(")") + 1).trim();
            String max = boxValue.replace(min, "").trim();

            Box box = new Box(getANameFor(Type.BOX));
            box.setMin(Dictionary.toDouble(Dictionary.toArray(min)));
            box.setMax(Dictionary.toDouble(Dictionary.toArray(max)));

            return box;
        } else {
            logger.warn("Missing key '{}'", BOX_KEY);
            return null;
        }
    }

    private String getANameFor(Type type) {
        return type + Util.generateID();
    }

    @SuppressWarnings("deprecation")
    private Surface readRotatedBox(Dictionary d) {
        if (d.found(RotatedBox.ORIGIN_KEY) && d.found(RotatedBox.I_KEY) && d.found(RotatedBox.J_KEY) && d.found(RotatedBox.K_KEY)) {

            double[] o = d.lookupDoubleArray(RotatedBox.ORIGIN_KEY);
            double[] i = d.lookupDoubleArray(RotatedBox.I_KEY);
            double[] j = d.lookupDoubleArray(RotatedBox.J_KEY);
            double[] k = d.lookupDoubleArray(RotatedBox.K_KEY);

            Vector3D v1 = new Vector3D(i);
            Vector3D v2 = new Vector3D(j);
            Vector3D v3 = new Vector3D(k);
            Vector3D vo = new Vector3D(o);

            double s1 = Vector3D.dotProduct(v1, v2);
            double s2 = Vector3D.dotProduct(v2, v3);
            double s3 = Vector3D.dotProduct(v3, v1);

            if (s1 == 0 && s2 == 0 && s3 == 0) {
                double[] delta = { v1.getNorm(), v2.getNorm(), v3.getNorm() };
                Vector3D vc = vo.add(v1.scalarMultiply(0.5)).add(v2.scalarMultiply(0.5)).add(v3.scalarMultiply(0.5));
                double[] center = vc.toArray();
                double[] rotation = { 0, 0, 0 };
                try {
                    Rotation rot = new Rotation(Vector3D.PLUS_I, Vector3D.PLUS_J, v1, v2);
                    rotation = rot.getAngles(RotationOrder.XYZ);
                } catch (CardanEulerSingularityException e) {
                }

                Box box = new Box(getANameFor(Type.BOX));
                box.setCenter(center);
                box.setDelta(delta);
                box.setRotation(rotation);

                return box;
            } else {
                RotatedBox rbox = new RotatedBox(getANameFor(Type.RBOX));
                rbox.setOrigin(o);
                rbox.setI(i);
                rbox.setJ(j);
                rbox.setK(k);
                return rbox;
            }
        } else {
            logger.warn("Missing any keys '{}' '{}' '{}' '{}'", RotatedBox.ORIGIN_KEY, RotatedBox.I_KEY, RotatedBox.J_KEY, RotatedBox.K_KEY);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private Surface readSphere(Dictionary d) {
        if (d.found(CENTRE_KEY) && d.found(RADIUS_KEY)) {
            Sphere sphere = new Sphere(getANameFor(Type.SPHERE));
            sphere.setCentre(d.lookupDoubleArray(CENTRE_KEY));
            sphere.setRadius(d.lookupDouble(RADIUS_KEY));
            return sphere;
        } else {
            logger.warn("Missing any keys '{}' '{}'", Sphere.CENTRE_KEY, Sphere.RADIUS_KEY);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private Cylinder readCylinder(Dictionary d) {
        if (d.found(P1_KEY) && d.found(P2_KEY) && d.found(RADIUS_KEY)) {
            Cylinder cylinder = new Cylinder(getANameFor(Type.CYLINDER));
            cylinder.setPoint1(d.lookupDoubleArray(P1_KEY));
            cylinder.setPoint2(d.lookupDoubleArray(P2_KEY));
            cylinder.setRadius(d.lookupDouble(RADIUS_KEY));
            return cylinder;
        } else {
            logger.warn("Missing any keys '{}' '{}' '{}'", Cylinder.POINT1_KEY, Cylinder.POINT2_KEY, Cylinder.RADIUS_KEY);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private Ring readRing(Dictionary d) {
        if (d.found(P1_KEY) && d.found(P2_KEY) && d.found(INNER_RADIUS_KEY) && d.found(OUTER_RADIUS_KEY)) {
            Ring ring = new Ring(getANameFor(Type.RING));
            ring.setPoint1(d.lookupDoubleArray(P1_KEY));
            ring.setPoint2(d.lookupDoubleArray(P2_KEY));
            ring.setInnerRadius(d.lookupDouble(INNER_RADIUS_KEY));
            ring.setOuterRadius(d.lookupDouble(OUTER_RADIUS_KEY));
            return ring;
        } else {
            logger.warn("Missing any keys '{}' '{}' '{}' '{}'", Ring.POINT1_KEY, Ring.POINT2_KEY, Ring.INNER_RADIUS_KEY, Ring.OUTER_RADIUS_KEY);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private StlArea readSurfaceToCell(Dictionary d) {
        if (d.found(FILE_KEY)) {
            StlArea stlArea = new StlArea(getANameFor(Type.STL_AREA));
            stlArea.setFile(d.lookup(FILE_KEY).replace("\"", ""));
            stlArea.setIncludeCut(d.lookupBoolean(INCLUDE_CUT_KEY));
            stlArea.setIncludeInside(d.lookupBoolean(INCLUDE_INSIDE_KEY));
            stlArea.setIncludeOutside(d.lookupBoolean(INCLUDE_OUTSIDE_KEY));

            double[][] outsidePoints = d.lookupDoubleMatrix(OUTSIDE_POINTS_KEY);
            if (outsidePoints.length == 0) {
                stlArea.setOutsidePoint(new double[] { 0, 0, 0 });
            } else {
                stlArea.setOutsidePoint(outsidePoints[0]);
            }
            stlArea.setNearDistance(d.lookupDouble(NEAR_DISTANCE_KEY));
            stlArea.setCurvature(d.lookupDouble(CURVATURE_KEY));

            File file = new File(baseDir, stlArea.getFile());
            if (file.exists()) {
                stlArea.setDataSet(readDataSet(file, monitor));
            }

            return stlArea;
        } else {
            logger.warn("Missing key '{}'", FILE_KEY);
            return null;
        }
    }

    private vtkPolyData readDataSet(File file, ProgressMonitor monitor) {
        STLAreaReader reader = new STLAreaReader(file, monitor);
        reader.run();
        List<Solid> solids = reader.getSolids();
        return solids.get(0).getDataSet();
    }
}

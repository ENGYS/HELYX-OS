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

import static eu.engys.core.project.geometry.Surface.CENTRE_KEY;
import static eu.engys.core.project.geometry.Surface.INNER_RADIUS_KEY;
import static eu.engys.core.project.geometry.Surface.OUTER_RADIUS_KEY;
import static eu.engys.core.project.geometry.Surface.P1_KEY;
import static eu.engys.core.project.geometry.Surface.P2_KEY;
import static eu.engys.core.project.geometry.Surface.RADIUS_KEY;
import static eu.engys.core.project.system.SetFieldsDict.BOX_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.CYLINDER_ANNULUS_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.CYLINDER_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.ROTATED_BOX_TO_CELL_KEY;
import static eu.engys.core.project.system.SetFieldsDict.SPHERE_TO_CELL_KEY;

import java.io.File;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.TransfromMode;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.stl.STLAreaWriter;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.RotatedBox;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;

public class TopoSetWriter {

    private static final Logger logger = LoggerFactory.getLogger(TopoSetWriter.class);
    private ProgressMonitor monitor;
    private File baseDir;

    public TopoSetWriter(File baseDir, ProgressMonitor monitor) {
        this.baseDir = baseDir;
        this.monitor = monitor;
    }

    public Dictionary write(Surface surface) {
        switch (surface.getType()) {
        case BOX:
            return writeBox(surface);
        case RBOX:
            return writeRotatedBox(surface);
        case SPHERE:
            return writeSphere(surface);
        case CYLINDER:
            return writeCylinder(surface);
        case RING:
            return writeRing(surface);
        case STL_AREA:
            return writeStlArea(surface);
        default:
            logger.error("Unknown type {}", surface.getType());
            return null;
        }
    }

    private Dictionary writeStlArea(Surface surface) {
        StlArea stlArea = (StlArea) surface;

        Dictionary surfaceToPoint = new Dictionary(SetFieldsDict.SURFACE_TO_CELL_KEY);
        surfaceToPoint.add(SetFieldsDict.FILE_KEY, "\"" + stlArea.getFile() + "\"");
        surfaceToPoint.add(SetFieldsDict.OUTSIDE_POINTS_KEY, new double[][] { stlArea.getOutsidePoint() });
        surfaceToPoint.add(SetFieldsDict.INCLUDE_CUT_KEY, stlArea.isIncludeCut());
        surfaceToPoint.add(SetFieldsDict.INCLUDE_INSIDE_KEY, stlArea.isIncludeInside());
        surfaceToPoint.add(SetFieldsDict.INCLUDE_OUTSIDE_KEY, stlArea.isIncludeOutside());
        surfaceToPoint.add(SetFieldsDict.NEAR_DISTANCE_KEY, stlArea.getNearDistance());
        surfaceToPoint.add(SetFieldsDict.CURVATURE_KEY, stlArea.getCurvature());

        _writeStlArea(baseDir, stlArea, monitor);

        return surfaceToPoint;
    }

    private void _writeStlArea(File baseDir, StlArea stlArea, ProgressMonitor monitor) {
        File file = new File(baseDir, stlArea.getFile());

        if (stlArea.getTransformMode() == TransfromMode.TO_DICTIONARY) {
            if (!file.exists()) {
                new STLAreaWriter(file, stlArea, monitor).run();
            }
        } else {
            AffineTransform transformation = stlArea.getTransformation();
            if (!file.exists() || !transformation.isIdentity()) {
                new STLAreaWriter(file, stlArea, monitor).run();
            }
        }
    }

    private Dictionary writeBox(Surface surface) {
        Box box = (Box) surface;
        double[] rotation = box.getRotation();
        if (rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0) {
            double[] center = box.getCenter();
            double[] delta = box.getDelta();

            double[] origin = { -delta[0] / 2, -delta[1] / 2, -delta[2] / 2 };
            double[] i = { delta[0], 0, 0 };
            double[] j = { 0, delta[1], 0 };
            double[] k = { 0, 0, delta[2] };

            Vector3D v_i = new Vector3D(i);
            Vector3D v_j = new Vector3D(j);
            Vector3D v_k = new Vector3D(k);
            Vector3D v_origin = new Vector3D(origin);
            Vector3D v_center = new Vector3D(center);

            Rotation rot = new Rotation(RotationOrder.XYZ, rotation[0], rotation[1], rotation[2]);
            v_i = rot.applyTo(v_i);
            v_j = rot.applyTo(v_j);
            v_k = rot.applyTo(v_k);
            v_origin = rot.applyTo(v_origin);

            i = v_i.toArray();
            j = v_j.toArray();
            k = v_k.toArray();
            origin = v_origin.add(v_center).toArray();

            Util.round(i);
            Util.round(j);
            Util.round(k);
            Util.round(origin);

            Dictionary rboxToCell = new Dictionary(ROTATED_BOX_TO_CELL_KEY);
            String originString = Dictionary.toString(origin);
            String iString = Dictionary.toString(i);
            String jString = Dictionary.toString(j);
            String kString = Dictionary.toString(k);
            rboxToCell.add(RotatedBox.ORIGIN_KEY, originString);
            rboxToCell.add(RotatedBox.I_KEY, iString);
            rboxToCell.add(RotatedBox.J_KEY, jString);
            rboxToCell.add(RotatedBox.K_KEY, kString);
            return rboxToCell;
        } else {
            Dictionary boxToCell = new Dictionary(BOX_TO_CELL_KEY);
            String min = Dictionary.toString(box.getMin());
            String max = Dictionary.toString(box.getMax());
            boxToCell.add(Surface.BOX_KEY, min + " " + max);
            return boxToCell;
        }
    }

    private Dictionary writeRotatedBox(Surface surface) {
        RotatedBox rbox = (RotatedBox) surface;
        Dictionary rboxToCell = new Dictionary(ROTATED_BOX_TO_CELL_KEY);
        String origin = Dictionary.toString(rbox.getOrigin());
        String i = Dictionary.toString(rbox.getI());
        String j = Dictionary.toString(rbox.getJ());
        String k = Dictionary.toString(rbox.getK());
        rboxToCell.add(RotatedBox.ORIGIN_KEY, origin);
        rboxToCell.add(RotatedBox.I_KEY, i);
        rboxToCell.add(RotatedBox.J_KEY, j);
        rboxToCell.add(RotatedBox.K_KEY, k);
        return rboxToCell;
    }

    private Dictionary writeSphere(Surface surface) {
        Sphere sphere = (Sphere) surface;
        Dictionary sphereToCell = new Dictionary(SPHERE_TO_CELL_KEY);
        sphereToCell.add(CENTRE_KEY, sphere.getCentre());
        sphereToCell.add(RADIUS_KEY, sphere.getRadius());
        return sphereToCell;
    }

    private Dictionary writeCylinder(Surface surface) {
        Cylinder cyl = (Cylinder) surface;
        Dictionary cylinderToCell = new Dictionary(CYLINDER_TO_CELL_KEY);
        cylinderToCell.add(P1_KEY, cyl.getPoint1());
        cylinderToCell.add(P2_KEY, cyl.getPoint2());
        cylinderToCell.add(RADIUS_KEY, cyl.getRadius());
        return cylinderToCell;
    }

    private Dictionary writeRing(Surface surface) {
        Ring ring = (Ring) surface;
        Dictionary ringToCell = new Dictionary(CYLINDER_ANNULUS_TO_CELL_KEY);
        ringToCell.add(P1_KEY, ring.getPoint1());
        ringToCell.add(P2_KEY, ring.getPoint2());
        ringToCell.add(INNER_RADIUS_KEY, ring.getInnerRadius());
        ringToCell.add(OUTER_RADIUS_KEY, ring.getOuterRadius());
        return ringToCell;
    }

}

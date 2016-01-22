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


package eu.engys.vtk.actors;

import static eu.engys.util.FormatUtil.format;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.geometry.surface.Plane;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.gui.view3D.Actor;
import eu.engys.util.progress.ProgressMonitor;

public class SurfaceToActor {
    
    public enum ActorMode {
        DEFAULT, VIRTUALISED
    };

    private static final Logger logger = LoggerFactory.getLogger(SurfaceToActor.class);

    private final ActorMode mode;
    private final ProgressMonitor monitor;
    private final BoundingBox boundingBox;

    public SurfaceToActor(ActorMode mode, BoundingBox boundingBox, ProgressMonitor monitor) {
        this.mode = mode;
        this.boundingBox = boundingBox;
        this.monitor = monitor;
    }

    public Actor[] toActor(Surface surface) {
        // System.out.println("SurfaceToActor.toActor() "+surface.getName() + " ["+surface.getType()+"] "+(surface.isVisible() ? "visible" : " NOT visible"));
        switch (surface.getType()) {
            case BOX:
                Box box = (Box) surface;
                return getBoxActor(box);
            case CYLINDER:
                Cylinder cyl = (Cylinder) surface;
                return getCylinderActor(cyl);
            case SPHERE:
                Sphere sphere = (Sphere) surface;
                return getSphereActor(sphere);
            case RING:
                Ring ring = (Ring) surface;
                return getRingActor(ring);
            case PLANE:
                if (surface instanceof Plane) {
                    Plane plane = (Plane) surface;
                    return getPlaneActor(plane);
                } else if (surface instanceof PlaneRegion) {
                    PlaneRegion plane = (PlaneRegion) surface;
                    return getPlaneRegionActor(plane);
                } else {
                    return new SurfaceActor[0];
                }
            case STL:
                Stl stl = (Stl) surface;
                return getSTLActor(stl);
            case MULTI:
                MultiPlane multi = (MultiPlane) surface;
                return getMultiPlaneActor(multi);
            case SOLID:
                Solid solid = (Solid) surface;
                return getSolidActor(solid);
            case LINE:
                FeatureLine line = (FeatureLine) surface;
                return getLineActor(line);
            default:
                return null;
        }
    }

    private Actor[] getSTLActor(Stl stl) {
        logger.info("[ADD STL] name: {}", stl.getPatchName());
        List<Actor> actors = new ArrayList<>();
        if (mode == ActorMode.DEFAULT) {
            try {
                for (Solid solid : stl.getSolids()) {
                    actors.add(new SolidActor(solid));
                }
            } catch (Throwable e) {
                logger.error("Errors loading STL", e);
            }
        } else {
            actors.add(new StlActor(stl));
        }
        return actors.toArray(new Actor[0]);
    }

    private Actor[] getSolidActor(Solid solid) {
        logger.info("[ADD SOLID] name: {}", solid.getPatchName());
        return new Actor[] { new SolidActor(solid) };
    }

    private SurfaceActor[] getLineActor(FeatureLine line) {
        logger.info("[ADD LINE] name: {}", line.getPatchName());
        return new SurfaceActor[] { new LineActor(line) };
    }

    private SurfaceActor[] getBoxActor(Box box) {
        logger.info("[ADD BOX] min: {}, max: {}", format(box.getMin()).toCents(), format(box.getMax()).toCents());
        return new SurfaceActor[] { new BoxActor(box) };
    }

    private SurfaceActor[] getCylinderActor(Cylinder cylinder) {
        logger.info("[ADD CYLINDER] point1: {}, point2: {}, radius: {}", format(cylinder.getPoint1()).toCents(), format(cylinder.getPoint2()).toCents(), format(cylinder.getRadius()).toCents());
        return new SurfaceActor[] { new CylinderActor(cylinder) };
    }

    private SurfaceActor[] getSphereActor(Sphere sphere) {
        logger.info("[ADD SPHERE] center: {}, radius: {}", format(sphere.getCenter()).toCents(), sphere.getRadius());
        return new SurfaceActor[] { new SphereActor(sphere) };
    }

    private SurfaceActor[] getRingActor(Ring ring) {
        logger.info("[ADD RING] point1: {}, point2: {}, innerRadius: {}, outerRadius: {}", format(ring.getPoint1()).toCents(), format(ring.getPoint2()).toCents(), ring.getInnerRadius(), ring.getOuterRadius());
        return new SurfaceActor[] { new RingActor(ring) };
    }

    private SurfaceActor[] getMultiPlaneActor(MultiPlane surface) {
        logger.info("[ADD MULTIPLANE] name: {}", surface.getPatchName());
        List<SurfaceActor> actors = new ArrayList<>();
        for (PlaneRegion plane : surface.getPlanes()) {
            actors.add(new PlaneRegionActor(plane, mode));
        }
        return actors.toArray(new SurfaceActor[0]);
    }

    private SurfaceActor[] getPlaneRegionActor(PlaneRegion plane) {
        logger.info("[ADD PLANE] name: {}", plane.getPatchName());
        return new SurfaceActor[] { new PlaneRegionActor(plane, mode) };
    }

    private SurfaceActor[] getPlaneActor(Plane plane) {
        if (plane.getCenter() == null) {
            plane.setCenter(boundingBox.getCenter());
        }
        if (plane.getNormal() == null) {
            plane.setNormal(new double[] {0, 0, 1});
        }
        logger.info("[ADD PLANE] origin: {}, normal: {}", format(plane.getCenter()).toCents(), format(plane.getNormal()).toCents());
        return new SurfaceActor[] { new PlaneActor(plane) };
    }
}

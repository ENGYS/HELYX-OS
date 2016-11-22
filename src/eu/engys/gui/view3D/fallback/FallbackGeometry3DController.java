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
package eu.engys.gui.view3D.fallback;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Context;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.RenderPanel;

public class FallbackGeometry3DController implements Geometry3DController {

    @Override
    public Context getCurrentContext() {
        return null;
    }

    @Override
    public void setRenderPanel(RenderPanel renderPanel) {
    }

    @Override
    public void clearContext() {
    }

    @Override
    public void geometryToMesh(GeometryToMesh g2m) {
    }

    @Override
    public void updateSurfacesSelection(Surface... selection) {
    }

    @Override
    public void updateSurfaceVisibility(Surface... selection) {
    }

    @Override
    public void updateSurfaceColor(Color color, Surface... selection) {
    }

    @Override
    public void updateSurfacesFilter(Surface... selection) {
    }

    @Override
    public void addSurfaces(Surface... surface) {
    }

    @Override
    public void transformSurfaces(AffineTransform t, boolean save, Surface... surfaces) {
    }

    @Override
    public void removeSurfaces(Surface... surfaces) {
    }

    @Override
    public BoundingBox computeBoundingBox(Surface... surfaces) {
        return new BoundingBox(0, 0, 0, 0, 0, 0);
    }

    @Override
    public void clear() {
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void applyContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public Collection<Actor> getActorsList() {
        return null;
    }

    @Override
    public Map<Surface, Actor> getActorsMap() {
        return null;
    }

    @Override
    public void loadActors() {
    }

    @Override
    public void newContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void newEmptyContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void showInternalMesh() {
    }

    @Override
    public void hideInternalMesh() {
    }

    @Override
    public void changeSurface(Surface... surface) {
    }

    @Override
    public void render() {
    }

    @Override
    public void zoomReset() {
    }

}

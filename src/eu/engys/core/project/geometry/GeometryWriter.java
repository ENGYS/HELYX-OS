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

import eu.engys.core.project.Model;
import eu.engys.util.progress.ProgressMonitor;

public class GeometryWriter {

    private Model model;
    private Geometry geometry;
    private ProgressMonitor monitor;

    public GeometryWriter(Model model, Geometry geometry, ProgressMonitor monitor) {
        this.model = model;
        this.geometry = geometry;
        this.monitor = monitor;
    }

    public void write() {
        writeFeatureLines();
        writeSurfaces();
    }

    private void writeFeatureLines() {
        for (FeatureLine line : geometry.getLines()) {
            geometry.getFactory().writeSurface(line, model, monitor);
        }
    }

    private void writeSurfaces() {
        for (Surface surface : geometry.getSurfaces()) {

            if (surface.getType().isStl()) {
                geometry.getFactory().writeSurface(surface, model, monitor);
            }
        }
    }

}

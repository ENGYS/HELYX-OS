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
package eu.engys.gui.mesh.panels.lines;

import static eu.engys.util.ui.ComponentsFactory.doubleField;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.util.Symbols;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class AutomaticBaseMeshPanel {

    public static final String AUTOMATIC_LABEL = "Automatic";
    public static final String BASE_MESHSPACING_LABEL = "Base Mesh Spacing " + Symbols.M;

    private BeanPanelBuilder builder;

    protected DoubleField meshSpacing;

    private Model model;

    public AutomaticBaseMeshPanel(Model model, BeanPanelBuilder builder) {
        this.model = model;
        this.builder = builder;

        builder.startGroup(AUTOMATIC_LABEL);
        layoutComponents();
        builder.endGroup();
    }

    private void layoutComponents() {
        meshSpacing = doubleField(1.0);
        builder.addComponent(BASE_MESHSPACING_LABEL, meshSpacing);
    }

    public void save(double spacing, boolean shouldConsiderSpacing) {
        model.getProject().getSystemFolder().getBlockMeshDict().setFromFile(false);
        model.getGeometry().setAutoBoundingBox(true);
        model.getGeometry().setCellSize(new double[] { meshSpacing.getDoubleValue(), meshSpacing.getDoubleValue(), meshSpacing.getDoubleValue() });
        model.getGeometry().saveAutoBlock(model, spacing, shouldConsiderSpacing);
    }

    public void updateBlock() {
        if (model.getGeometry().hasBlock()) {
            EventManager.triggerEvent(this, new RemoveSurfaceEvent(model.getGeometry().getBlock()));
            model.getGeometry().setBlock(Geometry.FAKE_BLOCK);
            model.blockChanged();
        }
    }

    public void setBaseMeshSpacing(double baseMeshSpacing) {
        meshSpacing.setDoubleValue(baseMeshSpacing);
    }

    public double getBaseMeshSpacing() {
        return meshSpacing.getDoubleValue();
    }

}

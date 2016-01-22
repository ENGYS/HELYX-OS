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


package eu.engys.gui.mesh.panels;

import static eu.engys.core.project.system.SnappyHexMeshDict.CASTELLATED_MESH_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LOCATION_IN_MESH;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINEMENTS_REGIONS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINEMENTS_SURFACES_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.WRAPPER_KEY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.PointInfo;
import eu.engys.core.dictionary.model.ShowLocationAdapter;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.PointEvent;
import eu.engys.util.ui.builder.PanelBuilder;

public class MaterialPointsPanel extends DefaultGUIPanel {

    public static final String TITLE = "Material Point";

    private DictionaryModel castellatedModel;
    private ShowLocationAdapter locationPanel;

    @Inject
    public MaterialPointsPanel(Model model) {
        super(TITLE, model);
    }

    @Override
    public void start() {
        super.start();
        locationPanel.turnMaterialPointsOn();
    }

    @Override
    public void stop() {
        super.stop();
        locationPanel.turnMaterialPointsOff();
    }

    @Override
    protected JComponent layoutComponents() {
        castellatedModel = new DictionaryModel(new Dictionary(""));

        PanelBuilder builder = new PanelBuilder();
        locationPanel = castellatedModel.bindLocation(LOCATION_IN_MESH, 4);
        builder.addComponent(locationPanel);
        locationPanel.addPropertyChangeListener(PointInfo.PROPERTY_NAME, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PointInfo.PROPERTY_NAME)) {
                    PointInfo pi = (PointInfo) evt.getNewValue();
                    EventManager.triggerEvent(this, new PointEvent(pi.getPoint(), pi.getKey(), pi.getAction(), pi.getColor()));
                }
            }
        });

        return builder.removeMargins().getPanel();

    }

    @Override
    public void load() {
        SnappyHexMeshDict snappyDict = model.getProject().getSystemFolder().getSnappyHexMeshDict();
        if (snappyDict != null) {
            if (snappyDict.found(CASTELLATED_MESH_CONTROLS_KEY)) {
                Dictionary castellated = snappyDict.subDict(CASTELLATED_MESH_CONTROLS_KEY);

                Dictionary castellatedCopy = new Dictionary(castellated);
                castellatedCopy.remove(REFINEMENTS_SURFACES_KEY);
                castellatedCopy.remove(REFINEMENTS_REGIONS_KEY);
                castellatedCopy.remove(WRAPPER_KEY);
                this.castellatedModel.setDictionary(castellatedCopy);
            }
        }
    }

    @Override
    public void save() {
        SnappyHexMeshDict snappyDict = model.getProject().getSystemFolder().getSnappyHexMeshDict();
        if (snappyDict != null) {
            Dictionary castellated = snappyDict.subDict(CASTELLATED_MESH_CONTROLS_KEY);
            String locations = castellatedModel.getDictionary().lookup(LOCATION_IN_MESH);
            castellated.add(LOCATION_IN_MESH, locations);
        }
    }

}

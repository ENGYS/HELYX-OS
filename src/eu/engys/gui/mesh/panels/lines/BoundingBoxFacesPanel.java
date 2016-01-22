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

package eu.engys.gui.mesh.panels.lines;

import static eu.engys.core.project.system.SnappyHexMeshDict.EXPANSION_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FCH_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FINAL_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SURFACE_LAYERS_KEY;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class BoundingBoxFacesPanel {

    public static final String NUMBER_OF_LAYERS_LABEL = "Number of Layers";
    public static final String FIRST_CELL_HEIGHT_LABEL = "First Cell Height";
    public static final String LAYER_STRETCHING_LABEL = "Layer Stretching";
    public static final String FINAL_LAYER_THICKNESS_LABEL = "Final Layer Thickness";
    public static final String TOTAL_LAYER_THICKNESS_LABEL = "Total Layer Thickness";
    public static final String FACE_NAME_LABEL = "Face Name";
    public static final String BOUNDING_BOX_FACES_LABEL = "Bounding Box Faces";

    private PanelBuilder planeBuilder;
    private DictionaryModel planeModel;
    private StringField planeName;
    private PropertyChangeListener listener;

    public BoundingBoxFacesPanel(PropertyChangeListener listener) {
        this.listener = listener;
        this.planeModel = new DictionaryModel();
        this.planeBuilder = new PanelBuilder();
        layoutComponents();
        addNameListener();
    }

    private void layoutComponents() {
        planeModel = new DictionaryModel();

        planeBuilder = new DictionaryPanelBuilder();
        planeBuilder.addComponent(FACE_NAME_LABEL, planeName = stringField());
        planeBuilder.addComponent(NUMBER_OF_LAYERS_LABEL, planeModel.bindIntegerPositive(N_SURFACE_LAYERS_KEY));
        planeBuilder.addComponent(TOTAL_LAYER_THICKNESS_LABEL, planeModel.bindDouble(MAX_LAYER_THICKNESS_KEY, (Double) null));
        planeBuilder.addComponent(FINAL_LAYER_THICKNESS_LABEL, planeModel.bindDouble(FINAL_LAYER_THICKNESS_KEY, (Double) null));
        planeBuilder.addComponent(LAYER_STRETCHING_LABEL, planeModel.bindDouble(EXPANSION_RATIO_KEY, (Double) null));
        planeBuilder.addComponent(FIRST_CELL_HEIGHT_LABEL, planeModel.bindDouble(FCH_KEY, (Double) null));
        planeBuilder.setEnabled(false);
    }

    public JPanel getPanel() {
        JPanel panel = planeBuilder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(BOUNDING_BOX_FACES_LABEL));
        panel.setName("plane.panel");
        return panel;
    }

    public void save(PlaneRegion... planes) {
        for (PlaneRegion plane : planes) {
            plane.setLayerDictionary(new Dictionary(planeModel.getDictionary()));
        }
    }

    public void selectPlane(PlaneRegion[] selection) {
        setEnabled(true);
        
        PlaneRegion plane = selection[0];
        planeModel.setDictionary(plane.getLayerDictionary());
        setPlaneName(plane.getName());
    }

    public void setEnabled(boolean enabled) {
        planeName.setEnabled(enabled);
        planeBuilder.setEnabled(enabled);
    }

    public void disableNameField() {
        planeName.setEnabled(false);
    }

    public void setPlaneName(String name) {
        removeNameListener();
        planeName.setValue(name);
        addNameListener();
    }

    public String getPlaneName() {
        return planeName.getText();
    }

    public void addNameListener() {
        planeName.addPropertyChangeListener(listener);
    }

    public void removeNameListener() {
        planeName.removePropertyChangeListener(listener);
    }

}

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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.engys.core.controller.Controller;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.events.view3D.RenameSurfaceEvent;
import eu.engys.gui.mesh.GeometryPanel;
import eu.engys.gui.mesh.actions.AddIGES;
import eu.engys.gui.mesh.actions.AddSTL;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public abstract class AbstractGeometryPanel extends AbstractGUIPanel implements GeometryPanel, ActionContainer {

    public static final String ZONES_LABEL = "Zones";
    public static final String LAYERS_LABEL = "Layers";
    public static final String REFINEMENT_LABEL = "Refinement";
    public static final String GEOMETRY = "Geometry";

    public static final String DISTANCE_M_LABEL = "Distance [m]";

    public static final String SURFACE_LABEL = "Surface";
    public static final String BAFFLE_LABEL = "Baffle";
    public static final String BOUNDARY_LABEL = "Boundary";
    public static final String INTERNAL_LABEL = "Internal";
    public static final String TYPE_LABEL = "Type";
    public static final String NAME_LABEL = "Name";
    public static final String CELL_ZONE_LABEL = "Cell Zone";
    public static final String LEVEL_LABEL = "Level";
    public static final String PROXIMITY_REFINEMENT_LABEL = "Proximity Refinement";
    public static final String FINAL_LAYER_THICKNESS_LABEL = "Final Layer Thickness";
    public static final String LAYER_STRETCHING_LABEL = "Layer Stretching";
    public static final String NUMBER_OF_LAYERS_LABEL = "Number of Layers";
    public static final String CELL_SIZE_LABEL = "Cell Size [m]";

    public static final String INSIDE_LEVEL_LABEL = "Inside Level";
    public static final String OUTSIDE_LEVEL_LABEL = "Outside Level";
    public static final String DISTANCE_LEVEL_LABEL = "Distance Level";

    public static final String MODE_LABEL = "Mode";
    public static final String NONE = "none";
    public static final String INSIDE = "inside";
    public static final String OUTSIDE = "outside";
    public static final String DISTANCE = "distance";
    public static final String NONE_LABEL = "None";
    public static final String INSIDE_LABEL = "Inside";
    public static final String OUTSIDE_LABEL = "Outside";
    public static final String DISTANCE_LABEL = "Distance";

    private GeometryBuilder surfaceRegionsBuilder;

    protected DictionaryModel surfaceModel;
    protected DictionaryModel volumeModel;
    protected DictionaryModel layerModel;
    protected DictionaryModel zoneModel;

    private GeometriesPanelBuilder geometriesPanel;

    protected PanelBuilder layersBuilder;
    protected PanelBuilder surfaceBuilder;
    protected PanelBuilder volumesBuilder;
    protected PanelBuilder zonesBuilder;

    private JTabbedPane tabbedPane;

    protected final GeometryTreeNodeManager treeNodeManager;

    public AbstractGeometryPanel(Model model, Controller controller) {
        super(GEOMETRY, model);
        this.treeNodeManager = new GeometryTreeNodeManager(model, controller, this, getGeometryActions(controller));
        model.addObserver(treeNodeManager);
        ActionManager.getInstance().parseActions(this);
    }

    protected abstract DefaultGeometryActions getGeometryActions(Controller controller);

    @Override
    protected JComponent layoutComponents() {
        surfaceModel = new DictionaryModel();
        volumeModel = new DictionaryModel();
        layerModel = new DictionaryModel();
        zoneModel = new DictionaryModel();

        geometriesPanel = new GeometriesPanelBuilder(this);
        surfaceRegionsBuilder = new GeometryBuilder(geometriesPanel, surfaceModel, volumeModel, layerModel, zoneModel);

        tabbedPane = new JTabbedPane();
        tabbedPane.setName("geometry.tabbed.pane");
        tabbedPane.putClientProperty("Synthetica.tabbedPane.tabIndex", 0);

        JPanel refinemetPanel = getRefinemetPanel();
        JPanel layersPanel = getLayersPanel();
        JPanel zonesPanel = getZonesPanel();

        refinemetPanel.setName("refinement.panel");
        layersPanel.setName("layers.panel");
        zonesPanel.setName("zones.panel");

        tabbedPane.addTab(REFINEMENT_LABEL, refinemetPanel);
        tabbedPane.addTab(LAYERS_LABEL, layersPanel);
        tabbedPane.addTab(ZONES_LABEL, zonesPanel);

        PanelBuilder builder = new PanelBuilder();
        builder.addButtons(getShapeButtons());
        builder.addSeparator("");
        geometriesPanel.addComponents(builder);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(builder.removeMargins().getPanel(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                saveSurfaces(treeNodeManager.getSelectedValues());
                selectSurface(treeNodeManager.getSelectedValues());
            }
        });

        return mainPanel;
    }

    protected abstract JButton[] getShapeButtons();

    private JPanel getRefinemetPanel() {
        PanelBuilder builder = new PanelBuilder();

        JPanel surfacesPanel = getSurfacesPanel();
        JPanel volumesPanel = getVolumesPanel();

        surfacesPanel.setName("refinement.surfaces");
        volumesPanel.setName("refinement.volumes");

        builder.addComponent(surfacesPanel);
        builder.addComponent(volumesPanel);
        return builder.getPanel();
    }

    protected abstract JPanel getSurfacesPanel();

    protected JPanel getVolumesPanel() {
        volumesBuilder = new PanelBuilder();
        final JComboBoxController comboBoxController = volumeModel.bindComboBoxController("mode");
        volumesBuilder.startChoice(MODE_LABEL, comboBoxController);

        volumesBuilder.startGroup(NONE, NONE_LABEL);
        volumesBuilder.endGroup();

        volumesBuilder.startGroup(INSIDE, INSIDE_LABEL);
        IntegerField inside = volumeModel.bindIntegerLevels("levels", INSIDE);
        volumesBuilder.addComponent(INSIDE_LEVEL_LABEL, inside);
        volumesBuilder.addComponent(CELL_SIZE_LABEL, new Size(model, inside));
        volumesBuilder.endGroup();

        volumesBuilder.startGroup(OUTSIDE, OUTSIDE_LABEL);
        IntegerField outside = volumeModel.bindIntegerLevels("levels", OUTSIDE);
        volumesBuilder.addComponent(OUTSIDE_LEVEL_LABEL, outside);
        volumesBuilder.addComponent(CELL_SIZE_LABEL, new Size(model, outside));
        volumesBuilder.endGroup();

        volumesBuilder.startGroup(DISTANCE, DISTANCE_LABEL);
        String[] columnNames = { DISTANCE_M_LABEL, LEVEL_LABEL };
        Class<?>[] type = { Double.class, Integer.class };
        volumesBuilder.addComponent(DISTANCE_LEVEL_LABEL, volumeModel.bindTableLevels(columnNames, type));
        volumesBuilder.endGroup();

        volumesBuilder.endChoice();

        comboBoxController.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean distanceRefinementOrNone = isDistanceRefinementOrNone(comboBoxController.getSelectedKey());
                surfaceBuilder.setEnabled(distanceRefinementOrNone || isCellZone());
                layersBuilder.setEnabled(distanceRefinementOrNone || isCellZone());
            }
        });
        volumesBuilder.getPanel().setBorder(BorderFactory.createTitledBorder("Volumetric"));
        return volumesBuilder.getPanel();
    }

    protected boolean isCellZone() {
        return false;
    }

    protected JPanel getLayersPanel() {
        return new JPanel();
    }

    protected JPanel getZonesPanel() {
        return new JPanel();
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

    @Override
    public void save() {
        super.save();
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) treeNodeManager.getSelectedValues());
        model.getGeometry().saveGeometry(model);
    }

    @Override
    public void stop() {
        super.stop();
        geometriesPanel.stop();
    }

    public void saveSurfaces(Surface[] surfaces) {
        surfaceRegionsBuilder.buildSurfaces(surfaces);
    }

    @Override
    public void changeSurface(Surface surface) {
        surfaceRegionsBuilder.buildSurfaces(surface);
        EventManager.triggerEvent(this, new ChangeSurfaceEvent(surface, false));
    }

    @Override
    public void renameSurface(String newName) {
        Surface[] selection = treeNodeManager.getSelectedValues();
        if (selection.length == 1) {
            Surface surface = selection[0];

            if (model.getGeometry().contains(newName)) {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Name already in use", "Name Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String oldPatchName = surface.getPatchName();
            surface.rename(newName);

            surfaceRegionsBuilder.buildSurfaces(surface);

            treeNodeManager.refreshNode(surface);

            EventManager.triggerEvent(this, new RenameSurfaceEvent(surface, oldPatchName, surface.getPatchName()));
        }
    }

    @Override
    public void clear() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) new Surface[0]);
    }

    @Action(key = "mesh.stl")
    public void addSTL() {
        new AddSTL(model, monitor) {
            @Override
            public void postLoad(List<Stl> stls) {
                addSTL(stls.toArray(new Stl[0]));
            }
        }.execute();
    }

    @Action(key = "mesh.igs")
    public void addIGES() {
        new AddIGES(model, monitor) {
            @Override
            public void postLoad(List<Stl> stls) {
                addSTL(stls.toArray(new Stl[0]));
            }
        }.execute();
    }

    public void addSTL(Stl... stls) {
        if (Util.isVarArgsNotNull(stls)) {
            for (Stl stl : stls) {
                getModel().getGeometry().addSurface(stl);
                getModel().geometryChanged(stl);
            }
            EventManager.triggerEvent(this, new AddSurfaceEvent(stls));
        }
    }

    @Action(key = "mesh.box")
    public void addBox() {
        treeNodeManager.clear();
        Surface box = model.getGeometry().getABox();

        getModel().getGeometry().addSurface(box);
        getModel().geometryChanged(box);

        EventManager.triggerEvent(this, new AddSurfaceEvent(box));
    }

    @Action(key = "mesh.cylinder")
    public void addCylinder() {
        treeNodeManager.clear();
        Surface cyl = model.getGeometry().getACylinder();

        getModel().getGeometry().addSurface(cyl);
        getModel().geometryChanged(cyl);

        EventManager.triggerEvent(this, new AddSurfaceEvent(cyl));
    }

    @Action(key = "mesh.sphere")
    public void addSphere() {
        treeNodeManager.clear();
        Surface sphere = model.getGeometry().getASphere();

        getModel().getGeometry().addSurface(sphere);
        getModel().geometryChanged(sphere);

        EventManager.triggerEvent(this, new AddSurfaceEvent(sphere));
    }

    @Action(key = "mesh.plane")
    public void addPlane() {
        treeNodeManager.clear();
        Surface plane = model.getGeometry().getAPlane();

        getModel().getGeometry().addSurface(plane);
        getModel().geometryChanged(plane);

        EventManager.triggerEvent(this, new AddSurfaceEvent(plane));
    }

    @Action(key = "mesh.ring")
    public void addRing() {
        treeNodeManager.clear();
        Surface ring = model.getGeometry().getARing();

        getModel().getGeometry().addSurface(ring);
        getModel().geometryChanged(ring);

        EventManager.triggerEvent(this, new AddSurfaceEvent(ring));
    }

    private boolean hasDistanceRefinementOrNone(Surface surface) {
        Dictionary volumeDictionary = surface.getVolumeDictionary();
        if (volumeDictionary.found("mode")) {
            String mode = volumeDictionary.lookup("mode");
            return isDistanceRefinementOrNone(mode);
        }
        return true;
    }

    private boolean isDistanceRefinementOrNone(String mode) {
        return mode == null || "distance".equals(mode) || "none".equals(mode);
    }

    public void selectSurface(Surface[] surfaces) {
        if (Util.isVarArgsNotNull(surfaces)) {
            geometriesPanel.showPanel(surfaces);

            updateGUIOnSelection(surfaces[0]);

            selectATab();

            Dictionary surfaceDictionary = surfaces[0].getSurfaceDictionary();
            Dictionary volumeDictionary = surfaces[0].getVolumeDictionary();
            Dictionary layerDictionary = surfaces[0].getLayerDictionary();
            Dictionary zoneDictionary = surfaces[0].getZoneDictionary();

            // System.out.println("DefaultGeometryPanel.selectSurface() "+surfaceDictionary+volumeDictionary+layerDictionary);

            surfaceModel.setDictionary(new Dictionary(surfaceDictionary));
            volumeModel.setDictionary(new Dictionary(volumeDictionary));
            layerModel.setDictionary(new Dictionary(layerDictionary));
            zoneModel.setDictionary(new Dictionary(zoneDictionary));
        } else {
            deselectAll();
        }
    }

    public void deselectAll() {
        geometriesPanel.showPanel(null);

        surfaceModel.setDictionary(new Dictionary(""));
        volumeModel.setDictionary(new Dictionary(""));
        layerModel.setDictionary(new Dictionary(""));
        zoneModel.setDictionary(new Dictionary(""));

        updateGUIOnSelection(null);
    }

    protected void updateGUIOnSelection(Surface surface) {
        if (surface == null) {
            surfaceBuilder.setEnabled(false);
            volumesBuilder.setEnabled(false);
            layersBuilder.setEnabled(false);
            zonesBuilder.setEnabled(false);
        } else {
            surfaceBuilder.setEnabled(true);
            volumesBuilder.setEnabled(true);
            layersBuilder.setEnabled(true);
            zonesBuilder.setEnabled(true);

            surfaceBuilder.setEnabled(surface.hasSurfaceRefinement() && hasDistanceRefinementOrNone(surface));
            volumesBuilder.setEnabled(surface.hasVolumeRefinement());
            layersBuilder.setEnabled(surface.hasLayers());
            zonesBuilder.setEnabled(surface.hasZones());
        }
    }

    private void selectATab() {
        if (tabbedPane.isEnabledAt(tabbedPane.getSelectedIndex()))
            return;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.isEnabledAt(i)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public boolean isDemo() {
        return false;
    }

    public static class Size extends DoubleField {
        private Model model;
        private IntegerField level;

        public Size(Model model, IntegerField level) {
            super(3);
            this.model = model;
            this.level = level;
            setEnabled(false);
            PropertyChangeListener listener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("value")) {
                        recalculate();
                    }
                }
            };
            level.addPropertyChangeListener(listener);
        }

        public void recalculate() {
            if (level.getValue() != null) {
                double[] d1 = model.getGeometry().getCellSize(level.getIntValue());
                setDoubleValue(d1[0]);
            } else {
                setValue(null);
            }
        }
    }

}

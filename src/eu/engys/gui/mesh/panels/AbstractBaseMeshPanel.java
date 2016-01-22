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

import static eu.engys.gui.mesh.panels.lines.AutomaticBaseMeshPanel.AUTOMATIC_LABEL;
import static eu.engys.gui.mesh.panels.lines.FromFileBaseMeshPanel.FROM_FILE_LABEL;
import static eu.engys.gui.mesh.panels.lines.UserDefinedBaseMeshPanel.USER_DEFINED_LABEL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import eu.engys.core.controller.Controller;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.application.BaseMeshTypeChangedEvent;
import eu.engys.gui.events.view3D.RenameSurfaceEvent;
import eu.engys.gui.mesh.panels.lines.AutomaticBaseMeshPanel;
import eu.engys.gui.mesh.panels.lines.BoundingBoxFacesPanel;
import eu.engys.gui.mesh.panels.lines.FromFileBaseMeshPanel;
import eu.engys.gui.mesh.panels.lines.UserDefinedBaseMeshPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.textfields.StringField;

public abstract class AbstractBaseMeshPanel extends AbstractGUIPanel {

    public static final String BASE_MESH = "Base Mesh";

    public static final String BASE_MESH_TYPE_LABEL = "Base Mesh Type";

    private BaseMeshTreeNodeManager treeNodeManager;

    protected JComboBoxController type;
    protected ActionListener typeChangeListener;

    private PlaneRegion[] selectedPlane;

    private Controller controller;

    private AutomaticBaseMeshPanel automaticPanel;
    private UserDefinedBaseMeshPanel userDefinedPanel;
    private FromFileBaseMeshPanel fromFilePanel;
    private BoundingBoxFacesPanel facesPanel;

    public AbstractBaseMeshPanel(Model model, Controller controller) {
        super(BASE_MESH, model);
        this.controller = controller;
        this.treeNodeManager = new BaseMeshTreeNodeManager(model, this);
        model.addObserver(treeNodeManager);
    }

    protected JComponent layoutComponents() {
        DictionaryPanelBuilder builder = new DictionaryPanelBuilder();

        type = (JComboBoxController) builder.startChoice(BASE_MESH_TYPE_LABEL);
        automaticPanel = new AutomaticBaseMeshPanel(model, builder);
        userDefinedPanel = new UserDefinedBaseMeshPanel(model, builder);
        fromFilePanel = new FromFileBaseMeshPanel(model, controller, builder);
        builder.endChoice();

        facesPanel = new BoundingBoxFacesPanel(new RenamePlaneListener());
        builder.addFill(facesPanel.getPanel());

        type.addActionListener(typeChangeListener = new BaseMeshTypeChangeListener());
        return builder.removeMargins().getPanel();
    }

    @Override
    public void load() {
        SnappyHexMeshDict snappyDict = model.getProject().getSystemFolder().getSnappyHexMeshDict();
        BlockMeshDict blockMeshDict = model.getProject().getSystemFolder().getBlockMeshDict();
        if (snappyDict != null) {
            loadSpacing();

            type.removeActionListener(typeChangeListener);
            loadBaseMeshType(snappyDict, blockMeshDict);
            type.addActionListener(typeChangeListener);
        }
    }

    protected abstract void loadSpacing();

    private void loadBaseMeshType(SnappyHexMeshDict snappyDict, BlockMeshDict blockMeshDict) {
        userDefinedPanel.resetToDefault();
        if (snappyDict.isAutoBlockMesh()) {
            type.setSelectedItem(AUTOMATIC_LABEL);
        } else {
            if (model.getGeometry().hasBlock()) {
                type.setSelectedItem(USER_DEFINED_LABEL);
                userDefinedPanel.load();
            } else {
                if (blockMeshDict != null && blockMeshDict.isFromFile()) {
                    type.setSelectedItem(FROM_FILE_LABEL);
                } else {
                    type.setSelectedItem(AUTOMATIC_LABEL);
                }
            }
        }
    }

    @Override
    public void save() {
        if (isUserDefined()) {
            userDefinedPanel.save();
            saveSelectedPlane();
        } else if (isFromFile()) {
            fromFilePanel.save();
        } else if (isAutomatic()) {
            automaticPanel.save();
        }
    }

    private void saveSelectedPlane() {
        if (selectedPlane != null) {
            facesPanel.save(selectedPlane);
        }
    }

    @Override
    public void clear() {
        facesPanel.setEnabled(false);
        selectedPlane = null;
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

    private void updateBlock() {
        if (isUserDefined()) {
            userDefinedPanel.updateBlock();
        } else {
            userDefinedPanel.turnOffShowBoxButton();
            if (isAutomatic()) {
                automaticPanel.updateBlock();
            } else if (isFromFile()) {
                fromFilePanel.updateBlock();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        userDefinedPanel.turnOffShowBoxButton();
    }

    public void selectPlane(PlaneRegion[] selection) {
        if (selection.length == 0) {
            clear();
        } else if (selection.length == 1) {
            facesPanel.selectPlane(selection);
            selectedPlane = selection;
        } else {
            selectedPlane = selection;
            facesPanel.setEnabled(true);
            facesPanel.setPlaneName(getMultipleSelectionName());
            facesPanel.disableNameField();
        }
    }

    private String getMultipleSelectionName() {
        StringBuilder sb = new StringBuilder();
        for (PlaneRegion plane : selectedPlane) {
            sb.append(plane.getName());
            sb.append(" ");
        }
        return sb.toString();
    }

    protected void setBaseMeshSpacing(double baseMeshSpacing) {
        automaticPanel.setBaseMeshSpacing(baseMeshSpacing);
    }

    protected double getBaseMeshSpacing() {
        return automaticPanel.getBaseMeshSpacing();
    }

    public void saveSurfaces(PlaneRegion[] selection) {
        Type type = selection[0].getType();
        List<PlaneRegion> planes = new ArrayList<>();
        for (PlaneRegion plane : selection) {
            if (plane.getType() != type)
                continue; /* uniform selection */
            planes.add(plane);
        }
        facesPanel.save(planes.toArray(new PlaneRegion[0]));
    }

    protected boolean isUserDefined() {
        return String.valueOf(type.getSelectedItem()).equals(USER_DEFINED_LABEL);
    }

    protected boolean isFromFile() {
        return String.valueOf(type.getSelectedItem()).equals(FROM_FILE_LABEL);
    }

    protected boolean isAutomatic() {
        return String.valueOf(type.getSelectedItem()).equals(AUTOMATIC_LABEL);
    }

    private class BaseMeshTypeChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // To tell the Controller to delete mesh scripts
            EventManager.triggerEvent(this, new BaseMeshTypeChangedEvent());
            updateBlock();
            save();
        }
    }

    private class RenamePlaneListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value") && evt.getSource() instanceof StringField) {
                renamePlane(selectedPlane[0]);
            }
        }

        private void renamePlane(PlaneRegion plane) {
            if (plane != null) {
                String oldPatchName = plane.getPatchName();

                String newName = facesPanel.getPlaneName();

                if (model.getGeometry().contains(newName)) {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Name already in use", "Name Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                plane.rename(newName);
                String newPatchName = plane.getPatchName();
                plane.getParent().renameRegion(oldPatchName, newPatchName);

                treeNodeManager.getTree().repaint();

                EventManager.triggerEvent(this, new RenameSurfaceEvent(plane, oldPatchName, newPatchName));
            }
        }

    }

}

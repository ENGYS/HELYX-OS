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


package eu.engys.gui.casesetup.materials.panels;

import static eu.engys.core.project.constant.ThermophysicalProperties.MATERIAL_NAME_KEY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.Model;
import eu.engys.gui.casesetup.materials.CompressibleMaterialsPanel;
import eu.engys.gui.casesetup.materials.IncompressibleMaterialsPanel;
import eu.engys.util.PrefUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class MaterialsDatabasePanel extends JPanel {

    public static final String NEW_LABEL = "New";
    public static final String COPY_LABEL = "Copy";
    public static final String REMOVE_LABEL = "Remove";

    public static final String NEW_TOOLTIP = "Create New Material";
    public static final String COPY_TOOLTIP = "Copy Currently Selected Material";
    public static final String REMOVE_TOOLTIP = "Remove Currently Selected Material";

    public static final String COPY_OF_SUFFIX = "_copy";

    private final Action newMaterial = new NewMaterialAction();
    private final Action copyMaterial = new CopyMaterialAction();
    private final Action removeMaterial = new RemoveMaterialAction();

    private MaterialParametersPanel parametersPanel;

    private DefaultMaterialsListListener materialListener;
    private UserMaterialsListListener userListener;

    private DefaultListModel<String> materialsListModel;
    private DefaultListModel<String> userListModel;

    private JList<String> defaultMaterialsList;
    private JList<String> userMaterialsList;

    private Model model;
    private Map<String, Dictionary> userMaterials = new HashMap<String, Dictionary>();

    @Inject
    public MaterialsDatabasePanel(final Model model, CompressibleMaterialsPanel compressiblePanel, IncompressibleMaterialsPanel incompressiblePanel) {
        super(new BorderLayout());
        this.model = model;

        materialListener = new DefaultMaterialsListListener();
        userListener = new UserMaterialsListListener();

        materialsListModel = new DefaultListModel<String>();
        userListModel = new DefaultListModel<String>();

        defaultMaterialsList = new JList<String>(materialsListModel);
        defaultMaterialsList.setName("materials.library.list");
        defaultMaterialsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userMaterialsList = new JList<String>(userListModel);
        userMaterialsList.setName("user.library.list");
        userMaterialsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        defaultMaterialsList.addListSelectionListener(materialListener);
        userMaterialsList.addListSelectionListener(userListener);

        parametersPanel = new MaterialParametersPanel(compressiblePanel, incompressiblePanel);
        parametersPanel.setEnabled(false);

        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setOneTouchExpandable(false);
        leftSplitPane.setTopComponent(configureList(defaultMaterialsList, "Default Library"));
        leftSplitPane.setBottomComponent(configureList(userMaterialsList, "User Library"));

        JComponent buttonsPanel = UiUtil.getCommandColumn(getButtons());
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(UiUtil.TWO_SPACES, UiUtil.TWO_SPACES, 0, UiUtil.TWO_SPACES));

        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerLocation(150);
        mainSplitPane.setOneTouchExpandable(false);
        mainSplitPane.setLeftComponent(leftSplitPane);
        JScrollPane comp = new JScrollPane(parametersPanel);
        comp.setBorder(BorderFactory.createEmptyBorder());
        mainSplitPane.setRightComponent(comp);

        add(mainSplitPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.EAST);
    }

    private List<JComponent> getButtons() {
        List<JComponent> buttons = new ArrayList<>();
        JButton newMaterialButton = new JButton(newMaterial);
        JButton copyMaterialButton = new JButton(copyMaterial);
        JButton removeMaterialButton = new JButton(removeMaterial);

        newMaterialButton.setName(NEW_LABEL);
        copyMaterialButton.setName(COPY_LABEL);
        removeMaterialButton.setName(REMOVE_LABEL);

        copyMaterial.setEnabled(false);
        removeMaterial.setEnabled(false);

        buttons.add(newMaterialButton);
        buttons.add(copyMaterialButton);
        buttons.add(removeMaterialButton);
        return buttons;
    }

    private JComponent configureList(JList<String> list, String label) {
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createTitledBorder(label));
        list.setOpaque(false);
        list.setBackground(getBackground().darker().brighter());
        list.setSelectionBackground(getBackground().darker());
        list.setSelectionForeground(Color.WHITE);
        list.setCellRenderer(new DefaultListCellRenderer());
        return scrollPane;
    }

    public void load() {
        updateDefaultMaterialsList();

        loadUserMaterials();
        updateUserMaterialsList();

        parametersPanel.show(model.getState());
    }

    private void loadUserMaterials() {
        userMaterials.clear();

        String userMaterialsProperties = PrefUtil.getString(PrefUtil.MATERIALS_USER_LIB + model.getState().getFlow(), null);
        if (userMaterialsProperties != null) {
            Dictionary userDictionary = DictionaryUtils.readDictionary(userMaterialsProperties);

            if (userDictionary.found("materials")) {
                for (Dictionary matDict : userDictionary.subDict("materials").getDictionaries()) {
                    try {
                        if (model.getState().isCompressible()) {
                            if (matDict.found("thermoType")) {
                                userMaterials.put(matDict.getName(), matDict);
                            }
                        } else {
                            userMaterials.put(matDict.getName(), matDict);
                        }

                    } catch (Exception e) {
                        System.out.println("INVALID MATERIAL DICTIONARY: " + matDict.getName());
                    }
                }
            }
        }
    }

    public void saveUserMaterials() {
        saveCurrentSelectedUserDefinedMaterial();
        Dictionary userMaterialsProperties = new Dictionary("materials");
        for (Dictionary mat : userMaterials.values()) {
            userMaterialsProperties.add(mat);
        }
        PrefUtil.putString(PrefUtil.MATERIALS_USER_LIB + model.getState().getFlow(), userMaterialsProperties.toString());
    }

    public Dictionary getMaterial() {
        return parametersPanel.getMaterial(model);
    }

    private void updateUserMaterialsList() {
        userMaterialsList.removeListSelectionListener(userListener);
        int selection = userMaterialsList.getSelectedIndex();
        userListModel.clear();
        for (String mat : userMaterials.keySet()) {
            userListModel.addElement(mat);
        }
        userMaterialsList.setSelectedIndex(selection);
        userMaterialsList.addListSelectionListener(userListener);
    }

    private void updateDefaultMaterialsList() {
        defaultMaterialsList.removeListSelectionListener(materialListener);
        materialsListModel.clear();
        Collection<Dictionary> materials = null;
        if (model.getState().isCompressible()) {
            materials = model.getMaterialsDatabase().getCompressibleMaterials();
        } else {
            materials = model.getMaterialsDatabase().getIncompressibleMaterials();
        }
        for (Dictionary mat : materials) {
            materialsListModel.addElement(mat.lookup(MATERIAL_NAME_KEY));
        }
        defaultMaterialsList.addListSelectionListener(materialListener);
    }

    private final class DefaultMaterialsListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && e.getSource() == defaultMaterialsList) {
                String key = (String) defaultMaterialsList.getSelectedValue();
                if (key == null)
                    return;

                clearSelectionOnUserMaterialsList();

                saveCurrentSelectedUserDefinedMaterial();

                if (model.getState().isCompressible()) {
                    setMaterialToParametersPanel(new Dictionary(model.getMaterialsDatabase().getCompressibleMaterialsMap().get(key)));
                } else {
                    setMaterialToParametersPanel(new Dictionary(model.getMaterialsDatabase().getIncompressibleMaterialsMap().get(key)));
                }

                parametersPanel.getNameField(model).setEnabled(false);

                copyMaterial.setEnabled(true);
                removeMaterial.setEnabled(false);
                parametersPanel.setEnabled(false);
            }
        }
    }

    private final class UserMaterialsListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && e.getSource() == userMaterialsList) {
                String key = (String) userMaterialsList.getSelectedValue();
                if (key == null)
                    return;

                clearSelectionOnDefaultMaterialsList();

                saveCurrentSelectedUserDefinedMaterial();

                Dictionary dict = new Dictionary(userMaterials.get(key));
                setMaterialToParametersPanel(dict);

                parametersPanel.getNameField(model).setEnabled(true);

                copyMaterial.setEnabled(true);
                removeMaterial.setEnabled(true);
                parametersPanel.setEnabled(true);
            }
        }
    }

    private void saveCurrentSelectedUserDefinedMaterial() {
        Dictionary currentMaterial = parametersPanel.getMaterial(model);
        if (userMaterials.keySet().contains(currentMaterial.getName())) {
            // If name is changed
            String oldMaterialName = currentMaterial.getName();
            String newMaterialName = currentMaterial.lookup(MATERIAL_NAME_KEY);

            userMaterials.remove(oldMaterialName);
            Dictionary newMaterialDict = new Dictionary(newMaterialName, currentMaterial);
            userMaterials.put(newMaterialName, newMaterialDict);

            updateUserMaterialsList();
        }

    }

    private void setMaterialToParametersPanel(Dictionary material) {
        parametersPanel.getNameField(model).removePropertyChangeListener(nameChangeListener);
        parametersPanel.setMaterial(material);
        parametersPanel.getNameField(model).addPropertyChangeListener(nameChangeListener);
    }

    /*
     * Actions
     */

    private final class NewMaterialAction extends ViewAction {
        public NewMaterialAction() {
            super(NEW_LABEL, NEW_TOOLTIP);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clearSelectionOnAllLists();

            Dictionary emptyDict = parametersPanel.getEmptyMaterial(model);

            String validMaterialName = getValidMaterialName(emptyDict.getName());
            Dictionary newMaterialDict = new Dictionary(validMaterialName, emptyDict);
            newMaterialDict.add(MATERIAL_NAME_KEY, validMaterialName);

            userMaterials.put(validMaterialName, newMaterialDict);
            updateUserMaterialsList();
            userMaterialsList.setSelectedValue(validMaterialName, true);
        }
    }

    private final class CopyMaterialAction extends ViewAction {

        public CopyMaterialAction() {
            super(COPY_LABEL, COPY_TOOLTIP);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clearSelectionOnAllLists();

            Dictionary currentlySelectedMaterial = parametersPanel.getMaterial(model);

            String validMaterialName = getValidMaterialName(currentlySelectedMaterial.getName());
            Dictionary copyMaterial = new Dictionary(validMaterialName, currentlySelectedMaterial);
            copyMaterial.add(MATERIAL_NAME_KEY, validMaterialName);

            userMaterials.put(validMaterialName, copyMaterial);
            updateUserMaterialsList();
            userMaterialsList.setSelectedValue(validMaterialName, true);
        }

    }

    private final class RemoveMaterialAction extends ViewAction {
        public RemoveMaterialAction() {
            super(REMOVE_LABEL, REMOVE_TOOLTIP);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String key = (String) userMaterialsList.getSelectedValue();
            if (key == null)
                return;
            userMaterials.remove(key);
            updateUserMaterialsList();
            defaultMaterialsList.setSelectedIndex(0);
        }
    }

    private void clearSelectionOnAllLists() {
        clearSelectionOnDefaultMaterialsList();
        clearSelectionOnUserMaterialsList();
    }

    private void clearSelectionOnDefaultMaterialsList() {
        defaultMaterialsList.removeListSelectionListener(userListener);
        defaultMaterialsList.clearSelection();
        defaultMaterialsList.addListSelectionListener(userListener);
    }

    private void clearSelectionOnUserMaterialsList() {
        userMaterialsList.removeListSelectionListener(userListener);
        userMaterialsList.clearSelection();
        userMaterialsList.addListSelectionListener(userListener);
    }

    private String getValidMaterialName(String materialname) {
        Map<String, Dictionary> materials = null;

        if (model.getState().isCompressible()) {
            materials = model.getMaterialsDatabase().getCompressibleMaterialsMap();
        } else {
            materials = model.getMaterialsDatabase().getIncompressibleMaterialsMap();
        }

        String uniqueMaterialName = materialname;
        while (materials.containsKey(uniqueMaterialName)) {
            uniqueMaterialName = uniqueMaterialName + COPY_OF_SUFFIX;
        }
        while (userMaterials.containsKey(uniqueMaterialName)) {
            uniqueMaterialName = uniqueMaterialName + COPY_OF_SUFFIX;
        }
        return uniqueMaterialName;
    }

    /*
     * Listeners
     */

    PropertyChangeListener nameChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                String oldname = (String) evt.getOldValue();
                if (oldname != null) {
                    String newName = (String) evt.getNewValue();

                    Dictionary currentMaterial = parametersPanel.getMaterial(model);

                    userMaterials.remove(oldname);
                    Dictionary newMaterialDict = new Dictionary(newName, currentMaterial);
                    userMaterials.put(newName, newMaterialDict);

                    updateUserMaterialsList();
                    userMaterialsList.setSelectedValue(newName, true);
                }
            }
        }
    };
}

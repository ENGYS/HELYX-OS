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


package eu.engys.gui.casesetup.boundaryconditions;

import static eu.engys.util.ui.ComponentsFactory.selectField;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.IBoundaryConditionsPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.core.project.zero.patches.BoundaryConditionsDefaults;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.Util;
import eu.engys.util.ui.ComponentsFactory.SelectField;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class BoundaryConditionsPanel extends AbstractGUIPanel implements IBoundaryConditionsPanel {

    public static final String BOUNDARY_CONDITIONS = "Boundary Conditions";
    public static final String PATCH_TYPE_LABEL = "Patch Type";
    public static final String PATCH_NAME_LABEL = "Patch Name";

    private CardLayout centerPanelLayout;
    private JPanel centerPanel;

    private final Set<BoundaryTypePanel> panels;
    private Map<BoundaryType, BoundaryTypePanel> panelsByType = new HashMap<BoundaryType, BoundaryTypePanel>();

    private StringField patchNameField;
    private SelectField<String> patchTypeField;

    private BoundaryType activeBoundaryType;

    private SelectBoundaryConditionAction selectBoundaryConditionListener;
    private BoundaryConditionsTreeNodeManager treeNodeManager;

    private PropertyChangeListener listener;

    private Set<ApplicationModule> modules;

    @Inject
    public BoundaryConditionsPanel(Model model, Set<ApplicationModule> modules, Set<BoundaryTypePanel> panels) {
        super(BOUNDARY_CONDITIONS, model);
        this.treeNodeManager = new BoundaryConditionsTreeNodeManager(model, this);
        this.panels = panels;
        this.modules = modules;
        model.addObserver(treeNodeManager);
    }

    protected JComponent layoutComponents() {
        centerPanelLayout = new CardLayout();
        centerPanel = new JPanel(centerPanelLayout);
        centerPanel.add(new JLabel(), "other");

        PanelBuilder bcTypeBuider = new PanelBuilder();

        // qui non usare il panel builder altrimenti il bordo del tabbedpane non
        // arriva fino in fondo
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(bcTypeBuider.margins(0, 0, 1, 0).getPanel(), BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        for (BoundaryTypePanel typePanel : panels) {
            addTypePanel(typePanel);
        }
        
        
        initNameField();
        initTypeField();
        
        bcTypeBuider.addComponent(PATCH_NAME_LABEL, patchNameField);
        bcTypeBuider.addComponent(PATCH_TYPE_LABEL, patchTypeField);

        return panel;
    }

    @Override
    public void addTypePanel(BoundaryTypePanel typePanel) {
        BoundaryType type = typePanel.getType();
        if (! panelsByType.containsKey(type)) {
            typePanel.layoutPanel();
            panelsByType.put(type, typePanel);
            centerPanel.add(typePanel.getPanel(), type.getKey());
            ModulesUtil.configureBoundaryConditionsView(modules, typePanel);
        }
    }

    @Override
    public void removeTypePanel(BoundaryTypePanel typePanel) {
        BoundaryType type = typePanel.getType();
        if (panelsByType.containsKey(type)) {
            panelsByType.remove(type);
            centerPanel.remove(typePanel.getPanel());
            ModulesUtil.configureBoundaryConditionsView(modules, typePanel);
        }
    }

    private void initTypeField() {
        patchTypeField = selectField();
        patchTypeField.setSelectedIndex(-1);
        patchTypeField.setEnabled(false);
        selectBoundaryConditionListener = new SelectBoundaryConditionAction(patchTypeField);
        patchTypeField.addActionListener(selectBoundaryConditionListener);
    }

    private void initNameField() {
        patchNameField = stringField();
        patchNameField.setEnabled(false);
        listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Patch[] patches = treeNodeManager.getSelectedValues();
                if (patches.length == 1 && patches[0] != null) {
                    patches[0].setName(patchNameField.getText());
                    treeNodeManager.refreshNode(patches[0]);
                }
            }
        };
        patchNameField.addPropertyChangeListener(listener);
    }

    class SelectBoundaryConditionAction implements ActionListener {
        private JComboBox<String> patchTypeField;

        public SelectBoundaryConditionAction(JComboBox<String> patchTypeField) {
            this.patchTypeField = patchTypeField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedType = (String) patchTypeField.getSelectedItem();
            BoundaryType boundaryType = BoundaryType.getType(selectedType);
            BoundaryConditions defaults = BoundaryConditionsDefaults.get(boundaryType.getKey());
            Patch[] patches = treeNodeManager.getSelectedValues();
            if (patches.length > 0) {
                for (Patch patch : patches) {
                    fixAMIPatch(patch);
                    patch.setBoundaryConditions(new BoundaryConditions(defaults));
                    patch.setPhisicalType(boundaryType);
                }
            }
            activateBoundaryMesh(patches);
            treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) patches);
        }

        private void fixAMIPatch(Patch patch) {
            if (patch.getPhisicalType().isCyclicAMI() && patch.getDictionary().found("neighbourPatch")) {
                String neighboutPatchName = patch.getDictionary().lookup("neighbourPatch");
                for (Patch p : model.getPatches().filterProcBoundary()) {
                    if (p.getName().equals(neighboutPatchName)) {
                        p.setPhisicalType(BoundaryType.getDefaultType());
                        p.setBoundaryConditions(new BoundaryConditions(BoundaryConditionsDefaults.get(BoundaryType.getDefaultKey())));
                        p.setDictionary(new Dictionary(""));
                    }
                }
            }
        }
    }

    public void updateSelection(Patch[] currentSelection) {
        updatePatchNameField(currentSelection);
        updatePatchTypeField(currentSelection);
        activateBoundaryMesh(currentSelection);
    }

    private void updatePatchNameField(Patch[] patches) {
        patchNameField.removePropertyChangeListener(listener);
        if (patches.length == 1) {
            patchNameField.setValue(patches[0].getName());
        } else {
            StringBuilder sb = new StringBuilder();
            for (Patch patch : patches) {
                sb.append(patch.getName() + " ");
            }
            patchNameField.setValue(sb.toString());
        }
        patchNameField.addPropertyChangeListener(listener);
    }

    private void updatePatchTypeField(Patch[] patches) {
        patchTypeField.removeActionListener(selectBoundaryConditionListener);
        if (patches.length > 0) {
            patchTypeField.setEnabled(true);
            if (patchesAreOfTheSameType(patches)) {
                patchTypeField.setSelectedItem(patches[0].getPhisicalType().getKey());
            } else {
                patchTypeField.setSelectedItem(null);
            }
        } else {
            patchTypeField.setSelectedItem(null);
            patchTypeField.setEnabled(false);
        }
        patchTypeField.addActionListener(selectBoundaryConditionListener);
    }

    private boolean patchesAreOfTheSameType(Patch[] patches) {
        BoundaryType type = null;
        for (Patch patch : patches) {
            if (type == null)
                type = patch.getPhisicalType();
            else if (patch.getPhisicalType() != type)
                return false;

        }
        return true;
    }

    private void activateBoundaryMesh(Patch[] patches) {
        if (patches.length > 0) {
            BoundaryType boundaryType = patchesAreOfTheSameType(patches) ? patches[0].getPhisicalType() : null;

            if (boundaryType != null) {
                if (boundaryType.hasBoundaryConditions()) {
                    // System.out.println("BoundaryConditionsPanel.activateBoundaryMesh() "
                    // + patches[0].getBoundaryConditions().toDictionary());
                    centerPanelLayout.show(centerPanel, boundaryType.getKey());
                    panelsByType.get(boundaryType).loadFromPatches(patches);
                } else {
                    centerPanelLayout.show(centerPanel, "other");
                }
            } else {
                centerPanelLayout.show(centerPanel, "other");
            }
            setActiveBoundaryType(boundaryType);
        } else {
            centerPanelLayout.show(centerPanel, "other");
            setActiveBoundaryType(null);
        }
    }

    private void setActiveBoundaryType(BoundaryType boundaryType) {
        this.activeBoundaryType = boundaryType;
    }

    @Override
    public void load() {
        ModulesUtil.configureBoundaryConditionsView(modules, this);
        loadTypeField();

        BoundaryConditionsDefaults.updateBoundaryConditionsDefaultsByFields(model);
        for (BoundaryTypePanel panel : panels) {
            panel.stateChanged();
            panel.materialsChanged();
        }
    }

    @Override
    public void save() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) treeNodeManager.getSelectedValues());
    }

    @Override
    public void clear() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) new Patch[0]);
        for (BoundaryTypePanel p : panels) {
            p.resetToDefault();
        }
    }

    public void savePatches(Patch[] values) {
        if (values.length > 0 && activeBoundaryType != null) {
            for (int i = 0; i < values.length; i++) {
                savePatch(values[i]);
            }
        }
    }

    public void savePatch(Patch patch) {
        patch.setPhisicalType(activeBoundaryType);
        if (panelsByType.containsKey(activeBoundaryType)) {
            panelsByType.get(activeBoundaryType).saveToPatch(patch);
        } else {
            patch.setBoundaryConditions(null);
        }
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

    @Override
    public void materialsChanged() {
        for (BoundaryTypePanel panel : panels) {
            panel.materialsChanged();
        }
    }

    @Override
    public void stateChanged() {
        final Patch[] selectedValues = treeNodeManager.getSelectedValues();
        treeNodeManager.getSelectionHandler().clear();

        ModulesUtil.configureBoundaryConditionsView(modules, this);

        loadTypeField();

        BoundaryConditionsDefaults.updateBoundaryConditionsDefaultsByFields(getModel());

        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (BoundaryTypePanel panel : panels) {
                    panel.stateChanged();
                }

                if (Util.isVarArgsNotNull(selectedValues)) {
                    treeNodeManager.setSelectedValues(selectedValues);
                    treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) selectedValues);
                }
            }
        });
    }
    
    private void loadTypeField() {
        patchTypeField.removeActionListener(selectBoundaryConditionListener);
        patchTypeField.removeAllItems();
        
        Map<String, BoundaryType> typesMap = BoundaryType.getRegisteredBoundaryTypes();
        for (String type : typesMap.keySet()) {
            BoundaryType boundaryType = typesMap.get(type);
            
            String key = boundaryType.getKey();
            String label = boundaryType.getLabel();
            Icon icon = boundaryType.getIcon();
            
            patchTypeField.addItem(key, label, icon);
        }
        patchTypeField.setSelectedIndex(-1);
        patchTypeField.addActionListener(selectBoundaryConditionListener);
    }
}

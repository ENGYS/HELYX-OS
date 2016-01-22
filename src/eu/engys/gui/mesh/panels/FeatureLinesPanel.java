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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.java.dev.designgridlayout.Componentizer;

import com.google.inject.Inject;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.RenameSurfaceEvent;
import eu.engys.gui.mesh.panels.lines.ColorFeatureLineAction;
import eu.engys.gui.mesh.panels.lines.FeatureLinesRefinementTable;
import eu.engys.gui.mesh.panels.lines.ImportFeatureLineAction;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.Util;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class FeatureLinesPanel extends AbstractGUIPanel {

    public static final String LINES = "Lines";
    public static final String COLOR_LABEL = "Color";
    public static final String NAME_LABEL = "Name";
    public static final String REFINE_ONLY_LABEL = "Refine Only";
    public static final String REFINEMENTS_LABEL = "Refinements";
    public static final String REMOVE_LABEL = "Remove";

    private PanelBuilder builder;
    private StringField nameField;
    private JCheckBox refineOnly;
    private JButton colorButton;
    private FeatureLine selectedLine;
    private ColorFeatureLineAction colorLineAction;
    private FeatureLinesRefinementTable refinementLevels;
    private FeatureLinesTreeNodeManager treeNodeManager;

    private PropertyChangeListener renameAction;

    @Inject
    public FeatureLinesPanel(Model model) {
        super(LINES, model);
        this.treeNodeManager = new FeatureLinesTreeNodeManager(model, this);
        model.addObserver(treeNodeManager);
    }

    @Override
    protected JComponent layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        builder.addRight(getButtons());
        builder.addComponent(createFeatureLinesPanel());

        return builder.removeMargins().getPanel();
    }

    private JPanel createFeatureLinesPanel() {
        renameAction = new PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value") && evt.getSource() instanceof StringField) {
                    StringField field = (StringField) evt.getSource();
                    renameSurface(field.getText());
                }
            }
        };
        nameField = ComponentsFactory.stringField();
        nameField.addPropertyChangeListener(renameAction);

        refineOnly = ComponentsFactory.checkField();

        builder = new PanelBuilder();
        builder.addComponent(NAME_LABEL, nameField);
        builder.addComponent(COLOR_LABEL, Componentizer.create().fixedPref(colorButton = new JButton(colorLineAction = new ColorFeatureLineAction(this))).component());
        if (hasRefineOnly()) {
            builder.addComponent(REFINE_ONLY_LABEL, refineOnly);
        }
        builder.addComponent(REFINEMENTS_LABEL, refinementLevels = new FeatureLinesRefinementTable(model, null));
        builder.setEnabled(false);

        return builder.getPanel();
    }

    @Override
    public void load() {
    }

    @Override
    public void save() {
        super.save();
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) treeNodeManager.getSelectedValues());
    }

    private void renameSurface(String newName) {
        if (selectedLine != null) {
            if (model.getGeometry().contains(newName)) {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Name already in use", "Name Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String oldPatchName = selectedLine.getName();
            selectedLine.rename(newName);

            treeNodeManager.refreshNode(selectedLine);

            EventManager.triggerEvent(this, new RenameSurfaceEvent(selectedLine, oldPatchName, selectedLine.getPatchName()));
        }
    }

    private JButton[] getButtons() {
        final JButton fromFile = new JButton(new ImportFeatureLineAction(model));
        fromFile.setName(ImportFeatureLineAction.FROM_FILE_LABEL);
        List<JButton> buttons = new ArrayList<>();
        buttons.add(fromFile);
        return buttons.toArray(new JButton[0]);
    }

    public void selectLine(FeatureLine[] currentSelection) {
        if (Util.isVarArgsNotNull(currentSelection)) {
            StringBuilder sb = new StringBuilder();
            for (FeatureLine line : currentSelection) {
                sb.append(line.getName());
                sb.append(" ");
            }
            nameField.setText(sb.toString());
            if (currentSelection.length == 1) {
                builder.setEnabled(true);
                this.selectedLine = currentSelection[0];

                refineOnly.setSelected(selectedLine.isRefineOnly());

                refinementLevels.setRefinements(selectedLine.getRefinements());
                refinementLevels.load();

                colorLineAction.setCurrentColor(selectedLine.getColor());
                colorButton.setBackground(selectedLine.getColor());
            } else {
                builder.setEnabled(false);
            }
        } else {
            deselectAll();
        }

    }

    public void saveLine(FeatureLine[] currentSelection) {
        if (currentSelection != null && currentSelection.length == 1) {
            FeatureLine line = currentSelection[0];
            line.setRefineOnly(refineOnly.isSelected());
            line.setRefinements(refinementLevels.getRefinements());
        }
    }

    public FeatureLine getSelectedLine() {
        return selectedLine;
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

    public void deselectAll() {
        nameField.setText("");
        builder.setEnabled(false);
    }

    @Override
    public void clear() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) new FeatureLine[0]);
    }

    protected boolean hasRefineOnly() {
        return true;
    }

}

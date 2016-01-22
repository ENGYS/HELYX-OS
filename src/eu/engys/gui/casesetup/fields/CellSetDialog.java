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

package eu.engys.gui.casesetup.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.util.ui.UiUtil;

public class CellSetDialog extends JPanel {

    public static final String CELLSET_PANEL_NAME = "cellset.panel";
    public static final String CELLSET_REM_NAME = "cellset.rem";
    public static final String CELLSET_ADD_NAME = "cellset.add";
    
    private Map<Integer, CellSetRow> rowsMap = new HashMap<>();
    private JDialog dialog;
    private JPanel rowsPanel;
    private JScrollPane scrollPane;
    private DictionaryModel dictionaryModel;
    private final String fieldName;
    private final Model model;
    private final String listName;
    private Component parentComponent;
    private JButton okButton;

    public CellSetDialog(Model model, DictionaryModel dictionaryModel, String fieldName, String listName, Component parentComponent) {
        super(new BorderLayout());
        this.dictionaryModel = dictionaryModel;
        this.fieldName = fieldName;
        this.model = model;
        this.parentComponent = parentComponent;
        this.listName = listName;
        setName(CELLSET_PANEL_NAME);
        layoutComponents();
    }

    private void layoutComponents() {
        JPanel addRemovePanel = new JPanel(new FlowLayout());
        addRemovePanel.setOpaque(false);
        JButton addButton = new JButton(new AddRowAction());
        addButton.setName(CELLSET_ADD_NAME);
        JButton remButton = new JButton(new RemRowAction());
        remButton.setName(CELLSET_REM_NAME);
        addRemovePanel.add(addButton);
        addRemovePanel.add(remButton);

        JPanel okPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDialogClose();
            }

        });
        okButton.setName("OK");
        okPanel.add(okButton);

        rowsPanel = new JPanel(new GridBagLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(rowsPanel, BorderLayout.NORTH);
        centerPanel.add(new JLabel(), BorderLayout.CENTER);

        add(addRemovePanel, BorderLayout.NORTH);
        scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        add(okPanel, BorderLayout.SOUTH);
        layoutDialog();
    }

    public JDialog getDialog() {
        return dialog;
    }

    private void layoutDialog() {
        Window parent = parentComponent == null ? UiUtil.getActiveWindow() : SwingUtilities.getWindowAncestor(parentComponent);
        String title = fieldName.equals(Fields.ALPHA_1) ? fieldName + " [phase 1]" : fieldName;
        dialog = new JDialog(parent, title, ModalityType.MODELESS);
        dialog.setName("cellset.dialog");
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                handleDialogClose();
            }
        });

        dialog.add(this);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getRootPane().setDefaultButton(okButton);
    }

    public void showDialog() {
        load();
        dialog.setVisible(true);
    }

    public void load() {
        Dictionary initialisation = dictionaryModel.getDictionary();
        if (initialisation != null && initialisation.isList(listName)) {
            ListField sources = initialisation.getList(listName);
            for (int i = 0; i < sources.getListElements().size(); i++) {
                Dictionary source = (Dictionary) sources.getListElements().get(i);
                addRow(new CellSetRow(model, fieldName, source, i));
            }
        }
    }

    private void addRow(CellSetRow row) {
        Integer index = rowsMap.size();
        rowsMap.put(index, row);
        rowsPanel.add(row, new GridBagConstraints(0, index, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
        rowsPanel.revalidate();
        EventManager.triggerEvent(this, new AddSurfaceEvent(row.getSurface()));
    }

    private void handleDialogClose() {
        save();
        clear();
        dialog.setVisible(false);
    }

    public void save() {
        ListField regions = new ListField(listName);
        for (Dictionary dict : getDictionaries()) {
            regions.add(dict);
        }
        Dictionary initialisation = dictionaryModel.getDictionary();
        initialisation.remove(listName);
        initialisation.add(new ListField(regions));
    }

    public void clear() {
        for (CellSetRow row : rowsMap.values()) {
            row.clear();
        }
        EventManager.triggerEvent(this, new RemoveSurfaceEvent(getSurfaces().toArray(new Surface[0])));
        rowsPanel.removeAll();
        rowsMap.clear();
    }

    private List<Surface> getSurfaces() {
        List<Surface> surfaces = new ArrayList<>();
        for (CellSetRow row : rowsMap.values()) {
            surfaces.add(row.getSurface());
        }
        return surfaces;
    }

    private List<Dictionary> getDictionaries() {
        List<Dictionary> dicts = new ArrayList<>();
        for (CellSetRow row : rowsMap.values()) {
            dicts.add(row.getDictionary());
        }
        return dicts;
    }

    private void removeRow() {
        CellSetRow row = rowsMap.get(rowsMap.size() - 1);
        rowsPanel.remove(row);
        rowsPanel.revalidate();
        rowsMap.remove(rowsMap.size() - 1);
        EventManager.triggerEvent(this, new RemoveSurfaceEvent(row.getSurface()));
    }

    private class AddRowAction extends AbstractAction {

        public AddRowAction() {
            super("+");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addRow(new CellSetRow(model, fieldName, new Dictionary("boxToCell"), rowsMap.size()));
        }

    }

    private class RemRowAction extends AbstractAction {

        public RemRowAction() {
            super("-");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (rowsMap.size() > 1) {
                removeRow();
            }
        }
    }

    // Test purpose only
    public void setInitialisation(Dictionary initialisation) {
        this.dictionaryModel = new DictionaryModel(initialisation);
    }

    // Test purpose only
    public Dictionary getInitialisation() {
        return dictionaryModel.getDictionary();
    }
}

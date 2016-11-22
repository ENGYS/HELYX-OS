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
package eu.engys.gui.casesetup.fields;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.gui.mesh.actions.AddSTLArea;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.TitledBorderWithAction;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public abstract class CellSetDialog extends JPanel {

    public static final String DIALOG_NAME = "cellset.dialog";
    public static final String CELLSET_PANEL_NAME = "cellset.panel";
    public static final String CELLSET_REM_NAME = "cellset.rem";
    public static final String CELLSET_ADD_NAME = "cellset.add";

    protected final Model model;
    protected final ProgressMonitor monitor;
    private final String title;
    protected Map<String, CellSetRow> rowsMap = new HashMap<>();

    private JDialog dialog;
    private JPanel rowsPanel;
    private JScrollPane scrollPane;
    private JButton okButton;

    public CellSetDialog(Model model, String title, ProgressMonitor monitor) {
        super(new BorderLayout());
        this.model = model;
        this.monitor = monitor;
        this.title = title;
        setName(CELLSET_PANEL_NAME);
        layoutComponents();
    }

    private void layoutComponents() {
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setOpaque(false);
        
        buttonsPanel.add(new JButton(new AddRowAction(Type.STL_AREA)){{ setName(CELLSET_ADD_NAME + ".stl");}});
//        buttonsPanel.add(new JButton(new AddRowAction(Type.IGES)){{ setName(CELLSET_ADD_NAME + ".iges");}});
        buttonsPanel.add(new JButton(new AddRowAction(Type.BOX)){{ setName(CELLSET_ADD_NAME + ".box");}});
        buttonsPanel.add(new JButton(new AddRowAction(Type.SPHERE)){{ setName(CELLSET_ADD_NAME + ".sphere");}});
        buttonsPanel.add(new JButton(new AddRowAction(Type.CYLINDER)){{ setName(CELLSET_ADD_NAME + ".cylinder");}});
//        buttonsPanel.add(new JButton(new AddRowAction(Type.PLANE)){{ setName(CELLSET_ADD_NAME + ".plane");}});
        buttonsPanel.add(new JButton(new AddRowAction(Type.RING)){{ setName(CELLSET_ADD_NAME + ".ring");}});
        
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

        add(buttonsPanel, BorderLayout.NORTH);
        scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        add(okPanel, BorderLayout.SOUTH);
    }

    public void showDialog(WindowListener adapter) {
        load();
        dialog = new JDialog(UiUtil.getActiveWindow(), title, ModalityType.MODELESS);
        dialog.setName(DIALOG_NAME);
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
//        dialog.getRootPane().setDefaultButton(okButton);
//        dialog.addComponentListener(adapter);
        dialog.addWindowListener(adapter);
        dialog.setVisible(true);
    }

    protected abstract CellSetRow newRow(Surface surface);

    protected abstract void load();

    protected abstract void save();

    protected void addRow(CellSetRow row) {
        _addRow(row);
        EventManager.triggerEvent(this, new AddSurfaceEvent(row.getSurface()));
    }
    
    private void _addRow(CellSetRow row) {
        final Surface surface = row.getSurface();
        rowsMap.put(surface.getName(), row);
        //remButton.setName(CELLSET_REM_NAME);
        row.setBorder(new TitledBorderWithAction(row.getSelectedKey(), row, new Runnable() {
            @Override
            public void run() {
                removeRow(surface.getName());
            }
        }));
        rowsPanel.add(row, new GridBagConstraints(0, rowsPanel.getComponentCount(), 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
        rowsPanel.revalidate();
    }

    private void handleDialogClose() {
        save();
        clear();
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
    }

    public void clear() {
        for (CellSetRow row : rowsMap.values()) {
            row.clear();
        }
        EventManager.triggerEvent(this, new RemoveSurfaceEvent(getSurfaces().toArray(new Surface[0])));
        rowsPanel.removeAll();
        rowsMap.clear();
    }

    protected List<Surface> getSurfaces() {
        List<Surface> surfaces = new ArrayList<>();
        for (CellSetRow row : rowsMap.values()) {
            surfaces.add(row.getSurface());
        }
        return surfaces;
    }

    private void removeRow(String surfaceName) {
        CellSetRow row = rowsMap.remove(surfaceName);
        EventManager.triggerEvent(this, new RemoveSurfaceEvent(row.getSurface()));
        
        List<CellSetRow> rows = new ArrayList<>(rowsMap.values());
        rowsMap.clear();
        rowsPanel.removeAll();
        for (int i = 0; i < rows.size(); i++) {
            _addRow(rows.get(i));
        }
        rowsPanel.revalidate();
    }

    private static String getTooltip(Type type) {
        switch (type) {
        case BOX: return ResourcesUtil.getString("mesh.box.tooltip");
        case SPHERE: return ResourcesUtil.getString("mesh.sphere.tooltip");
        case CYLINDER: return ResourcesUtil.getString("mesh.cylinder.tooltip");
        case RING: return ResourcesUtil.getString("mesh.ring.tooltip");
        case STL_AREA: return ResourcesUtil.getString("mesh.stl.tooltip");
        default: return "";
    }
}
    
    private static Icon getIcon(Type type) {
        switch (type) {
            case BOX: return ResourcesUtil.getIcon("mesh.box.icon");
            case SPHERE: return ResourcesUtil.getIcon("mesh.sphere.icon");
            case CYLINDER: return ResourcesUtil.getIcon("mesh.cylinder.icon");
            case RING: return ResourcesUtil.getIcon("mesh.ring.icon");
            case STL_AREA: return ResourcesUtil.getIcon("mesh.stl.icon");
            default: return null;
        }
    }
    
    private class AddRowAction extends ViewAction {
        private Type type;

        public AddRowAction(Type type) {
            super(null, CellSetDialog.getIcon(type), CellSetDialog.getTooltip(type));
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Geometry geometry = model.getGeometry();
            switch (type) {
                case BOX: addRow(newRow(geometry.getFactory().newSurface(Box.class, getAName()))); break;
                case SPHERE: addRow(newRow(geometry.getFactory().newSurface(Sphere.class, getAName()))); break;
                case CYLINDER: addRow(newRow(geometry.getFactory().newSurface(Cylinder.class, getAName()))); break;
                case RING: addRow(newRow(geometry.getFactory().newSurface(Ring.class, getAName()))); break;
                case STL_AREA:
                    new AddSTLArea(model, monitor) {
                        @Override
                        public void postLoad(StlArea stlArea) {
                            addRow(newRow(stlArea));
                        }
                    }.execute();
                    break;
                default: break;
            }
            
        }

        private String getAName() {
            return type + Util.generateID();
        }
    }

}

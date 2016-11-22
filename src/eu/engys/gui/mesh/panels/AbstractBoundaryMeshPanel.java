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
package eu.engys.gui.mesh.panels;

import static eu.engys.core.controller.AbstractController.MESH_CHECK;
import static eu.engys.core.controller.AbstractController.MESH_CHECK_EDIT;
import static eu.engys.core.controller.AbstractController.MESH_CREATE;
import static eu.engys.core.controller.AbstractController.MESH_CREATE_EDIT;
import static eu.engys.core.controller.AbstractController.MESH_DELETE;
import static eu.engys.util.ui.ComponentsFactory.labelField;

import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.ui.builder.PanelBuilder;
import net.java.dev.designgridlayout.Componentizer;

public abstract class  AbstractBoundaryMeshPanel extends AbstractGUIPanel {

    public static final String TITLE = "Mesh";
    
    private BoundaryMeshTreeNodeManager treeNodeManager;

    private JLabel name;
    private JLabel path;
    private JLabel created;
    
    protected PanelBuilder builder;
    private MeshInfoPanel meshInfoPanel;

    public AbstractBoundaryMeshPanel(Model model) {
        super(TITLE, model);
        this.treeNodeManager = new BoundaryMeshTreeNodeManager(model, this);
        model.addObserver(treeNodeManager);
    }

    protected JComponent layoutComponents() {
        Action createMesh = ActionManager.getInstance().get(MESH_CREATE);
        Action editCreateMesh = ActionManager.getInstance().get(MESH_CREATE_EDIT);
        Action checkMesh = ActionManager.getInstance().get(MESH_CHECK);
        Action editCheckMesh = ActionManager.getInstance().get(MESH_CHECK_EDIT);
        Action deleteMesh = ActionManager.getInstance().get(MESH_DELETE);

        JButton runMeshButton = new JButton(createMesh);
        JButton editMeshScriptButton = new JButton(editCreateMesh);
        JButton checkMeshButton = new JButton(checkMesh);
        JButton editCheckMeshButton = new JButton(editCheckMesh);
        JButton deleteMeshButton = new JButton(deleteMesh);

        runMeshButton.setPreferredSize(new Dimension(120, runMeshButton.getPreferredSize().height));
        checkMeshButton.setPreferredSize(new Dimension(120, checkMeshButton.getPreferredSize().height));
        deleteMeshButton.setPreferredSize(new Dimension(120, checkMeshButton.getPreferredSize().height));

        JComponent c1 = Componentizer.create().minToPref(runMeshButton).fixedPref(editMeshScriptButton).minAndMore(new JLabel()).component();
        JComponent c2 = Componentizer.create().minToPref(checkMeshButton).fixedPref(editCheckMeshButton).minAndMore(new JLabel()).component();
        JComponent c3 = Componentizer.create().minToPref(deleteMeshButton).fixedPref(new JLabel()).minAndMore(new JLabel()).component();

        PanelBuilder actions = new PanelBuilder();
        actions.addComponent(c1);
        actions.addComponent(c2);
        actions.addComponent(c3);

        name = labelField("");
        path = labelField("");
        created = labelField("");

        PanelBuilder properties = new PanelBuilder();
        properties.addComponent("Name", name);
        properties.addComponent("Path", path);
        properties.addComponent("Created", created);
        
        meshInfoPanel = new MeshInfoPanel();

        builder = new PanelBuilder();
        builder.addComponent(actions.withTitle("Actions").getPanel());
        builder.addComponent(properties.withTitle("Properties").getPanel());
        addExtraComponents(builder);
        builder.addComponent(meshInfoPanel.getStatistics(), meshInfoPanel.getCellType());
        builder.addComponent(meshInfoPanel.getDataArrays());
        builder.addComponent(meshInfoPanel.getBounds());

        return builder.removeMargins().getPanel();
    }

    protected void addExtraComponents(PanelBuilder builder) {
    }

    @Override
    public void start() {
        load();
    }
    
    @Override
    public void load() {
        name.setText(model.getProject().getBaseDir().getName());
        path.setText(model.getProject().getBaseDir().getParent());

        try {
            Path basePath = model.getProject().getBaseDir().toPath();
            long lastModify = Files.getLastModifiedTime(basePath).toMillis();
            created.setText(DateFormat.getDateInstance().format(new Date(lastModify)));
        } catch (Exception e) {
            created.setText("-");
        }

        meshInfoPanel.load(model.getMesh());
    }

    @Override
    public void save() {
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

}

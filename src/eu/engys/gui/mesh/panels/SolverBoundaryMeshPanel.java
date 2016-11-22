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

import static eu.engys.util.ui.ComponentsFactory.labelField;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JLabel;

import eu.engys.core.project.Model;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.ui.builder.PanelBuilder;

public class SolverBoundaryMeshPanel extends AbstractGUIPanel {

    public static final String TITLE = "Mesh";
    private static final DecimalFormat formatter = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.US));

    private BoundaryMeshTreeNodeManager treeNodeManager;

    private JLabel name;
    private JLabel path;
    private JLabel created;

    private PanelBuilder dataArrays;
    private MeshInfoPanel meshInfoPanel;

    @Inject
    public SolverBoundaryMeshPanel(Model model) {
        super(TITLE, model);
        this.treeNodeManager = new BoundaryMeshTreeNodeManager(model, this);
        model.addObserver(treeNodeManager);
    }

    protected JComponent layoutComponents() {
        name = labelField("");
        path = labelField("");
        created = labelField("");

        meshInfoPanel = new MeshInfoPanel();

        PanelBuilder properties = new PanelBuilder();
        properties.addComponent("Name", name);
        properties.addComponent("Path", path);
        properties.addComponent("Created", created);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(properties.withTitle("Properties").getPanel());
        builder.addComponent(meshInfoPanel.getStatistics(), meshInfoPanel.getCellType());
        builder.addComponent(meshInfoPanel.getDataArrays());
        builder.addComponent(meshInfoPanel.getBounds());
        
        return builder.removeMargins().getPanel();
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
    public void stop() {
        super.stop();
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

}

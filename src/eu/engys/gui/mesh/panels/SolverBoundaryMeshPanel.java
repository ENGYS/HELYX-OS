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

import static eu.engys.util.ui.ComponentsFactory.labelField;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import eu.engys.core.project.Model;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;

public class SolverBoundaryMeshPanel extends AbstractGUIPanel {

    public static final String TITLE = "Mesh";
    private static final DecimalFormat formatter = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.US));

    private BoundaryMeshTreeNodeManager treeNodeManager;

    private JLabel name;
    private JLabel path;
    private JLabel created;
    private JLabel cells;
    private JLabel points;
    private JLabel faces;
    private JTextArea cellsPerLevel;
    private JLabel memory;

    private JLabel xBounds;
    private JLabel yBounds;
    private JLabel zBounds;

    private PanelBuilder dataArrays;

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

        cells = labelField("");
        points = labelField("");
        faces = labelField("");

        cellsPerLevel = ComponentsFactory.labelArea();
        cellsPerLevel.setEditable(false);

        memory = labelField("");

        xBounds = labelField("");
        yBounds = labelField("");
        zBounds = labelField("");

        PanelBuilder properties = new PanelBuilder();
        properties.getPanel().setBorder(BorderFactory.createTitledBorder("Properties"));
        properties.addComponent("Name", name);
        properties.addComponent("Path", path);
        properties.addComponent("Created", created);

        PanelBuilder statistics = new PanelBuilder();
        statistics.getPanel().setBorder(BorderFactory.createTitledBorder("Statistics"));
        statistics.addComponent("Number of Cells", cells);
        statistics.addComponent("Number of Faces", faces);
        statistics.addComponent("Number of Points", points);
        statistics.addComponent("Cells per Refinement Level", cellsPerLevel);
        // statistics.addComponent("Memory [MB]", memory);

        dataArrays = new PanelBuilder();
        dataArrays.getPanel().setBorder(BorderFactory.createTitledBorder("Data Arrays"));

        PanelBuilder bounds = new PanelBuilder();
        bounds.getPanel().setBorder(BorderFactory.createTitledBorder("Bounds"));
        bounds.addComponent("X Range", xBounds);
        bounds.addComponent("Y Range", yBounds);
        bounds.addComponent("Z Range", zBounds);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(properties.getPanel());
        builder.addComponent(statistics.getPanel());
        builder.addComponent(dataArrays.getPanel());
        builder.addComponent(bounds.getPanel());
        return builder.removeMargins().getPanel();
    }
    
    @Override
    public void start() {
        super.start();
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

        if (model.getMesh() != null) {
            cells.setText(formatter.format(model.getMesh().getNumberOfCells()));
            points.setText(formatter.format(model.getMesh().getNumberOfPoints()));
            faces.setText(formatter.format(model.getMesh().getNumberOfFaces()));

            List<Integer> cellsPerRefinementLevel = model.getMesh().getCellsPerRefinementLevel();
            String text = "";
            for (int i = 0; i < cellsPerRefinementLevel.size(); i++) {
                if (i > 0)
                    text += "\n";
                text += i + "\t" + cellsPerRefinementLevel.get(i);
            }
            cellsPerLevel.setText(text);

            // memory.setText(formatter.format(model.getMesh().getMemorySize()/1024D));

            double[] bounds = model.getMesh().getBounds();

            xBounds.setText(getTextForBounds(bounds[0], bounds[1]));
            yBounds.setText(getTextForBounds(bounds[2], bounds[3]));
            zBounds.setText(getTextForBounds(bounds[4], bounds[5]));
        } else {
            cells.setText("-");
            points.setText("-");
            faces.setText("-");
            cellsPerLevel.setText(" - ");
            // memory.setText("-");
            xBounds.setText("[- , -]");
            yBounds.setText("[- , -]");
            zBounds.setText("[- , -]");
        }
    }

    private String getTextForBounds(double min, double max) {
        if (areValid(min, max)) {
            return "[" + formatter.format(min) + " , " + formatter.format(max) + "] (Delta " + formatter.format(max - min) + ")";
        } else {
            return "[0 , 0]";
        }
    }

    private boolean areValid(double min, double max) {
        return min < Double.MAX_VALUE && max > -Double.MAX_VALUE;
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

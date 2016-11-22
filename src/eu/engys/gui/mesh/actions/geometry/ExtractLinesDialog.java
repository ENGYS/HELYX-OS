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
package eu.engys.gui.mesh.actions.geometry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Region;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.BoxEvent;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.util.ui.CheckBoxPanel;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.StringField;
import eu.engys.vtk.actions.ExtractLines;
import vtk.vtkAppendPolyData;
import vtk.vtkPolyData;

public class ExtractLinesDialog {

    private static final Icon ICON_ON = ResourcesUtil.getIcon("light.on.icon");
    private static final Icon ICON_OFF = ResourcesUtil.getIcon("light.off.icon");

    
    public static final String EXTRACT_LINES_DIALOG = "extract.lines.dialog";
    
    public static final String TITLE = "Extract Feature Lines";
    public static final String FEATURE_ANGLE_LABEL = "Feature Angle";
    public static final String SURFACE_LABEL = "Surface";
    public static final String MANIFOLD_EDGES_LABEL = "Manifold Edges";
    public static final String NON_MANIFOLD_EDGES_LABEL = "Non-manifold Edges";
    public static final String BOUNDARY_EDGES_LABEL = "Boundary Edges";
    public static final String OUTSIDE_KEY = "outside";
    public static final String INSIDE_KEY = "inside";

    public static final String MIN_LABEL = "Min";
    public static final String MAX_LABEL = "Max";
    public static final String OUTSIDE_LABEL = "Outside";
    public static final String INSIDE_LABEL = "Inside";
    public static final String APPLY_LABEL = "Apply";
    public static final String SAVE_LABEL = "Save";
    public static final String CANCEL_LABEL = "Cancel";

    public static final String LINE_SUFFIX = "_line";

    private final Model model;

    private JDialog dialog;
    private DoubleField angle;

    private JCheckBox inside;
    private DoubleField[] insideBoxMin;
    private DoubleField[] insideBoxMax;

    private JCheckBox outside;
    private DoubleField[] outsideBoxMin;
    private DoubleField[] outsideBoxMax;
    private StringField surfacesNameField;

    private Window parent;

    private JCheckBox boundaryEdges;
    private JCheckBox nonManifoldEdges;
    private JCheckBox manifoldEdges;

    private JToggleButton showInsideButton;
    private JToggleButton showOutsideButton;

    private Surface surface;

    private FeatureLine line;
    private Runnable onShow;
    private Runnable onHide;
    private Geometry3DController controller3d;

    // public static void main(String[] args) {
    // new HelyxLookAndFeel().init();
    // Model model = new Model();
    // model.init();
    //
    // model.getGeometry().addSurface(new Stl("a"));
    //
    // new ExtractLinesDialog(null, model, null).show(null, null, null);
    // }

    public ExtractLinesDialog(Window parent, Model model, Geometry3DController controller3d) {
        this.parent = parent;
        this.model = model;
        this.controller3d = controller3d;

        layoutComponents();
    }

    private void layoutComponents() {
        surfacesNameField = ComponentsFactory.stringField();
        surfacesNameField.setEnabled(false);

        angle = ComponentsFactory.doubleField(30.0, 0.0, 180.0);
        boundaryEdges = new JCheckBox(BOUNDARY_EDGES_LABEL, true);
        nonManifoldEdges = new JCheckBox(NON_MANIFOLD_EDGES_LABEL, true);
        manifoldEdges = new JCheckBox(MANIFOLD_EDGES_LABEL, true);

        inside = new JCheckBox(INSIDE_LABEL, false);
        insideBoxMin = ComponentsFactory.doublePointField(8, 0.0);
        insideBoxMax = ComponentsFactory.doublePointField(8, 1.0);

        outside = new JCheckBox(OUTSIDE_LABEL, false);
        outsideBoxMin = ComponentsFactory.doublePointField(8, 0.0);
        outsideBoxMax = ComponentsFactory.doublePointField(8, 1.0);

        showInsideButton = getShowBoxButton(insideBoxMin, insideBoxMax);
        PanelBuilder insideBuilder = new PanelBuilder();
        insideBuilder.addComponent(MIN_LABEL, insideBoxMin[0], insideBoxMin[1], insideBoxMin[2], showInsideButton);
        insideBuilder.addComponentAndSpan(MAX_LABEL, insideBoxMax);
        JPanel insidePanel = new CheckBoxPanel(insideBuilder, inside);
        insideBuilder.setEnabled(false);

        PanelBuilder outsideBuilder = new PanelBuilder();
        showOutsideButton = getShowBoxButton(outsideBoxMin, outsideBoxMax);
        outsideBuilder.addComponent(MIN_LABEL, outsideBoxMin[0], outsideBoxMin[1], outsideBoxMin[2], showOutsideButton);
        outsideBuilder.addComponentAndSpan(MAX_LABEL, outsideBoxMax);
        JPanel outsidePanel = new CheckBoxPanel(outsideBuilder, outside);
        outsideBuilder.setEnabled(false);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(SURFACE_LABEL, surfacesNameField);
        builder.addComponent(FEATURE_ANGLE_LABEL, angle);
        builder.addComponent(boundaryEdges);
        builder.addComponent(nonManifoldEdges);
        builder.addComponent(manifoldEdges);
        builder.addComponent(insidePanel);
        builder.addComponent(outsidePanel);
        builder.addFill(new JSeparator());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(new ApplyButton());
        buttonsPanel.add(new SaveButton());
        buttonsPanel.add(new CancelButton());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(builder.getPanel(), BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setNames();

        dialog = new JDialog(parent, ModalityType.MODELESS);
        dialog.setName(EXTRACT_LINES_DIALOG);
        dialog.setTitle(TITLE);
        dialog.setSize(500, 420);
        dialog.getContentPane().add(mainPanel);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
    }

    public void show(final Surface surface, Runnable onShow, Runnable onHide) {
        this.surface = surface;
        this.onShow = onShow;
        this.onHide = onHide;
        if (this.onShow != null) {
            this.onShow.run();
        }
        vtkPolyData dataset = getDatasetFrom(surface);
        double[] bounds = dataset.GetBounds();
        
        insideBoxMin[0].setDoubleValue(bounds[0]);
        insideBoxMin[1].setDoubleValue(bounds[2]);
        insideBoxMin[2].setDoubleValue(bounds[4]);
        
        insideBoxMax[0].setDoubleValue(bounds[1]);
        insideBoxMax[1].setDoubleValue(bounds[3]);
        insideBoxMax[2].setDoubleValue(bounds[5]);

        outsideBoxMin[0].setDoubleValue(bounds[0]);
        outsideBoxMin[1].setDoubleValue(bounds[2]);
        outsideBoxMin[2].setDoubleValue(bounds[4]);
        
        outsideBoxMax[0].setDoubleValue(bounds[1]);
        outsideBoxMax[1].setDoubleValue(bounds[3]);
        outsideBoxMax[2].setDoubleValue(bounds[5]);
        
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                surfacesNameField.setText(surface.getName());
                dialog.setVisible(true);
            }
        });
    }

    private JToggleButton getShowBoxButton(final DoubleField[] min, final DoubleField[] max) {
        final JToggleButton button = new JToggleButton();
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isSelected()) {
                    EventManager.triggerEvent(this, new BoxEvent(min, max, EventActionType.SHOW));
                } else {
                    EventManager.triggerEvent(this, new BoxEvent(min, max, EventActionType.HIDE));
                }
            }
        });
        button.setPreferredSize(new Dimension(36, 36));
        button.setIcon(ICON_OFF);
        button.setSelectedIcon(ICON_ON);
        button.setPressedIcon(ICON_ON);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        return button;
    }

    private void setNames() {
        inside.setName(INSIDE_KEY);
        showInsideButton.setName(INSIDE_KEY + ".show");
        insideBoxMin[0].setName(INSIDE_KEY + MIN_LABEL + "X");
        insideBoxMin[1].setName(INSIDE_KEY + MIN_LABEL + "Y");
        insideBoxMin[2].setName(INSIDE_KEY + MIN_LABEL + "Z");

        insideBoxMax[0].setName(INSIDE_KEY + MAX_LABEL + "X");
        insideBoxMax[1].setName(INSIDE_KEY + MAX_LABEL + "Y");
        insideBoxMax[2].setName(INSIDE_KEY + MAX_LABEL + "Z");

        outside.setName(OUTSIDE_KEY);
        showOutsideButton.setName(OUTSIDE_KEY + ".show");
        outsideBoxMin[0].setName(OUTSIDE_KEY + MIN_LABEL + "X");
        outsideBoxMin[1].setName(OUTSIDE_KEY + MIN_LABEL + "Y");
        outsideBoxMin[2].setName(OUTSIDE_KEY + MIN_LABEL + "Z");

        outsideBoxMax[0].setName(OUTSIDE_KEY + MAX_LABEL + "X");
        outsideBoxMax[1].setName(OUTSIDE_KEY + MAX_LABEL + "Y");
        outsideBoxMax[2].setName(OUTSIDE_KEY + MAX_LABEL + "Z");

    }

    private void doExtract() {
        hideLine();
        line = extractFeatureLines();
        showLine();
    }

    private void showLine() {
        if (line != null) {
            if (controller3d != null) {
                controller3d.addSurfaces(line);
                controller3d.render();
            } else {
                EventManager.triggerEvent(this, new AddSurfaceEvent(false, line));
            }
        }
    }

    private void hideLine() {
        if (line != null) {
            if (controller3d != null) {
                controller3d.removeSurfaces(line);
                controller3d.render();
            } else {
                EventManager.triggerEvent(this, new RemoveSurfaceEvent(false, line));
            }
        }
    }

    private FeatureLine extractFeatureLines() {
        vtkPolyData input = getDatasetFrom(surface);
        if (input != null) {
            ExtractLines extract = new ExtractLines();
            extract.setInput(input);
            extract.setAngle(angle.getDoubleValue());
            extract.setBoundary(boundaryEdges.isSelected());
            extract.setManifold(manifoldEdges.isSelected());
            extract.setNonmanifold(nonManifoldEdges.isSelected());

            if (inside.isSelected()) {
                extract.setInsideMin(new double[] { insideBoxMin[0].getDoubleValue(), insideBoxMin[1].getDoubleValue(), insideBoxMin[2].getDoubleValue() });
                extract.setInsideMax(new double[] { insideBoxMax[0].getDoubleValue(), insideBoxMax[1].getDoubleValue(), insideBoxMax[2].getDoubleValue() });
            }
            if (outside.isSelected()) {
                extract.setOutsideMin(new double[] { outsideBoxMin[0].getDoubleValue(), outsideBoxMin[1].getDoubleValue(), outsideBoxMin[2].getDoubleValue() });
                extract.setOutsideMax(new double[] { outsideBoxMax[0].getDoubleValue(), outsideBoxMax[1].getDoubleValue(), outsideBoxMax[2].getDoubleValue() });
            }

            vtkPolyData output = extract.execute();

            String name = model.getGeometry().getALineName(surface.getName() + LINE_SUFFIX);
            FeatureLine line = new FeatureLine(name);
            line.setModified(true);
            line.setDataSet(output);
            line.setColor(Color.BLUE);

            return line;
        }

        return null;
    }

    private vtkPolyData getDatasetFrom(Surface surface) {
        vtkPolyData dataset = null;

        if (surface != null) {
            if (surface.hasRegions() && surface.getRegions().length > 0) {
                if (surface.isSingleton()) {
                    dataset = surface.getRegions()[0].getTransformedDataSet();
                } else {
                    vtkAppendPolyData append = new vtkAppendPolyData();
                    for (Region region : surface.getRegions()) {
                        append.AddInputData(region.getTransformedDataSet());
                    }
                    append.Update();

                    dataset = append.GetOutput();
                }
            } else {
                dataset = surface.getTransformedDataSet();
            }
        }

        return dataset;
    }

    private void addLine() {
        model.getGeometry().addLine(line);
        model.geometryChanged(line);
    }

    public Component getPanel() {
        return dialog.getContentPane();
    }

    public FeatureLine getFeatureLine() {
        return line;
    }

    private void closeDialog() {
        if (showInsideButton.isSelected()) {
            showInsideButton.doClick();
        }

        if (showOutsideButton.isSelected()) {
            showOutsideButton.doClick();
        }

        if (dialog != null) {
            dialog.dispose();
        }
        if (this.onHide != null) {
            this.onHide.run();
        }
    }

    class ApplyButton extends JButton {
        public ApplyButton() {
            super(new AbstractAction(APPLY_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doExtract();
                }
            });
            setName(APPLY_LABEL);
        }
    }

    class SaveButton extends JButton {
        public SaveButton() {
            super(new AbstractAction(SAVE_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {

                    doExtract();

                    addLine();

                    closeDialog();
                }
            });
            setName(SAVE_LABEL);
        }
    }

    class CancelButton extends JButton {
        public CancelButton() {
            super(new AbstractAction(CANCEL_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {

                    hideLine();

                    closeDialog();
                }
            });
            setName(CANCEL_LABEL);
        }

    }

    /*
     * Utils
     */
    public void showTest(final Surface surface) {
        this.surface = surface;
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                surfacesNameField.setText(surface.getName());
            }
        });
    }

}

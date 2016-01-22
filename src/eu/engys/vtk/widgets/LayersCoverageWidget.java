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

package eu.engys.vtk.widgets;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkLookupTable;
import vtk.vtkPolyData;
import vtk.vtkPolyDataReader;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.Model;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.core.project.mesh.FieldItem.DataType;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.LayerInfo;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.checkboxtree.VisibleItem;
import eu.engys.util.ui.textfields.AdaptativeFormat;
import eu.engys.vtk.VTKColors;
import eu.engys.vtk.VTKRangeCalculator;
import eu.engys.vtk.actors.DefaultActor;

public class LayersCoverageWidget {

    class vtkPolyDataWidget {
        
        private RenderPanel renderPanel;
        private Actor actor;
        private vtkLookupTable lut;

        public vtkPolyDataWidget(RenderPanel renderPanel) {
            this.renderPanel = renderPanel;
            
            vtkPolyDataReader reader = new vtkPolyDataReader();
            reader.ReadAllFieldsOn();
            reader.ReadAllScalarsOn();
            reader.SetFileName(new File(model.getProject().getBaseDir(), LAYER_INFO_VTK).getAbsolutePath());
            reader.Update();
            
            final vtkPolyData layerInfo = reader.GetOutput();
            
            this.actor = new DefaultActor("LayersInfo") {
                {
                    newActor(layerInfo, true);
                }
                @Override
                public VisibleItem getVisibleItem() {
                    return null;
                }
                @Override
                public void setRepresentation(Representation representation) {
                    super.setRepresentation(Representation.SURFACE);
                }
            };
        }
        
        public void update(FieldItem field) {
            new VTKRangeCalculator(field).calculateRange_Automatically_For(actor);
            lut = new vtkLookupTable();
            VTKColors.applyTypeToLookupTable(field, lut);
            this.actor.setScalarColors(lut, field);
        }

        private void On() {
            renderPanel.addActor(actor);
        }
        
        private void Off() {
            renderPanel.removeActor(actor);
        }
        
        public vtkLookupTable getLut() {
            return lut;
        }

        public void Delete() {
            renderPanel.removeActor(actor);
            actor.deleteActor();
            lut.Delete();
        }
    }
    
    private static final String LAYER_INFO_VTK = "layerInfo.vtk";
    private static final Logger logger = LoggerFactory.getLogger(LayersCoverageWidget.class);

    private final Model model;
    private final RenderPanel renderPanel;
    private final ProgressMonitor monitor;

    private vtkPolyDataWidget widget;

    private JPanel colorBar;
    private LayerInfo layerInfo;

    public LayersCoverageWidget(Model model, RenderPanel renderPanel, ProgressMonitor monitor) {
        this.model = model;
        this.renderPanel = renderPanel;
        this.monitor = monitor;
    }

    public void activateLayersCoverage(LayerInfo layerInfo, JPanel colorBar, EventActionType action) {
        renderPanel.lock();
        if (action.equals(EventActionType.HIDE)) {
            if (this.colorBar != null) {
                this.colorBar.removeAll();
            }
            hide();
        } else if (action.equals(EventActionType.SHOW)) {
            if (this.colorBar != null) {
                this.colorBar.removeAll();
            }
            this.colorBar = colorBar;
            this.layerInfo = layerInfo;
            show();
        } else if (action.equals(EventActionType.REMOVE)) {
            clear();
        }
        renderPanel.unlock();
        renderPanel.renderLater();
    }
    
    private void hide() {
        if (widget != null) {
            widget.Off();
        }
    }

    private void show() {
        if (widget == null) {
            createWidget();
        }
        
        updateWidget();
        
        widget.On();
    }
    
    private void updateWidget() {
        FieldItem field = new FieldItem(layerInfo.getKey(), DataType.CELL, -1);
        if (widget != null) {
            widget.update(field);
        }
        if (colorBar != null) {
            colorBar.removeAll();
            colorBar.setLayout(new GridLayout(1, 0));
//            colorBar.setLayout(new FlowLayout(SwingConstants.LEFT, 2, 1));
            double[] range = field.getRange();
            if (layerInfo.isDiscrete()) {
                for (int i = (int) range[0]; i<= (int) range[1]; i++) {
                    colorBar.add(getLabelForValue(i, INT_FORMATTER.format(i)));
                }
            } else {
                colorBar.add(getLabelForValue(range[0], DOUBLE_FORMATTER.format(range[0])));
                colorBar.add(getLabelForValue(range[1], DOUBLE_FORMATTER.format(range[1])));
            }
            colorBar.revalidate();
            colorBar.repaint();
        }
    }

    private static final AdaptativeFormat DOUBLE_FORMATTER = new AdaptativeFormat(new DecimalFormat("0.0##"), new DecimalFormat("0.0##E0"), 3);
    private static final DecimalFormat INT_FORMATTER = new DecimalFormat("0");
    
    private Component getLabelForValue(double value, String text) {
        double[] color = new double[3]; 
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        widget.getLut().GetColor(value, color);
        label.setBackground(VTKColors.toSwing(color));
        label.setForeground(VTKColors.inverse(color));
        return label;
    }

    private void createWidget() {
        widget = new vtkPolyDataWidget(renderPanel);
    }

    public void clear() {
        renderPanel.lock();
        if (widget != null) {
            widget.Delete();
        }
        widget = null;
        renderPanel.unlock();
    }

}

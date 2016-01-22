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

package eu.engys.vtk;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent.Kind;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElement;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.gui.view3D.Controller3D;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.LayerInfo;
import eu.engys.gui.view3D.Mesh3DController;
import eu.engys.gui.view3D.QualityInfo;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Selection;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;

public class VTKEmptyView3D extends JPanel implements CanvasPanel {

    private static final Logger logger = LoggerFactory.getLogger(VTKEmptyView3D.class);
    private Icon engysLogo = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".logo.full");

    private Model model;
    private ProgressMonitor monitor;

    private Mesh3DController meshController;
    private Geometry3DController geometryController;
    private List<Controller3D> controllers;

    private RenderPanel renderPanel;

    @Inject
    public VTKEmptyView3D(Model model, Set<Controller3D> controllers, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
        this.controllers = new ArrayList<>();
        this.renderPanel = new RenderPanelAdapter();

        // this.meshController = new VTKMesh3DController(model, monitor);
        // this.geometryController = new VTKGeometry3DController(model, monitor);

        // registerController(meshController);
        // registerController(geometryController);

        for (Controller3D c : controllers) {
            registerController(c);
        }
    }

    @Override
    public void registerController(Controller3D controller) {
        controller.setRenderPanel(renderPanel);
        controllers.add(controller);
        if (controller instanceof Geometry3DController) {
            this.geometryController = (Geometry3DController) controller;
        } else if (controller instanceof Mesh3DController) {
            this.meshController = (Mesh3DController) controller;
        }
    }

    public RenderPanel getRenderPanel() {
        return renderPanel;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final ImageIcon image = (ImageIcon) engysLogo;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (image != null) {
            int xCoord = (getWidth() / 2) - (image.getIconWidth() / 2);
            int yCoord = (getHeight() / 2) - (image.getIconHeight() / 2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g.drawImage(image.getImage(), xCoord, yCoord, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    // @Override
    // public void handleInitializeFieldsStarted() {
    // }
    //
    // @Override
    // public void handleInitializeFieldsFinished() {
    // }
    //
    // @Override
    // public void start() {
    // }
    //
    // @Override
    // public void stop() {
    // }

    @Override
    public void stop(Class<? extends ViewElement> klass) {

    }

    @Override
    public void start(Class<? extends ViewElement> klass) {

    }

    @Override
    public void save() {
    }

    @Override
    public void load() {
        for (Controller3D context : controllers) {
            logger.info("[LOAD] {}", context.getClass().getSimpleName());
            context.loadActors();
        }
    }

    // @Override
    // public void load(Class<?> klass) {
    // }

    @Override
    public void geometryToMesh(GeometryToMesh g2m) {
        geometryController.clear();
        meshController.clear();
    }

    @Override
    public void clear() {
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public void showBox(DoubleField[] min, DoubleField[] max, EventActionType actions) {
    }

    @Override
    public void showPoint(DoubleField[] point, String key, EventActionType action, Color color) {
    }

    @Override
    public void showPlane(DoubleField[] origin, DoubleField[] normal, EventActionType actions) {
    }

    @Override
    public void showPlaneDisplay(DoubleField[] origin, DoubleField[] normal, EventActionType actions) {
    }

    @Override
    public void showAxis(DoubleField[] origin, DoubleField[] normal, EventActionType actions) {
    }

    @Override
    public void activateSelection(Selection selection, EventActionType action) {
    }

    @Override
    public void showQualityFields(QualityInfo qualityInfo, EventActionType action) {
    }

    @Override
    public void showLayersCoverage(LayerInfo layerInfo, JPanel colorBar, EventActionType action) {
    }

    @Override
    public void layoutComponents() {
    }

    @Override
    public void updateMinAndMaxForFields(String varName, Point3d min, Point3d max) {
    }

    @Override
    public void showMinMaxFieldPoints(String key, Kind kind, boolean visible) {
    }

    @Override
    public Geometry3DController getGeometryController() {
        return geometryController;
    }

    @Override
    public Mesh3DController getMeshController() {
        return meshController;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getController(Class<T> klass) {
        for (Controller3D c : controllers) {
            if (klass.isInstance(c)) {
                return (T) c;
            }
        }
        return null;
    }

    @Override
    public BoundingBox computeBoundingBox(boolean visibleOnly) {
        return VTKUtil.computeBoundingBox(controllers, true);
    }

    @Override
    public boolean showWidget(Widget widget) {
        return false;
    }

    @Override
    public void showWidgetPanel(Widget widget) {
    }

    @Override
    public void hideWidgetPanel(Widget widget) {
    }

    @Override
    public void hideWidget(Widget widget) {
    }

    @Override
    public void resetZoom() {
    }

    @Override
    public void loadWidgets() {
    }

    @Override
    public void applyContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
    }

}

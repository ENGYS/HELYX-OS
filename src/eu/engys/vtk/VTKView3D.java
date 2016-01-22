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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.vecmath.Point3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.View3DEvent;
import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent.Kind;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElement;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.gui.view3D.Controller3D;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.LayerInfo;
import eu.engys.gui.view3D.Mesh3DController;
import eu.engys.gui.view3D.QualityInfo;
import eu.engys.gui.view3D.Selection;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.util.plaf.ILookAndFeel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.vtk.widgets.AxesWidget;
import eu.engys.vtk.widgets.AxisWidgetManager;
import eu.engys.vtk.widgets.ExtractSelectionWidget;
import eu.engys.vtk.widgets.LayersCoverageWidget;
import eu.engys.vtk.widgets.LogoWidget;
import eu.engys.vtk.widgets.MinMaxPointWidgetManager;
import eu.engys.vtk.widgets.PlaneDisplayWidget;
import eu.engys.vtk.widgets.PlaneWidget;
import eu.engys.vtk.widgets.PointWidgetManager;
import eu.engys.vtk.widgets.QualityWidget;
import eu.engys.vtk.widgets.panels.BoundingBoxBar;
import eu.engys.vtk.widgets.shapes.BoxWidget;

public class VTKView3D extends JPanel implements CanvasPanel {

    private static Logger logger = LoggerFactory.getLogger(CanvasPanel.class);

    private Set<ViewElement> viewElements;
    private Set<View3DElement> view3DElements;
    private Map<Class<? extends ViewElement>, View3DElement> elementsByClass = new HashMap<>();
    private Map<String, View3DElement> elementsByTitle = new HashMap<>();

    private final Model model;
    private final Set<Widget> widgets;
    private final VTKRenderPanel renderPanel;
    private final VTKView3DController view3DController;

    private final List<Controller3D> controllers;

    private VTK3DActionsToolBar genericToolBar;
    
    private PointWidgetManager pointWidgetManager;
    private AxisWidgetManager axisWidgetManager;
    private MinMaxPointWidgetManager minMaxPointWidgetManager;
    private AxesWidget axesWidget;
//    private CORWidget corWidget;
    private BoxWidget boxWidget;
    private PlaneWidget planeWidget;
    private PlaneDisplayWidget planeDisplayWidget;
    private ExtractSelectionWidget selectionWidget;
    private QualityWidget qualityWidget;
    private LayersCoverageWidget layersWidget;
    private LogoWidget logoWidget;

    private WidgetToolBar widgetToolBar;
    private BoundingBoxBar boundingBoxBar;
    private JPanel southPanel;
    private WidgetPanel widgetPanel;

    private Mesh3DController meshController;
    private Geometry3DController geometryController;

    private ILookAndFeel laf;
    private ProgressMonitor monitor;

    @Inject
    public VTKView3D(Model model, ILookAndFeel laf, Set<ViewElement> viewElements, Set<Controller3D> controllers, Set<Widget> widgets, ProgressMonitor monitor) {
		super();
		this.model = model;
		this.laf = laf;
        this.viewElements = viewElements;
        this.view3DElements = new LinkedHashSet<>();
		this.widgets = widgets;
		this.monitor = monitor;
		this.renderPanel = new VTKRenderPanel(laf);
		this.view3DController = new VTKView3DController(this);
		this.controllers = new ArrayList<>();
		
//        this.meshController = new VTKMesh3DController(model, monitor);
//        this.geometryController = new VTKGeometry3DController(model, monitor);
//        
//        registerController(meshController);
//        registerController(geometryController);

        for (Controller3D c : controllers) {
            registerController(c);
        }

        EventManager.registerEventListener(new HelyxView3DEventListener(this), View3DEvent.class);
    }

    @Override
    public void registerController(Controller3D controller) {
        controller.setRenderPanel(renderPanel);
        controllers.add(controller);
        if (this.geometryController == null && controller instanceof Geometry3DController) {
            this.geometryController = (Geometry3DController) controller;
        } else if (this.meshController == null && controller instanceof Mesh3DController) {
            this.meshController = (Mesh3DController) controller;
        }  
    }

    @Override
    public void layoutComponents() {
        setLayout(new BorderLayout());

        for (ViewElement element : viewElements) {
            layoutElements(element);
        }
        
        this.genericToolBar = new VTK3DActionsToolBar(model, laf);
        this.widgetToolBar = new WidgetToolBar(widgets);
        this.boundingBoxBar = new BoundingBoxBar(this, laf);

        this.southPanel = new JPanel(new BorderLayout());
        this.southPanel.add(boundingBoxBar, BorderLayout.SOUTH);

        add(genericToolBar, BorderLayout.EAST);
        if (widgetToolBar.hasWidgets()) {
            add(widgetToolBar, BorderLayout.NORTH);
        }

        add(renderPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        initWidgets();

        renderPanel.GetRenderer().AddObserver("EndEvent", this, "handleEndRendering");
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                logoWidget.update(renderPanel.getSize());
            }
        });
    }

    private void layoutElements(ViewElement element) {
        View3DElement view3d = element.getView3D();
        view3d.install(this);
        view3DElements.add(view3d);
        elementsByClass.put(element.getClass(), view3d);
        elementsByTitle.put(element.getTitle(), view3d);
    }

    void handleEndRendering() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                // double timeInSeconds = vtkRendererPanel.GetRenderer().GetLastRenderTimeInSeconds();
                // double fps = 1.0 / timeInSeconds;
                // System.out.println("FPS " + fps);
                BoundingBox bb = boundingBoxBar.update();
//                corWidget.update(bb);
            }
        });
    }

    private void initWidgets() {
        for (Widget widget : widgets) {
            widget.populate(this);
        }
        this.widgetPanel = new WidgetPanel(widgets);
        this.axesWidget = new AxesWidget(renderPanel);
        this.logoWidget = new LogoWidget(renderPanel);
//        this.corWidget = new CORWidget(renderPanel);
        this.planeWidget = new PlaneWidget(renderPanel);
        this.planeDisplayWidget = new PlaneDisplayWidget(renderPanel);
        this.selectionWidget = new ExtractSelectionWidget(renderPanel, monitor);
        this.qualityWidget = new QualityWidget(model, renderPanel, monitor);
        this.layersWidget = new LayersCoverageWidget(model, renderPanel, monitor);
        this.boxWidget = new BoxWidget(renderPanel);
        this.pointWidgetManager = new PointWidgetManager(renderPanel);
        this.axisWidgetManager = new AxisWidgetManager(renderPanel);
        this.minMaxPointWidgetManager = new MinMaxPointWidgetManager(renderPanel);
    }

//    @Override
//    public void start() {
//        widgetPanel.clear();
//        vtkRendererPanel.resetZoomLater();
//    }

    @Override
    public void load() {
        for (Controller3D context : controllers) {
            logger.info("[LOAD 3D] {}", context.getClass().getSimpleName());
            context.loadActors();
        }
        
        for (View3DElement element : view3DElements) {
            logger.info("[LOAD 3D] {}", element.getClass().getSimpleName());
            _load3D(element);
        }

        logger.info("[LOAD] 3D Widgets");
        loadWidgets();
    }
    
    private void _load3D(final View3DElement view3DElement) {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                view3DElement.load(VTKView3D.this);
            }
        });
    }

    @Override
    public void save() {
//        if (view3DElement == currentElement) {
//            view3DElement.save(this);
//        }
    }
    
    @Override
    public void stop(Class<? extends ViewElement> klass) {
        stopWidgets();
        if (elementsByClass.containsKey(klass)) {
            final View3DElement view3DElement = elementsByClass.get(klass);
            ExecUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    view3DElement.stop(VTKView3D.this);
                    view3DElement.save(VTKView3D.this);
                }
            });        
        }
    }

    @Override
    public void start(Class<? extends ViewElement> klass) {
        if (klass == null) { 
            _start(view3DElements.iterator().next());
            resetZoom();
        } else if (elementsByClass.containsKey(klass)) {
            _start(elementsByClass.get(klass));        
        }
    }

    private void _start(final View3DElement view3DElement) {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                view3DElement.start(VTKView3D.this);
            }
        });
        renderPanel.renderLater();
    }

    @Override
    public void resetZoom() {
        renderPanel.resetZoomAndWait();
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
    
    public VTKRenderPanel getVTKRendererPanel() {
        return renderPanel;
    }

    @Override
    public BoundingBox computeBoundingBox(boolean visibleOnly) {
        return VTKUtil.computeBoundingBox(controllers, visibleOnly);
    }

    @Override
    public void geometryToMesh(GeometryToMesh g2m) {
        renderPanel.clearSelection();
        
        for (Controller3D controller : controllers) {
            controller.geometryToMesh(g2m);
        }
        
        for (View3DElement element : view3DElements) {
            _load3D(element);
        }
    }

    @Override
    public void clear() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                renderPanel.lock();
                _clear();
                renderPanel.unlock();
            }
        });
    }

    private void _clear() {
        logger.info("[CLEAR] ");
        renderPanel.clear();
        widgetPanel.clear();
        widgetToolBar.clear();

        planeWidget.clear();
        planeDisplayWidget.clear();
        selectionWidget.clear();
        qualityWidget.clear();
        layersWidget.clear();
        pointWidgetManager.clear();
        axisWidgetManager.clear();
        minMaxPointWidgetManager.clear();

        genericToolBar.clear();

        for (Widget widget : widgets) {
            widget.clear();
        }
        axesWidget.clear();
//        corWidget.clear();

        for (Controller3D context : controllers) {
            context.clearContext();
            context.clear();
        }

        view3DController.setProjection(false);
        VTKUtil.gc(true);
    }

    public Dimension getMinimumSize() {
        return new Dimension(50, 50);
    }

    @Override
    public JPanel getPanel() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("", this);
        UiUtil.setOneTabHide(tabbedPane);
        final JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(23, 0, 0, 0));
        container.add(tabbedPane, BorderLayout.CENTER);
        return container;
    }

    @Override
    public void showPoint(DoubleField[] point, String key, EventActionType action, Color color) {
        pointWidgetManager.showPoint(point, key, action, color);
    }

    @Override
    public void showAxis(DoubleField[] origin, DoubleField[] normal, EventActionType action) {
        axisWidgetManager.showPoint(origin, normal, action);
    }

    @Override
    public void showPlane(DoubleField[] origin, DoubleField[] normal, EventActionType action) {
        BoundingBox bb = computeBoundingBox(true);
        double diagonal = bb.getDiagonal() / 2;
        double value = Double.isInfinite(diagonal) ? 1 : diagonal > 0 ? diagonal : 1;
        planeWidget.showPlane(origin, normal, action, value);
    }

    @Override
    public void showPlaneDisplay(DoubleField[] origin, DoubleField[] normal, EventActionType action) {
        BoundingBox bb = computeBoundingBox(true);
        double diagonal = bb.getDiagonal() / 2;
        double value = Double.isInfinite(diagonal) ? 1 : diagonal > 0 ? diagonal : 1;
        planeDisplayWidget.showPlane(origin, normal, action, value);
    }

    @Override
    public void activateSelection(Selection selection, EventActionType action) {
        selectionWidget.activateSelection(selection, action);
    }

    @Override
    public void showBox(DoubleField[] min, DoubleField[] max, EventActionType action) {
        boxWidget.showBox(null, min, max, action);
    }

    @Override
    public void showMinMaxFieldPoints(String key, Kind kind, boolean visible) {
        minMaxPointWidgetManager.setPointsVisible(key, kind, visible);
    }

    @Override
    public void updateMinAndMaxForFields(String varName, Point3d min, Point3d max) {
        minMaxPointWidgetManager.updateCoordinates(min, max, varName);
    }

    @Override
    public void showQualityFields(QualityInfo qualityInfo, EventActionType action) {
        qualityWidget.activateQualityField(qualityInfo, action);
    }

    @Override
    public void showLayersCoverage(LayerInfo layerInfo, JPanel colorBar, EventActionType action) {
        layersWidget.activateLayersCoverage(layerInfo, colorBar, action);
    }
    
    @Override
    public boolean showWidget(Widget widget) {
        if (widget.canShow()) {
            widget.show();
            if (widget.getWidgetComponent() != null) {
                showWidgetPanel(widget);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showWidgetPanel(Widget widget) {
        if (widgetPanel.isHidden()) {
            southPanel.add(widgetPanel, BorderLayout.CENTER);
            widgetPanel.setHidden(false);
        }
        widgetPanel.showPanel(widget.getWidgetComponent().getKey());
        southPanel.revalidate();
    }

    @Override
    public void hideWidget(Widget widget) {
        widget.hide();
        if (widget.getWidgetComponent() != null) {
            hideWidgetPanel(widget);
        }
    }

    @Override
    public void hideWidgetPanel(Widget widget) {
        widgetPanel.hidePanel(widget.getWidgetComponent().getKey());
        if (widgetPanel.isEmpty()) {
            widgetPanel.setHidden(true);
            southPanel.remove(widgetPanel);
        }
        southPanel.revalidate();
    }
    
//    @Override
//    public void handleInitializeFieldsStarted(){
//    	for (Widget widget : widgets) {
//    		widget.handleInitializeFieldsStarted();
//        }
//    }
//
//    @Override
//    public void handleInitializeFieldsFinished(){
//    	for (Widget widget : widgets) {
//    		widget.handleInitializeFieldsFinished();
//    	}
//    }

    public void stopWidgets() {
        for (Widget widget : widgets) {
            widget.stop();
        }
    }

    public void loadWidgets() {
//        corWidget.on();
        for (Widget widget : widgets) {
            widget.load();
        }
    }

    public void updateWidgets_fieldChanged() {
        for (Widget widget : widgets) {
            widget.handleFieldChanged();
        }
    }

    public void updateWidgets_timeStepChanged() {
        for (Widget widget : widgets) {
            widget.handleTimeStepChanged();
        }
    }
    
    public void updateWidgets_newTimeStep() {
        for (Widget widget : widgets) {
            widget.handleNewTimeStepsRead();
        }
    }

    public VTKView3DController getVTKController() {
        return view3DController;
    }

    public Model getModel() {
        return model;
    }

    public void dumpContext(Class<? extends View3DElement> klass) {
        for (Controller3D c : controllers) {
            c.dumpContext(klass);
        }
    }

    public void applyContext(Class<? extends View3DElement> klass) {
        for (Controller3D c : controllers) {
            c.applyContext(klass);
        }
        genericToolBar.update(controllers);
        for (Widget widget : widgets) {
            widget.applyContext();
        }
    }

}

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
package eu.engys.gui.view3D;

import java.awt.Color;

import javax.swing.JPanel;
import javax.vecmath.Point3d;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.events.view3D.VolumeReportVisibilityEvent.Kind;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElement;
import eu.engys.gui.view3D.quality.QualityInfo;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.util.ui.textfields.DoubleField;

public interface CanvasPanel {

	// public void start();
	// public void stop();
	// public void load();
	public void start(Class<? extends ViewElement> klass);

	public void stop(Class<? extends ViewElement> klass);

	public void load(boolean loadMesh);

	public void save();

	public void clear();

	public void geometryToMesh(GeometryToMesh g2m);

	public JPanel getPanel();

	public void showBox(DoubleField[] min, DoubleField[] max, EventActionType actions);

	public void showRotatedBox(DoubleField[] center, DoubleField[] delta, DoubleField[] rotation, EventActionType actions);

	public void showPoint(String key, DoubleField[] point, EventActionType action, Color color);

	public void showPlane(String key, DoubleField[] origin, DoubleField[] normal, EventActionType actions);

	public void showPlaneDisplay(String key, DoubleField[] origin, DoubleField[] normal, EventActionType actions);

	public void showAxis(DoubleField[] origin, DoubleField[] normal, double magnitude, EventActionType actions);

	public void showAxis(DoubleField[] origin, DoubleField angle1, DoubleField angle2, double magnitude, int sign, EventActionType actions);

//	public void activateCOR(EventActionType action);

//	public void activateLocation(EventActionType action);

	public void activateSelection(Selection selection, EventActionType action);

	public void showQualityFields(QualityInfo qualityInfo, EventActionType action);

	public void showLayersCoverage(LayerInfo layerInfo, JPanel colorBar, EventActionType action);

	public void layoutComponents();

	public void updateMinAndMaxForFields(String varName, Point3d min, Point3d max);

	public void showMinMaxFieldPoints(String key, Kind kind, boolean visible);

	public Geometry3DController getGeometryController();

	public Mesh3DController getMeshController();

	public <T> T getController(Class<T> klass);

	public BoundingBox computeBoundingBox(boolean visibleOnly);

	public void showWidgetPanel(Widget widget);

	public void hideWidgetPanel(Widget widget);

	public boolean showWidget(Widget widget);

	public void hideWidget(Widget widget);

	public void resetZoom();

	public void loadWidgets();

	void registerController(Controller3D context);

	public void applyContext(Class<? extends View3DElement> klass);

	public void dumpContext(Class<? extends View3DElement> klass);
	
	public RenderPanel getRenderPanel();

}

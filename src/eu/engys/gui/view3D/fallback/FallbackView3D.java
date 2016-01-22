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


package eu.engys.gui.view3D.fallback;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.vecmath.Point3d;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.patches.Patch;
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
import eu.engys.util.ApplicationInfo;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;

public class FallbackView3D implements CanvasPanel {

    private JPanel panel;
    private Icon engysLogo = ResourcesUtil.getIcon(ApplicationInfo.getVendor().toLowerCase() + ".logo.full");

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            final ImageIcon image = (ImageIcon) engysLogo;
            panel = new JPanel() {
                @Override
                public void paintComponent(final Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (image != null) {
                        // int xCoord = 30;
                        // int yCoord = getHeight() - image.getIconHeight() -
                        // 30;
                        int xCoord = (getWidth() / 2) - (image.getIconWidth() / 2);
                        int yCoord = (getHeight() / 2) - (image.getIconHeight() / 2);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        g.drawImage(image.getImage(), xCoord, yCoord, null);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    }
                }
            };
            panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            panel.add(new JLabel("3D graphics not available"));
        }
    };

    @Override
    public void registerController(Controller3D context) {
    }

    @Override
    public void layoutComponents() {
    }

    @Override
    public void load() {
    }

    @Override
    public void save() {
    }

    @Override
    public void start(Class<? extends ViewElement> klass) {
    }
    
    @Override
    public void stop(Class<? extends ViewElement> klass) {
    }
    
    @Override
    public void loadWidgets() {
    }

    @Override
    public BoundingBox computeBoundingBox(boolean visibleOnly) {
        return new BoundingBox();
    }

    public void updatePatchesSelection(Patch[] selection) {
    }

    public void updatePatchVisibility(Patch selection, boolean b) {
    }

    public void updateCellZonesSelection(CellZone[] selection) {
    }

    public void updateCellZoneVisibility(CellZone selection, boolean b) {
    }

    @Override
    public void clear() {
    }

    public Dimension getMinimumSize() {
        return new Dimension(50, 50);
    }
    
    @Override
    public JPanel getPanel() {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return panel;
    }

    @Override
    public void showPoint(DoubleField[] point, String key, EventActionType action, Color color) {
    }
    
    @Override
    public void showBox(DoubleField[] min, DoubleField[] max, EventActionType actions) {
    }

    @Override
    public void updateMinAndMaxForFields(String varName, Point3d min, Point3d max) {
    }

    @Override
    public void showMinMaxFieldPoints(String key, Kind kind, boolean visible) {
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
    public void geometryToMesh(GeometryToMesh g2m) {
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
    public Geometry3DController getGeometryController() {
        return new FallbackGeometry3DController();
    }

    @Override
    public Mesh3DController getMeshController() {
        return new FallbackMesh3DController();
    }

    @Override
    public <T> T getController(Class<T> klass) {
        return null;
    }
    
    @Override
    public void resetZoom() {
    }

    @Override
    public void showPlane(DoubleField[] origin, DoubleField[] normal, EventActionType action) {
    }

    @Override
    public void showPlaneDisplay(DoubleField[] origin, DoubleField[] normal, EventActionType actions) {
    }

    @Override
    public void showAxis(DoubleField[] origin, DoubleField[] normal, EventActionType actions) {
    }

    @Override
    public void applyContext(Class<? extends View3DElement> klass) {
    }

    @Override
    public void dumpContext(Class<? extends View3DElement> klass) {
    }

}

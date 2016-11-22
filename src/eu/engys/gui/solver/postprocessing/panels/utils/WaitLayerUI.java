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
package eu.engys.gui.solver.postprocessing.panels.utils;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.LayerUI;

import eu.engys.util.ui.ResourcesUtil;

public class WaitLayerUI extends LayerUI<JPanel> implements ActionListener {

    private Timer timer;
    private int angle;
    private UiState state;
    private Runnable runnable;
    private boolean mouseOver;

    private static final int FPS = 24;
    private static final int TICK = 1000 / FPS;
    private static final String PROPERTY_NAME = "tick";

    private static final Icon REFRESH_ICON_GRAY = ResourcesUtil.getIcon("chart.refresh.gray.icon");
    private static final Icon REFRESH_ICON_WHITE = ResourcesUtil.getIcon("chart.refresh.white.icon");

    public WaitLayerUI(Runnable runnable) {
        this.runnable = runnable;
        init();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    public void init() {
        changeState(UiState.SHOW_REFRESH);
    }

    public void start() {
        if (state.isShowingRefreshIcon()) {
            timer = new Timer(TICK, this);
            timer.start();
            changeState(UiState.SHOW_WHEEL);
        }
    }

    public void stop() {
        if (state.isShowingRefreshIcon() || state.isShowingWheel()) {
            changeState(UiState.SHOW_NOTHING);
            if (timer != null) {
                timer.stop();
            }
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        switch (state) {
        case SHOW_REFRESH:
            paintRefreshIcon(g, c);
            break;
        case SHOW_WHEEL:
            paintLoadingWheel(g, c);
            break;
        case SHOW_NOTHING:
            break;
        default:
            break;
        }
    }

    private void paintRefreshIcon(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        paintBackgroundPanel(g2, c);
        paintBackgroundButton(g2, c);
        paintRefreshIcon(g2, c);
        g2.dispose();
    }

    private void paintLoadingWheel(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        paintBackgroundPanel(g2, c);
        paintWheel(g2, c);
        g2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state.isShowingWheel()) {
            repaintLayer();
            angle += 3;
            if (angle >= 360) {
                angle = 0;
            }
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent event, JLayer layer) {
        if (event.getID() == MouseEvent.MOUSE_RELEASED && isOnRefreshButton(layer, event)) {
            if (state.isShowingRefreshIcon()) {
                runnable.run();
            }
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent event, JLayer layer) {
        if (event.getID() == MouseEvent.MOUSE_MOVED) {
            if (isOnRefreshButton(layer, event)) {
                mouseOver = true;
            } else {
                mouseOver = false;
            }
            repaintLayer();
        }
    }

    public boolean isOnRefreshButton(JLayer layer, MouseEvent event) {
        ImageIcon imageIcon = (ImageIcon) REFRESH_ICON_GRAY;
        int x = (layer.getWidth() - imageIcon.getIconWidth()) / 2;
        int y = (layer.getHeight() - imageIcon.getIconHeight()) / 2;
        int w = imageIcon.getIconWidth();
        int h = imageIcon.getIconHeight();
        Rectangle2D refreshIconBounds = new Rectangle2D.Double(x, y, w, h);
        Point mousePoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), layer);
        return refreshIconBounds.contains(mousePoint);
    }

    private void changeState(UiState state) {
        this.state = state;
        repaintLayer();
    }

    private void repaintLayer() {
        firePropertyChange(PROPERTY_NAME, 0, 1);
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent event, JLayer layer) {
        if (PROPERTY_NAME.equals(event.getPropertyName())) {
            layer.repaint();
        }
    }

    /*
     * Paint
     */
    private void paintRefreshIcon(Graphics2D g2, JComponent c) {
        ImageIcon imageGray = (ImageIcon) REFRESH_ICON_GRAY;
        ImageIcon imageWhite = (ImageIcon) REFRESH_ICON_WHITE;
        int x = ((c.getWidth() - imageGray.getIconWidth()) / 2);
        int y = ((c.getHeight() - imageGray.getIconHeight()) / 2);

        if (mouseOver) {
            g2.drawImage(imageWhite.getImage(), x, y, null);
        } else {
            g2.drawImage(imageGray.getImage(), x, y, null);
        }
    }

    private void paintWheel(Graphics2D g2, JComponent c) {
        int cx = c.getWidth() / 2;
        int cy = c.getHeight() / 2;
        int stroke = 3;
        int linesSize = 9;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(stroke, CAP_ROUND, JOIN_ROUND));
        g2.setPaint(Color.GRAY.darker());
        g2.rotate(Math.PI * angle / 180, cx, cy);
        for (int i = 0; i < 12; i++) {
            float scale = (11.0f - (float) i) / 11.0f;
            g2.drawLine(cx + linesSize, cy, cx + linesSize * 2, cy);
            g2.rotate(-Math.PI / 6, cx, cy);
            g2.setComposite(AlphaComposite.getInstance(SRC_OVER, scale));
        }
    }

    private void paintBackgroundButton(Graphics2D g2, JComponent c) {
        ImageIcon image = (ImageIcon) REFRESH_ICON_GRAY;
        int padding = 8;
        int x = ((c.getWidth() - image.getIconWidth()) / 2);
        int y = ((c.getHeight() - image.getIconHeight()) / 2);
        int w = image.getIconWidth();
        int h = image.getIconHeight();
        int roundAngle = image.getIconWidth() / 2;
        int grayIntensity = 100;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(grayIntensity, grayIntensity, grayIntensity));
        g2.fillRoundRect(x - padding, y - padding, w + (padding * 2), h + (padding * 2), roundAngle, roundAngle);
    }

    private void paintBackgroundPanel(Graphics2D g2, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();

        // float alpha = 1.0f;
        float alpha = 0.6f;
        Color color = new JPanel().getBackground();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Composite backupComposite = g2.getComposite();
        g2.setColor(color);
        g2.setComposite(AlphaComposite.getInstance(SRC_OVER, alpha));
        g2.fillRect(0, 0, w, h);
        g2.setComposite(backupComposite);
    }

    private enum UiState {
        SHOW_REFRESH, SHOW_WHEEL, SHOW_NOTHING;

        public boolean isShowingRefreshIcon() {
            return this == SHOW_REFRESH;
        }

        public boolean isShowingWheel() {
            return this == SHOW_WHEEL;
        }

        public boolean isShowingNothing() {
            return this == SHOW_NOTHING;
        }
    }

}

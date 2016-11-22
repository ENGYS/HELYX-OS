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
package eu.engys.util.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class TitledBorderWithAction extends TitledBorder implements MouseListener, MouseMotionListener {

    public static final String EMPTY_TITLE = " ";
    private static final ImageIcon ICON_BLACK = (ImageIcon) ResourcesUtil.getIcon("delete.icon");
    private static final ImageIcon ICON_RED = (ImageIcon) ResourcesUtil.getIcon("uncheck.icon");
    // private static final ImageIcon ICON = (ImageIcon) ResourcesUtil.getIcon("table.delete.row.icon");

    private Runnable action;
    private JComponent parent;
    private int labelY;
    private int labelH;
    private int labelX;
    private int labelW;
    private CrossButton label;

    public TitledBorderWithAction(String text, JComponent parent, Runnable action) {
        super(text);
        this.parent = parent;
        this.action = action;
        parent.addMouseListener(this);
        parent.addMouseMotionListener(this);
        this.label = new CrossButton();
    }

    public TitledBorderWithAction(JComponent parent, Runnable action) {
        this(EMPTY_TITLE, parent, action);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);

        Border border = getBorder();
        String title = getTitle();
        if ((title != null) && !title.isEmpty()) {
            int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;
            Dimension size = label.getPreferredSize();
            Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));

            labelY = y;
            labelH = size.height;

            insets.left = 0;
            insets.right = 0;
            insets.left += edge + TEXT_INSET_H;
            insets.right += edge + TEXT_INSET_H;

            labelX = x;
            labelW = width - insets.left - insets.right;
            if (labelW > size.width) {
                labelW = size.width;
            }

            labelX += width - insets.right - labelW;
            // labelY -= labelH - insets.top;

            g.translate(labelX, labelY);
            label.setSize(labelW, labelH);
            label.paint(g);
            g.translate(-labelX, -labelY);
        }
    }

    private static Insets getBorderInsets(Border border, Component c, Insets insets) {
        if (border == null) {
            insets.set(0, 0, 0, 0);
        } else if (border instanceof AbstractBorder) {
            AbstractBorder ab = (AbstractBorder) border;
            insets = ab.getBorderInsets(c, insets);
        } else {
            Insets i = border.getBorderInsets(c);
            insets.set(i.top, i.left, i.bottom, i.right);
        }
        return insets;
    }

    class CrossButton extends JLabel {
        private boolean rollover;

        public CrossButton() {
            super("M");
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(rollover ? ICON_RED.getImage() : ICON_BLACK.getImage(), 0, 0, null);
        }

        public void setRollover(boolean rollover) {
            this.rollover = rollover;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Rectangle2D rect = new Rectangle2D.Double(labelX, labelY, labelW, labelH);
        if (rect.contains(e.getX(), e.getY())) {
            action.run();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        checkCross(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        checkCross(e);
    }

    private void checkCross(MouseEvent e) {
        Rectangle2D rect = new Rectangle2D.Double(labelX, labelY, labelW, labelH);
        if (rect.contains(e.getX(), e.getY())) {
            label.setRollover(true);
        } else {
            label.setRollover(false);
        }
        parent.repaint();
    }

}

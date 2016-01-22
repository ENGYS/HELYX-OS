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
package eu.engys.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

public class MemoryWidget extends JComponent implements ActionListener {

    public static final String PROTOTYPE_STRING = " 9999 / 9999 MB ";

    private final LineMetrics lm;
    private final Color progressForeground = new JTable().getSelectionForeground();
    private final Color progressBackground = new JTable().getSelectionBackground();

    private Timer timer;
    
    private long free = Runtime.getRuntime().freeMemory();
    private long total = Runtime.getRuntime().totalMemory();
    private long max = Runtime.getRuntime().maxMemory();

    class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                System.gc();
                repaint();
            }
        }
    }
    
    public MemoryWidget() {
        Font font = new JLabel().getFont();
        setFont(font);

        FontRenderContext frc = new FontRenderContext(null, false, false);
        Rectangle2D bounds = font.getStringBounds(PROTOTYPE_STRING, frc);
        Dimension dim = new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
        setPreferredSize(dim);
        setMaximumSize(dim);
        lm = font.getLineMetrics(PROTOTYPE_STRING, frc);

        setForeground(new JLabel().getForeground());
        setBackground(new JLabel().getBackground());

//        progressForeground = jEdit.getColorProperty("view.status.memory.foreground");
//        progressBackground = jEdit.getColorProperty("view.status.memory.background");

        addMouseListener(new MouseHandler());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer = new Timer(2000, this);
        timer.start();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    public void removeNotify() {
        timer.stop();
        ToolTipManager.sharedInstance().unregisterComponent(this);
        super.removeNotify();
    }

    /**
     * see specification at http://stackoverflow.com/a/18375641
     */
    public void actionPerformed(ActionEvent evt) {
        Runtime runtime = Runtime.getRuntime();
        this.free = runtime.freeMemory();
        this.total = runtime.totalMemory();
        this.max = runtime.maxMemory();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Insets insets = new Insets(0, 0, 0, 0);// MemoryStatus.this.getBorder().getBorderInsets(this);

        long used = total - free;

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom - 1;

        float fraction = ((float) used) / max;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(progressBackground);
        g2.fillRect(insets.left, insets.top, (int) (width * fraction), height);

        String str = (used / 1024 / 1024) + " / " + (max / 1024 / 1024) + " MB";
        FontRenderContext frc = new FontRenderContext(null, false, false);
        Rectangle2D bounds = g2.getFont().getStringBounds(str, frc);

        Graphics g3 = g2.create();
        g3.setClip(insets.left, insets.top, (int) (width * fraction), height);
        g3.setColor(progressForeground);

        int textX = insets.left + ((int) (width - bounds.getWidth()) / 2);
        int textY = (int) (insets.top + height/2 + lm.getAscent() / 2);
        g3.drawString(str, textX, textY);
        g3.dispose();

        g3 = g2.create();
        g3.setClip(insets.left + (int) (width * fraction), insets.top, getWidth() - insets.left - (int) (width * fraction), height);
        g3.setColor(getForeground());
        g3.drawString(str, insets.left + ((int) (width - bounds.getWidth()) >> 1), textY);
        g3.dispose();
    }
}

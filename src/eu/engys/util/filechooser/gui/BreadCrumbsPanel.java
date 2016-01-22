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

package eu.engys.util.filechooser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public class BreadCrumbsPanel extends JPanel {

    public static final String NAME = "chooser.breadcrumbspanel";

    private FileChooserController controller;
    private JPanel mainPanel;
    private JScrollPane scrollPanel;
    private MoveToTheEndListener listener;
    private JScrollBar horizontalScrollBar;

    private List<JButton> buttons = new ArrayList<>();

    public BreadCrumbsPanel(FileChooserController controller) {
        super(new BorderLayout());
        this.controller = controller;
        setName(NAME);
        layoutComponents();
    }

    private void layoutComponents() {
        this.mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.scrollPanel = createScrollPanel();
        this.horizontalScrollBar = scrollPanel.getHorizontalScrollBar();
        scrollPanel.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        this.listener = new MoveToTheEndListener();
        add(scrollPanel, BorderLayout.CENTER);
    }

    private JScrollPane createScrollPanel() {
        JScrollPane scrollPanel = new JScrollPane(mainPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        return scrollPanel;
    }

    public void updatePanel(FileObject fileObject) {
        horizontalScrollBar.addAdjustmentListener(listener);

        buttons.clear();
        mainPanel.removeAll();
        addButtons(fileObject);

        revalidate();
        repaint();
    }

    private void addButtons(FileObject fileObject) {
        try {
            addButtonFor(fileObject);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    private void addButtonFor(final FileObject fo) throws FileSystemException {
        if (fo != null) {
            FileObject parent = fo.getParent();
            if (parent != null) {
                addButtonFor(parent);
            }
            if (isRoot(fo)) {
                _add(fo, "/");
            } else {
                String baseName = fo.getName().getBaseName();
                if (!baseName.isEmpty()) {
                    _add(fo, baseName);
                }
            }
        }
    }

    private void _add(final FileObject fo, String name) {
        JButton button = new JButton(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                controller.goToURL(fo);
            }
        });
        button.setName(name);
        mainPanel.add(button);
        buttons.add(button);
    }

    private boolean isRoot(FileObject fo) throws FileSystemException {
        return fo.getName().getBaseName().isEmpty() && fo.getParent() == null;
    }

    public List<JButton> getButtons() {
        return buttons;
    }

    public List<String> getPath() {
        List<String> path = new ArrayList<>();
        for (JButton b : buttons) {
            path.add((String) b.getAction().getValue(Action.NAME));
        }
        return path;
    }

    private class MoveToTheEndListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (!e.getValueIsAdjusting()) {
                horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
                horizontalScrollBar.removeAdjustmentListener(this);
            }
        }
    }

}

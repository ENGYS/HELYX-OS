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

package eu.engys.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractGUIPanel extends JPanel implements GUIPanel, ModelObserver {

    private static final Logger logger = LoggerFactory.getLogger(GUIPanel.class);

    @Inject
    protected ProgressMonitor monitor;

    private final String title;
    protected final Model model;

    private JLabel titleLabel;
    protected JToolBar titleToolbar;

    protected CanvasPanel view3D;

    public AbstractGUIPanel(String title, Model model) {
        super(new BorderLayout());
        this.title = title;
        this.model = model;

        setName(title);
        logger.info("-> {}", title);
    }

    @Override
    public void install(CanvasPanel view3D) {
        this.view3D = view3D;
    }
    
    @Override
    public void layoutPanel() {
        JComponent titleComponent = createTitle(title);
        JComponent mainComponent = layoutComponents();

        titleComponent.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 8));
        mainComponent.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        add(titleComponent, BorderLayout.NORTH);
        add(mainComponent, BorderLayout.CENTER);
    }

    protected JComponent layoutComponents() {
        return new JLabel("Component");
    }

    private JComponent createTitle(String text) {
        titleLabel = new JLabel(text);
        Font font = titleLabel.getFont();
        titleLabel.setFont(font.deriveFont(font.getSize2D() + 2).deriveFont(Font.BOLD));

        titleToolbar = UiUtil.getToolbar("view.gui.toolbar");
        titleToolbar.add(titleLabel);
        titleToolbar.add(Box.createHorizontalGlue());

        PanelBuilder pb = new PanelBuilder();
        pb.addComponent(titleToolbar);
        pb.addComponent(new JSeparator());

        return pb.removeMargins().getPanel();
    }

    protected void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getKey() {
        return title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public JComponent getPanel() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    @Override
    public void load() {
    }

    @Override
    public void resetToDefaults() {
    }

    @Override
    public void save() {
        requestFocusInWindow();
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void start() {
    }

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public void stop() {
        save();
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void fieldManipulationFunctionObjectsChanged() {
    }

    @Override
    public void monitoringFunctionObjectsChanged() {
    }

    @Override
    public void stateChanged() {
    }

    @Override
    public void runtimeFieldsChanged() {
    }
    
    @Override
    public void fieldsChanged() {
    }
    
    @Override
    public void solverChanged() {
    }

    @Override
    public void materialsChanged() {
    }

    @Override
    public void projectChanged() {
    }

    @Override
    public int getIndex() {
        return -1;
    }

    public ProgressMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public String toString() {
        return title;
    }

}

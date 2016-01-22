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

package eu.engys.gui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import eu.engys.core.modules.ModulePanel;
import eu.engys.core.modules.tree.ModuleElementPanel;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.ModelObserver;
import eu.engys.gui.tree.GUIPanelHandler;
import eu.engys.gui.tree.Tree;
import eu.engys.util.ui.UiUtil;

public class ViewElementPanel extends JPanel implements GUIPanelHandler, ModuleElementPanel {
    private CardLayout mainLayout;
    private JPanel guiPanelContainer;

    protected Set<GUIPanel> panels;
    protected Map<String, GUIPanel> panelsMap = new HashMap<String, GUIPanel>();
    protected Map<String, ModulePanel> modulePanelsMap = new HashMap<String, ModulePanel>();

    private JSplitPane splitPane;
    private Tree tree;
    private ViewElement element;

    public ViewElementPanel(ViewElement element) {
        super();
        this.element = element;
        this.panels = element.getPanels();
        setName(element.getTitle());
        layoutPanel();
    }

    protected void layoutPanel() {
        mainLayout = new CardLayout();
        guiPanelContainer = new JPanel(mainLayout);

        tree = new Tree(this);

        for (GUIPanel guiPanel : panels) {
            String title = guiPanel.getKey();
            JComponent panel = guiPanel.getPanel();

            guiPanelContainer.add(panel, title);
            panelsMap.put(title, guiPanel);
        }

        splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(false);
        splitPane.setLeftComponent(tree);
        splitPane.setRightComponent(guiPanelContainer);
        splitPane.setDividerLocation(200);

        setLayout(new BorderLayout());
        setBorder(UiUtil.getStandardBorder());

        if (element.getActions() != null) {
            add(element.getActions().toolbar(), BorderLayout.NORTH);
        }

        add(splitPane, BorderLayout.CENTER);
    }

    private GUIPanel selectedPanel;

    @Override
    public void selectAndClearPanel(String key) {
        selectPanel(key);
        selectedPanel.clear();
    }

    @Override
    public void selectPanel(final String key) {
        if (selectedPanel != null) {
            if (selectedPanel.getKey().equals(key))
                return;
            if (selectedPanel.canStop()) {
                selectedPanel.stop();
            } else {
                return;
            }
        }

        if (panelsMap.containsKey(key)) {
            this.selectedPanel = panelsMap.get(key);
        } else if (modulePanelsMap.containsKey(key)) {
            this.selectedPanel = (GUIPanel) modulePanelsMap.get(key);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                selectedPanel.start();
                mainLayout.show(guiPanelContainer, key);
            }
        });
    }

    public void selectNode(String key) {
        selectPanel(key);
        tree.selectPanel(selectedPanel);
    }

    public GUIPanel getNode(String key) {
        return panelsMap.get(key);
    }

    @Override
    public Set<GUIPanel> getPanels() {
        return panels;
    }

    public List<ModelObserver> getObservers() {
        List<ModelObserver> list = new ArrayList<ModelObserver>();
        for (GUIPanel guiPanel : panelsMap.values()) {
            if (guiPanel instanceof ModelObserver) {
                list.add((ModelObserver) guiPanel);
            }
        }
        for (ModulePanel modulePanel : modulePanelsMap.values()) {
            if (modulePanel instanceof ModelObserver) {
                list.add((ModelObserver) modulePanel);
            }
        }
        return list;
    }

    public void start() {
        if (tree != null) {
            tree.selectPanelIfNeeded();
            if (selectedPanel != null) {
                selectedPanel.start();
            }
            tree.addListener();
        }
    }

    public void stop() {
        if (tree != null) {
            tree.removeListener();
        }
        if (selectedPanel != null) {
            selectedPanel.stop();
        }
    }

    public String getSelectedNode() {
        return selectedPanel.getKey();
    }

    public void clear() {
        getTree().clearAllSelections();
    }

    public Tree getTree() {
        return tree;
    }

    @Override
    public void addPanel(ModulePanel modulePanel) {
        String key = modulePanel.getKey();

        if (!modulePanelsMap.containsKey(key)) {
            JComponent panel = modulePanel.getPanel();
            guiPanelContainer.add(panel, key);
            modulePanelsMap.put(key, modulePanel);
        }

        if (modulePanel instanceof GUIPanel) {
            tree.addPanel((GUIPanel) modulePanel);
        }

    }

    @Override
    public void removePanel(ModulePanel modulePanel) {
        if (modulePanel instanceof GUIPanel) {
            tree.removePanel((GUIPanel) modulePanel);
        }
    }

    /*
     * For test purposes only
     */
    public Map<String, ModulePanel> getModulePanelsMap() {
        return modulePanelsMap;
    }
}

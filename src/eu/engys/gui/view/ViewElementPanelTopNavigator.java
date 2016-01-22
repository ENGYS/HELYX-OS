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
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.engys.gui.GUIPanel;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class ViewElementPanelTopNavigator extends ViewElementPanel {

    private CardLayout mainLayout;
    private JPanel mainPanel;
    private ViewElementTopNavigator navigator;

    private GUIPanel selectedPanel;

    public ViewElementPanelTopNavigator(ViewElement element) {
        super(element);
    }

    protected void layoutPanel() {
        mainLayout = new CardLayout();
        mainPanel = new JPanel(mainLayout);

        navigator = new ViewElementTopNavigator(this);

        for (GUIPanel guiPanel : panels) {
            String title = guiPanel.getKey();
            JComponent panel = guiPanel.getPanel();

            mainPanel.add(panel, title);
            panelsMap.put(title, guiPanel);
        }

        setLayout(new BorderLayout());
        setBorder(UiUtil.getStandardBorder());
        add(navigator, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        if (panels.iterator().hasNext()) {
            String key = panels.iterator().next().getKey();
            navigator.selectPanel(key);
            selectPanel(key);
        }
    }

    GUIPanel getSelectedPanel() {
        return selectedPanel;
    }

    public boolean canStop(final String key) {
        return selectedPanel != null && selectedPanel.canStop();
    }

    @Override
    public void selectPanel(final String key) {
        if (key == null)
            return;
        if (selectedPanel != null) {
            if (selectedPanel.getKey().equals(key))
                return;
            if (selectedPanel.canStop()) {
                selectedPanel.stop();
            } else {
                return;
            }
        }
        final GUIPanel panel = panelsMap.get(key);
        this.selectedPanel = panel;
        panel.start();

        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                mainLayout.show(mainPanel, key);
                navigator.selectPanel(key);
            }
        });
    }

    public void panelChanged(GUIPanel panel) {
        selectPanel(panel.getKey());
    }

    public Set<GUIPanel> getPanels() {
        return panels;
    }

    @Override
    public void clear() {
        navigator.clear();
        selectedPanel = null;
    }

    @Override
    public void start() {
        super.start();
        if (selectedPanel == null) {
            navigator.setSelectedIndex(0);
        }
    }
}

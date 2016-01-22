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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import eu.engys.gui.view3D.widget.Widget;
import eu.engys.gui.view3D.widget.WidgetComponent;

public class WidgetPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private Set<Widget> components;
    private Map<String, WidgetComponent> componentsMap;
    private boolean hidden = true;

    @Inject
    public WidgetPanel(Set<Widget> components) {
        super(new BorderLayout());
        this.components = components;
        this.componentsMap = new HashMap<>();
        layoutComponents();
    }

    private void layoutComponents() {
        tabbedPane = new JTabbedPane();
        for (Widget widget : components) {
            WidgetComponent widgetComponent = widget.getWidgetComponent();
            if (widgetComponent != null) {
            	componentsMap.put(widgetComponent.getKey(), widgetComponent);
            }
        }
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void showPanel(String key) {
        WidgetComponent c = componentsMap.get(key);
        c.handleShow();
        if(getTabIndex(key) == -1){
        	tabbedPane.addTab(key, c.getPanel());
        }
        tabbedPane.setSelectedComponent(c.getPanel());
    }
    
    private int getTabIndex(String tab){
    	for (int i = 0; i < tabbedPane.getTabCount(); i++) {
    		if(tabbedPane.getTitleAt(i).equals(tab)){
    			return i;
    		}
		}
    	return -1;
    }

    public void hidePanel(String key) {
    	tabbedPane.removeTabAt(getTabIndex(key));
    }

    public boolean isEmpty() {
        return tabbedPane.getTabCount() == 0;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    public boolean isHidden() {
        return hidden;
    }

    public void clear() {
        for (WidgetComponent wc : componentsMap.values()) {
            wc.clear();
        }
    }
}

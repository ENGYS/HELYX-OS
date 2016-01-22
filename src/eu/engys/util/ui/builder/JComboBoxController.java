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


package eu.engys.util.ui.builder;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import eu.engys.util.ui.JComboBoxWithItemsSupport;

public class JComboBoxController extends JComboBoxWithItemsSupport implements GroupController {
	
	private List<String> keys = new ArrayList<String>();
	private Map<String, GroupController> childControllers = new HashMap<String, GroupController>();
	
    public JComboBoxController() {
        super();
    }
    
    @Override
    public void addActionListener(ActionListener action) {
        super.addActionListener(action);            
    }

    @Override
    public void addGroup(String groupKey, String groupName) {
    	if (!keys.contains(groupKey)) {
    		keys.add(groupKey);
    		super.addItem(groupName);
    	}
    }
    
    @Override
    public void addChildController(GroupController controller) {
    	childControllers.put(keys.get(keys.size()-1), controller);
    }
    
    @Override
    public GroupController getChildController(String key) {
    	return childControllers.get(key);
    }
    
    @Override
    public JComponent getComponent() {
        return this;
    }
    
    @Override
    public void setSelectedItem(String groupName) {
    	super.setSelectedItem(groupName);
    }
    
    @Override
    public void setSelectedKey(String key) {
    	super.setSelectedIndex(keys.indexOf(key));
    }
    
    @Override
    public String getSelectedKey() {
    	int index = getSelectedIndex();
    	return index < 0 ? null : keys.get(index);
    }
    
    public boolean containsKey(String key) {
    	return keys.contains(key);
    }
}

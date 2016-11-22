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

package eu.engys.util.bean;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import eu.engys.util.ui.JComboBoxWithItemsSupport;
import eu.engys.util.ui.builder.GroupController;

public class BeanComboBoxController<T> extends JComboBoxWithItemsSupport<BeanModel<? extends T>> implements GroupController {
	
    private Map<String, BeanModel<? extends T>> itemsMap = new HashMap<>();
	private List<String> keys = new ArrayList<>();
	private Map<String, GroupController> childControllers = new HashMap<>();
	
    public BeanComboBoxController(BeanModel<? extends T>[] items) {
        super();
        for (int i = 0; i < items.length; i++) {
            addItem(items[i]);
            itemsMap.put(items[i].getBean().getClass().getCanonicalName(), items[i]);
        }
    }

    @Override
    public void addActionListener(ActionListener action) {
        super.addActionListener(action);            
    }

    @Override
    public void addGroup(String groupKey, String groupName) {
    	if (!keys.contains(groupKey)) {
    		keys.add(groupKey);
    		if (itemsMap.containsKey(groupKey)) {
    		    BeanModel<? extends T> item = itemsMap.get(groupKey);
    		    addLabel(item, groupName);
    		} else {
    		    throw new RuntimeException("");
    		}
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
    
//    @Override
//    public void setSelectedItem(String groupName) {
//    	super.setSelectedItem(groupName);
//    }
    
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

    public List<String> getKeys() {
        return keys;
    }
}

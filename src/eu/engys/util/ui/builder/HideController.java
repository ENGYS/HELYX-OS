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

package eu.engys.util.ui.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class HideController implements GroupController {
	private ActionListener action;
	private JLabel label = new JLabel();
	private List<String> groups = new ArrayList<>();
	private List<String> keys = new ArrayList<>();
	private Map<String, GroupController> childControllers = new HashMap<>();
	
	private String selectedKey = null;

	public void setSelectedIndex(int i) {
		setSelectedItem(groups.get(i));
	}

//	@Override
	public void setSelectedItem(String item) {
		selectedKey = item != null ? keys.get(groups.indexOf(item)) : null;
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, selectedKey));
		label.setText(item);
	}
	
	@Override
	public void addItemListener(ItemListener pop) {
	}

	@Override
	public void setSelectedKey(String key) {
		setSelectedIndex(keys.indexOf(key));
	}
	
	@Override
	public String getSelectedKey() {
		return selectedKey;
	}
	
	public String getSelectedItem() {
		return groups.get(keys.indexOf(selectedKey));
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
		return label;
	}

	@Override
	public void addGroup(String groupKey, String groupName) {
		groups.add(groupName);
		keys.add(groupKey);
	}

	@Override
	public void addActionListener(ActionListener showHideAction) {
		this.action = showHideAction;
	}

	@Override
	public List<String> getKeys() {
	    return keys;
	}
}

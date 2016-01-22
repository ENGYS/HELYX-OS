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


package eu.engys.util.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class JComboBoxWithItemsSupport extends JComboBox<String> implements ItemListener {
	
	private Map<String, String> itemFromKey = new HashMap<String, String>();
	private List<Integer> disabledIndexes = new ArrayList<Integer>();
	
	public JComboBoxWithItemsSupport() {
    	super();
    	final ListCellRenderer<? super String> renderer = getRenderer();
        setRenderer(new ListCellRenderer<String>() {
            @Override
        	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            	Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            	
            	if (c instanceof JLabel) {
            		JLabel label = (JLabel) c;
            		String key = value;
            		label.setText(itemFromKey.containsKey(key) ? itemFromKey.get(key) : key);
            		
            		if (disabledIndexes.contains(index)) {
            			label.setFocusable(false);
            			label.setEnabled(false);
            			if (isSelected) 
            				label.setBackground(list.getBackground());
            		} else {
            			label.setFocusable(true);
            			label.setEnabled(true);
            		}
            	}
            	
            	return c;
        	}
        });
        addItemListener(this);
    }
    
	@Override
    public void itemStateChanged(ItemEvent e) {
    	if (e.getSource() instanceof JComboBox && e.getStateChange() == ItemEvent.SELECTED) {
    		JComboBox<?> combo = (JComboBox<?>) e.getSource();
			int index = combo.getSelectedIndex();
    		if (disabledIndexes.contains(index)) {
    			combo.setSelectedIndex(-1);
    		}
    	}
    }
	
	@Override
	public void setSelectedIndex(int index) {
		if (disabledIndexes.contains(index)) {
			super.setSelectedIndex(-1);
		} else {
			super.setSelectedIndex(index);
		}
	}
	
	public boolean isDisabledAt(int index){
		return disabledIndexes.contains(index);
	}
	
//    private void addDisabledIndex(int index) {
//    	disabledIndexes.add(index);
//    }

    public void addDisabledItem(String item) {
        int itemIndex = ((DefaultComboBoxModel<String>) getModel()).getIndexOf(item);
        disabledIndexes.add(itemIndex);
    }
    
    public void clearDisabledIndexes() {
    	disabledIndexes.clear();
    }

	public void setLabels(String[] labels) {
		for (int i = 0; i < getItemCount(); i++) {
			itemFromKey.put(getItemAt(i), labels[i]);
		}
	}
    
}

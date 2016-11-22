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

package eu.engys.util.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class JComboBoxWithItemsSupport<T> extends JComboBox<T> {
	
	private Map<T, String> itemFromKey = new HashMap<>();
	private List<Integer> disabledIndexes = new ArrayList<Integer>();
    private boolean isDisabledIndex;
	
	public JComboBoxWithItemsSupport() {
    	super();
    	final ListCellRenderer<? super T> renderer = getRenderer();
        setRenderer(new ListCellRenderer<T>() {
            @Override
        	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
            	Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            	
            	if (c instanceof JLabel) {
            		JLabel label = (JLabel) c;
            		T key = value;
            		label.setText(key != null ? (itemFromKey.containsKey(key) ? itemFromKey.get(key) : key.toString()) : "");
            		
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
    }

	@Override
	public void setPopupVisible(boolean visible) {
	    if(!visible && isDisabledIndex){
	        isDisabledIndex = false;
	    } else {
	        super.setPopupVisible(visible);
	    }
	}
	
	@Override
	public void setSelectedIndex(int index) {
		if (disabledIndexes.contains(index)) {
		    isDisabledIndex = true;
		    if(getSelectedIndex() == index){
		        super.setSelectedIndex(-1);
		    }
		} else {
			super.setSelectedIndex(index);
		}
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public void setSelectedItem(Object item) {
        if (disabledIndexes.contains(getIndexOf((T)item))) {
            isDisabledIndex = true;
            if(getSelectedItem() == item){
                super.setSelectedIndex(-1);
            }
        } else {
            super.setSelectedItem(item);
        }
    }
	
    public void addDisabledItem(T item) {
        int itemIndex = getIndexOf(item);
        disabledIndexes.add(itemIndex);
    }

    public void addDisabledItem(int itemIndex) {
        disabledIndexes.add(itemIndex);
    }
    
    private int getIndexOf(T item) {
        for (int i = 0; i < getModel().getSize(); i++) {
            if (getModel().getElementAt(i).equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public void clearDisabledIndexes() {
    	disabledIndexes.clear();
    }
    
    public boolean isDisabled(T item){
        int itemIndex = getIndexOf(item);
        return disabledIndexes.contains(itemIndex);
    }

    @Override
    public void addItem(T item) {
        super.addItem(item);
    }

    public void addItem(T item, String label) {
        super.addItem(item);
        itemFromKey.put(item, label);
    }
    
    public void addLabel(T item, String label) {
        itemFromKey.put(item, label);
    }
    
}

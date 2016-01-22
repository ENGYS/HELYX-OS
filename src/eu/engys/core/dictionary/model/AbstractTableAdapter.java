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


package eu.engys.core.dictionary.model;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractTableAdapter extends JPanel {

    public static final Integer LEAVE_ONE_LINE = 1;

    private Map<Integer, JComponent[]> rowsMap = new LinkedHashMap<>();

    private final String[] columnLabels;
    private final String rowLabel;
    private PanelBuilder builder;
    private final Integer linesToLeave;
    protected JPanel buttonsPanel;
	private boolean showColumnNames;

    public AbstractTableAdapter(String[] columnNames) {
        this(columnNames, "", 0, true);
    }

    public AbstractTableAdapter(String[] columnNames, String rowsLabel, Integer linesToLeave, boolean showColumnNames) {
        super(new BorderLayout());
        this.rowLabel = rowsLabel;
        this.columnLabels = columnNames;
        this.linesToLeave = linesToLeave;
		this.showColumnNames = showColumnNames;
        layoutComponents();
    }

    private void layoutComponents() {
        setOpaque(false);
        setName("abstract.table.adapter");

        this.builder = new PanelBuilder();
        buttonsPanel = getButtonsPanel();
        add(buttonsPanel, BorderLayout.NORTH);
        add(builder.getPanel(), BorderLayout.CENTER);
        clear();
    }

    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setOpaque(false);

        JButton addButton = new JButton(new AddRowAction());
        addButton.setName("add.row.button");
        buttonsPanel.add(addButton);

        JButton remButton = new JButton(new RemRowAction());
        remButton.setName("rem.row.button");
        buttonsPanel.add(remButton);
        return buttonsPanel;
    }

    public void clear() {
        rowsMap.clear();
        builder.clear();
        if(showColumnNames){
        	builder.addComponent("", labelArrayField());
        	builder.addFill(new JSeparator());
        }
    }

    private JLabel[] labelArrayField() {
    	JLabel[] labels = new JLabel[columnLabels.length];
    	for (int i = 0; i < labels.length; i++) {
			labels[i] = new JLabel(columnLabels[i]);
		}
		return labels;
	}

	public void hideButtonsPanel() {
        buttonsPanel.setVisible(false);
    }

    protected abstract void addRow();

    protected JComponent[] addRow(JComponent[] field) {
        return addRow(field, true);
    }

    protected JComponent[] addRow(JComponent[] field, boolean save) {
        addPropertyChangeListeners(field);
        String label = rowLabel.isEmpty() ? "" : rowLabel + (rowsMap.size() + 1);
        JComponent[] componentToAdd = createComponent(field);
        builder.addComponent(label, componentToAdd);
        setNames(rowsMap.size(), field);
        updateGUI();
        rowsMap.put(rowsMap.size(), field);
        if(save){
            save();
        }
        return componentToAdd;
    }

    private void setNames(int row, JComponent... c) {
        for (int i = 0; i < c.length; i++) {
            c[i].setName((rowLabel.isEmpty() ? getName() : rowLabel) + "." + columnLabels[i] + "." + row);
        }
    }

    protected JComponent[] createComponent(JComponent[] field) {
        return field;
    }

    protected void removeRow() {
        if (rowsMap.size() > linesToLeave) {
            removeLastRowFromMap();
            clear();
            load();
            updateGUI();
        }
    }

    private void removeLastRowFromMap() {
        Integer[] keys = rowsMap.keySet().toArray(new Integer[0]);
        JComponent[] removedComp = rowsMap.remove(keys[keys.length - 1]);
        save();
        triggerEventFor3D(removedComp);
    }

    private void updateGUI() {
        JPanel panel = builder.getPanel();
        panel.revalidate();
        panel.repaint();
    }

    public Map<Integer, JComponent[]> getRowsMap() {
        return rowsMap;
    }
    
    protected int elementsCount() {
        return rowsMap.size();
    }

    protected abstract void load();

    protected abstract void save();

    protected void addPropertyChangeListeners(JComponent[] field) {
        for (int i = 0; i < field.length; i++) {
            JComponent f = field[i];
            if (f instanceof JCheckBox) { 
                ((JCheckBox) f).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        save();
                    }
                });
            } else {
                f.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("value")) {
                            save();
                        }
                    }
                });
            }
        }
    }

    protected void triggerEventFor3D(JComponent[] comp) {
    }

    private final class AddRowAction extends AbstractAction {

        private AddRowAction() {
            super("+");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addRow();
        }
    }

    private final class RemRowAction extends AbstractAction {
        private RemRowAction() {
            super("-");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            removeRow();
        }
    }

}

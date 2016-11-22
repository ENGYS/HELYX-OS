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
package eu.engys.gui.mesh.panels.lines;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import eu.engys.core.dictionary.model.AbstractTableAdapter;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine.Refinement;
import eu.engys.gui.mesh.panels.AbstractGeometryPanel.Size;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public class FeatureLinesRefinementTable extends AbstractTableAdapter {

    public static final String REFINEMENTS_LEVELS_KEY = "feature.lines.refinements.levels";
    public static final String LEVEL_LABEL = "Level";
    public static final String DISTANCE_M_LABEL = "Distance [m]";
    public static final String CELL_SIZE_LABEL = "Cell Size [m]";

	private static final String[] COLUMN_NAMES = { DISTANCE_M_LABEL, LEVEL_LABEL, CELL_SIZE_LABEL};
	
    private List<Refinement> refinements;
    private Model model;
    
	public FeatureLinesRefinementTable(Model model, List<Refinement> refinements) {
	    super(COLUMN_NAMES);
	    setName(REFINEMENTS_LEVELS_KEY);
        this.model = model;
        this.refinements = refinements;
//        System.out.println("FeatureLinesRefinementTable.FeatureLinesRefinementTable() size: " + refinements.size());
//		fixOldStyleLevels();
	}

	public JButton getButton() {
		JButton b = new JButton(new AbstractAction("Edit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				getDialog().setVisible(true);
			}

		});
		return b;
	}

	private JDialog getDialog() {
		final JDialog dialog = new JDialog(UiUtil.getActiveWindow(), "Refinement Level", ModalityType.MODELESS);
		dialog.setName("line.adapter.dialog");

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton(new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				dialog.setVisible(false);
			}
		});
		okButton.setName("OK");
		buttonsPanel.add(okButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(new JScrollPane(this), BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

		dialog.add(mainPanel);
		dialog.setSize(600, 400);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.getRootPane().setDefaultButton(okButton);
		return dialog;
	}

//	private void fixOldStyleLevels() {
//		if (lineDictionary.found(LEVEL)) {
//			Dictionary clone = new Dictionary(lineDictionary);
//			String levelValue = clone.lookup(LEVEL);
//			clone.remove(LEVEL);
//			clone.add(LEVELS, "( ( 0.0 " + levelValue + " ) )");
//			this.lineDictionary = clone;
//		}
//	}

	protected void addRow() {
	    DoubleField d = ComponentsFactory.doubleField();
	    IntegerField i = ComponentsFactory.intField();
	    Size s = new Size(model, i);
	    s.recalculate();
	    JTextField[] row = new JTextField[] {d, i, s};
	    addRow(row);
	}

	@Override
	public void load() {
	    clear();
	    for (Refinement ref : refinements) {
	        
	        DoubleField d = ComponentsFactory.doubleField();
	        d.setDoubleValue(ref.getDistance());
	        
	        IntegerField i = ComponentsFactory.intField();
	        i.setIntValue(ref.getLevel());
	        
	        Size s = new Size(model, i);
	        s.recalculate();
	        
  	        JTextField[] row = new JTextField[] {d, i, s};
	        addRow(row, false);
	    }
	    
//		if (getRowsMap().isEmpty()) {
//			addRow();
//		}
	}

	@Override
	protected void save() {
	    refinements.clear();
		if (getRowsMap().isEmpty()) {
			return;
		}
		
		for (Integer index : getRowsMap().keySet()) {
		    JComponent[] row = getRowsMap().get(index);
            int level = ((IntegerField) row[1]).getIntValue();
            double distance = ((DoubleField) row[0]).getDoubleValue();
            
            refinements.add(new Refinement(distance, level));
        }
	}

    public List<Refinement> getRefinements() {
        return refinements;
    }
    
    public void setRefinements(List<Refinement> refinements) {
        this.refinements = refinements;
    }

}

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

package eu.engys.gui.mesh.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.gui.Accessory;
import eu.engys.util.ui.CopyPasteSupport;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.groupcolumnheader.ColumnGroup;
import eu.engys.util.ui.groupcolumnheader.GroupableTableColumnModel;
import eu.engys.util.ui.groupcolumnheader.GroupableTableHeader;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.StringField;

public class STLAccessory implements Accessory {


    public static final String GEOMETRY_IS_IN_MM = "Geometry is in mm";
    public static final String NAME = "stl.accessory";
    
    private final String[] COLUMN_NAMES = { "Part Name", "X", "Y", "Z", "X", "Y", "Z", "X", "Y", "Z" };
    private DefaultTableModel tableModel;
    private JTable table;
    private AffineTransform[] transformations;
    private JPanel panel;
    private final HelyxFileChooser chooser;
	private JCheckBox geometryInMm;

    public STLAccessory(HelyxFileChooser chooser) {
        this.chooser = chooser;
        
        panel = new JPanel(new BorderLayout());
        panel.setName(NAME);
        
        PanelBuilder optionsBuilder = new PanelBuilder(); 
        layoutOptionsPanel(optionsBuilder);
        layoutTable();

        panel.add(optionsBuilder.getPanel(), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        panel.setPreferredSize(new Dimension(600, 600));

        table.getColumnModel().getColumn(0).setPreferredWidth(120);

        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);

        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);

        table.getColumnModel().getColumn(7).setPreferredWidth(50);
        table.getColumnModel().getColumn(8).setPreferredWidth(50);
        table.getColumnModel().getColumn(9).setPreferredWidth(50);

    }

    public void layoutOptionsPanel(PanelBuilder optionsBuilder) {
        geometryInMm = new JCheckBox();
        geometryInMm.setName("stl.accessory.mm");
        geometryInMm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateScale(geometryInMm.isSelected());
			}
		});
        optionsBuilder.addComponent(GEOMETRY_IS_IN_MM, geometryInMm);
    }

    private void layoutTable() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? File.class : Double.class;
            }
        };
        table = new JTable() {
            public boolean editCellAt(int row, int column, EventObject e) {
                boolean result = super.editCellAt(row, column, e);
                final Component editor = getEditorComponent();
                if (e instanceof KeyEvent && editor instanceof JTextComponent) {
                    ((JTextComponent) editor).selectAll();
                }

                return result;
            }

        };
        table.setDefaultRenderer(File.class, new FileRenderer());
        table.setColumnModel(new GroupableTableColumnModel());
        table.setTableHeader(new GroupableTableHeader((GroupableTableColumnModel) table.getColumnModel()));
        table.setModel(tableModel);
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    saveTransformations();
                }
            }
        });
        setupEditors(table);

        // Setup Column Groups
        GroupableTableColumnModel cm = (GroupableTableColumnModel) table.getColumnModel();

        ColumnGroup g_trans = new ColumnGroup("Translate");
        g_trans.add(cm.getColumn(1));
        g_trans.add(cm.getColumn(2));
        g_trans.add(cm.getColumn(3));

        ColumnGroup g_rot = new ColumnGroup("Rotate");
        g_rot.add(cm.getColumn(4));
        g_rot.add(cm.getColumn(5));
        g_rot.add(cm.getColumn(6));

        ColumnGroup g_scale = new ColumnGroup("Scale");
        g_scale.add(cm.getColumn(7));
        g_scale.add(cm.getColumn(8));
        g_scale.add(cm.getColumn(9));

        cm.addColumnGroup(g_trans);
        cm.addColumnGroup(g_rot);
        cm.addColumnGroup(g_scale);

        CopyPasteSupport.addSupportTo(table);
    }

    private void updateScale(boolean isInMM) {
		for (int r=0; r<tableModel.getRowCount(); r++) {
			tableModel.setValueAt(isInMM ? 0.001 : 1.0, r, 7);
			tableModel.setValueAt(isInMM ? 0.001 : 1.0, r, 8);
			tableModel.setValueAt(isInMM ? 0.001 : 1.0, r, 9);
		}
	}

	private void setupEditors(JTable table) {
        StringField stringTextField = new StringField();
        final IntegerField integerTextField = new IntegerField();
        final DoubleField doubleTextField = new DoubleField();

        stringTextField.setMargin(new Insets(0, 0, 0, 0));
        integerTextField.setMargin(new Insets(0, 0, 0, 0));
        doubleTextField.setMargin(new Insets(0, 0, 0, 0));

        DefaultCellEditor stringEditor = new DefaultCellEditor(stringTextField);
        DefaultCellEditor intEditor = new DefaultCellEditor(integerTextField) {
            @Override
            public Object getCellEditorValue() {
                try {
                    return Integer.valueOf(Integer.parseInt((String) super.getCellEditorValue()));
                } catch (NumberFormatException e) {
                    return Integer.valueOf(integerTextField.getIntValue());
                }
            }
        };
        DefaultCellEditor doubleEditor = new DefaultCellEditor(doubleTextField) {
            @Override
            public Object getCellEditorValue() {
                try {
                    return Double.valueOf(Double.parseDouble((String) super.getCellEditorValue()));
                } catch (NumberFormatException e) {
                    return Double.valueOf(doubleTextField.getDoubleValue());
                }
            }
        };

        table.setDefaultEditor(String.class, stringEditor);
        table.setDefaultEditor(Double.class, doubleEditor);
        table.setDefaultEditor(Integer.class, intEditor);
    }

    public AffineTransform[] getTransformations() {
        if (transformations == null) {
            saveTransformations();
        }
        return transformations;
    }

    private void saveTransformations() {
        transformations = new AffineTransform[table.getRowCount()];

        for (int row = 0; row < table.getRowCount(); row++) {
            
            double[] translate = new double[] { ((Double) table.getValueAt(row, 1)), ((Double) table.getValueAt(row, 2)), ((Double) table.getValueAt(row, 3)) };
            double[] rotation  = new double[] { ((Double) table.getValueAt(row, 4)), ((Double) table.getValueAt(row, 5)), ((Double) table.getValueAt(row, 6)) };
            double[] scale     = new double[] { ((Double) table.getValueAt(row, 7)), ((Double) table.getValueAt(row, 8)), ((Double) table.getValueAt(row, 9)) };
            
            AffineTransform t = new AffineTransform();
            t.setTranslate(translate);
            t.setRotation(rotation);
            t.setScale(scale);

            // System.out.println("STLFileChooser.STLAccessory.saveTransformations() "+t);
            transformations[row] = t;
        }
    }

    @Override
    public void onSelectionChanged() {
        tableModel.setRowCount(0);
        File[] files = chooser.getSelectedFiles();
        if (files != null) {
            for (File file : files) {
                if (chooser.getSelectedFileFilter().accepts(file)) {
                	double scale = geometryInMm.isSelected() ? 0.001 : 1.0;
                    tableModel.addRow(new Object[] { file, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, scale, scale, scale });
                }
            }
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    class FileRenderer implements TableCellRenderer {
    	private TableCellRenderer defaultRenderer = new JTable().getDefaultRenderer(Object.class);
    	
    	@Override
    	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    		JLabel c = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		String text = c.getText();
    		Color fg = c.getForeground();
    		if (value instanceof File) {
    			File file = (File) value;
    			text = file.getName();

    			if (file.length() < 1024L) {
    				c.setText("(!) "+text);
    				c.setForeground(Color.RED);
    				c.setToolTipText("File may be empty");
    			} else {
    				c.setText(text);
    				c.setForeground(fg);
    				c.setToolTipText(null);
    			}
    		} 
    		return c;
    	}
    }
}

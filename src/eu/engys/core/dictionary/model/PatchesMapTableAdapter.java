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

import static eu.engys.core.project.system.MapFieldsDict.PATCH_MAP_KEY;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.util.ui.UiUtil;

public class PatchesMapTableAdapter extends DictionaryTableAdapter {

    private Patches sourcePatches = new Patches();
    private Patches targetPatches;
    private ActionListener sourceListener;
    private ActionListener targetListener;

    public PatchesMapTableAdapter(DictionaryModel dictionaryModel, Patches targetPatches) {
        super(dictionaryModel, new String[] { "Source", "Target" });
        this.targetPatches = targetPatches;
        this.sourceListener = new SourceComboActionListener();
        this.targetListener = new TargetComboActionListener();
    }

    public void updateSourceList(Patches sourcePatches) {
        this.sourcePatches = sourcePatches;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addRow() {
        JComboBox[] fields = new JComboBox[2];
        fields[0] = new JComboBox<String>();
        for (Patch sp : sourcePatches) {
            fields[0].addItem(sp.getName());
        }
        fields[0].setSelectedIndex(-1);
        fields[0].addActionListener(sourceListener);

        fields[1] = new JComboBox<String>();
        for (Patch sp : targetPatches) {
            fields[1].addItem(sp.getName());
        }
        fields[1].setSelectedIndex(-1);
        fields[1].addActionListener(targetListener);

        addRow(fields, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() {
        String patchMap = dictionaryModel.getDictionary().lookup(PATCH_MAP_KEY);
        String[] patches = DictionaryUtils.string2StringArray(patchMap);

        for (int i = 0; i < patches.length; i += 2) {
            JComboBox[] fields = new JComboBox[2];
            fields[0] = new JComboBox<String>();
            fields[0].removeActionListener(sourceListener);
            for (Patch sp : sourcePatches) {
                fields[0].addItem(sp.getName());
            }
            fields[0].setSelectedItem(patches[i]);
            fields[0].addActionListener(sourceListener);

            fields[1] = new JComboBox<String>();
            fields[1].removeActionListener(targetListener);
            for (Patch sp : targetPatches) {
                fields[1].addItem(sp.getName());
            }
            fields[1].setSelectedItem(patches[i + 1]);
            fields[1].addActionListener(targetListener);

            addRow(fields, false);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save() {
        dictionaryModel.getDictionary().remove(PATCH_MAP_KEY);

        StringBuilder sb = new StringBuilder("(");
        for (Integer key : getRowsMap().keySet()) {
            JComboBox<String>[] comps = (JComboBox<String>[]) getRowsMap().get(key);
            if(comps[0].getSelectedIndex() > -1){
                sb.append((String) comps[0].getSelectedItem() + " ");
                sb.append((String) comps[1].getSelectedItem() + " ");
            }
        }
        sb.append(")");
        dictionaryModel.getDictionary().add(PATCH_MAP_KEY, sb.toString());
    }

    private class SourceComboActionListener implements ActionListener {

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> combo = ((JComboBox<String>) e.getSource());
            for (Integer key : getRowsMap().keySet()) {
                JComboBox<String> sourceCombo = (JComboBox<String>) getRowsMap().get(key)[0];
                if (sourceCombo != combo && sourceCombo.getSelectedItem() !=null && sourceCombo.getSelectedItem().equals(combo.getSelectedItem())) {
                    combo.hidePopup();
                    combo.setSelectedIndex(-1);
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Source patch already used", "Fields Map Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
    }

    private class TargetComboActionListener implements ActionListener {

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> combo = ((JComboBox<String>) e.getSource());
            for (Integer key : getRowsMap().keySet()) {
                JComboBox<String> targetCombo = (JComboBox<String>) getRowsMap().get(key)[1];
                if (targetCombo != combo && targetCombo.getSelectedItem() != null && targetCombo.getSelectedItem().equals(combo.getSelectedItem())) {
                    combo.hidePopup();
                    combo.setSelectedIndex(-1);
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Target patch already used", "Fields Map Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
    }

}

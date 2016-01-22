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


package eu.engys.gui.casesetup.boundaryconditions.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.ParametersPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;

public abstract class AbstractParametersPanel extends JPanel implements ParametersPanel {

    private final JCheckBox allowEditing;

    protected final DictionaryPanelBuilder builder;

    private BoundaryTypePanel parentPanel;

    public AbstractParametersPanel(BoundaryTypePanel parent) {
        super(new BorderLayout());
        this.parentPanel = parent;

        setName(getClass().getSimpleName());
        setOpaque(false);

        allowEditing = new JCheckBox(new AbstractAction("Allow Multiple Patches Editing") {
            @Override
            public void actionPerformed(ActionEvent e) {
                builder.setEnabled(allowEditing.isSelected());
                canEdit = allowEditing.isSelected();
            }
        });
        allowEditing.setFont(allowEditing.getFont().deriveFont(allowEditing.getFont().getSize2D() - 2));
        allowEditing.setOpaque(false);

        builder = new DictionaryPanelBuilder();

        add(allowEditing, BorderLayout.NORTH);
        add(builder.removeMargins().getPanel(), BorderLayout.CENTER);
        init();
        populatePanel();
    }

    protected abstract void init();

    protected abstract void populatePanel();

    @Override
    public JPanel getComponent() {
        return this;
    }

    public BoundaryTypePanel getParentPanel() {
        return parentPanel;
    }

    @Override
    public void stateChanged(Model model) {
    }

    @Override
    public void materialsChanged(Model model) {
    }

    @Override
    public void tabChanged(Model model) {
    }
    
    public void loadFromDictionary(Dictionary dictionary) {
    }

    public void saveToDictionary(Dictionary dictionary) {
    	saveToDictionary(dictionary, builder);
    }

    public static final void saveToDictionary(Dictionary dictionary, DictionaryPanelBuilder builder) {
    	DictionaryModel model = builder.getSelectedModel();
    	if (model != null) {
    		Dictionary selected = new Dictionary(model.getDictionary());
//    		 System.out.println("AbstractParametersPanel.saveToDictionary() ---------"+selected.getName()+"--------- ");
//    		 System.out.println("AbstractParametersPanel.saveToDictionary() ---------"+selected+"--------- ");
    		dictionary.add(selected);
    		for (DictionaryModel companion : model.getCompanions()) {
    			// System.out.println("AbstractParametersPanel.saveToDictionary() companion is "+companion.getDictionary().getName());
    			dictionary.add(new Dictionary(companion.getDictionary()));
    		}
    	}
    }
    
    public abstract void saveToBoundaryConditions(String patchName, BoundaryConditions bc);

    public abstract void loadFromBoundaryConditions(String patchName, BoundaryConditions bc);

    private boolean canEdit = false;

    @Override
    public void setMultipleEditing(boolean multipleSelection) {
        builder.setEnabled(!multipleSelection);
        allowEditing.setVisible(multipleSelection);
        allowEditing.setSelected(false);
        canEdit = !multipleSelection;
    }

    @Override
    public boolean canEdit() {
        return canEdit;
    }

    @Override
    public DictionaryModel getDictionaryModel() {
        return builder.getSelectedModel();
    }

//    @Override
//    public void selectDictionary(Dictionary dictionary) {
//        builder.selectDictionary(dictionary);
//    }
//
//    @Override
//    public void selectDictionaryByModel(DictionaryModel model, Dictionary dict) {
//        builder.selectDictionaryByKey(model.getKey(), dict);
//    }
//
//    @Override
//    public void selectDictionaries(Dictionary dictionary, Dictionary companion) {
//        builder.selectDictionaries(dictionary, companion);
//    }

}

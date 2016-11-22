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
package eu.engys.core.dictionary.model;

import static eu.engys.core.dictionary.Dictionary.TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.util.ui.builder.GroupController;
import eu.engys.util.ui.builder.PanelBuilder;

public class DictionaryPanelBuilder extends PanelBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryPanelBuilder.class);

    private DictionaryModel selectedModel;
    private Map<String, DictionaryModel> modelsByKey = new HashMap<String, DictionaryModel>();
    private Map<String, DictionarySelector> dictionarySelectors = new HashMap<String, DictionarySelector>();

    private boolean selectModelAfterSelection = true;

    public DictionaryPanelBuilder() {
        super();
    }

    public DictionaryPanelBuilder(String name) {
        super(name);
    }

    public void startDictionary(String groupName, DictionaryModel model) {
        String groupKey = model.getKey();

        startGroup(groupKey, groupName);

        modelsByKey.put(groupKey, model);
        dictionarySelectors.put(groupKey, new DictionarySelector(controllers, groups));

        checkForParent();
    }

    public void endDictionary() {
        modelsByKey.get(groups.peek().groupKey).refresh();
        endGroup();
    }

    public DictionaryModel getSelectedModel() {
        return selectedModel;
    }

    private void setSelectedModel(DictionaryModel selectedModel) {
        if (selectedModel != null) {
            this.selectedModel = selectedModel;
        }
    }

    public void selectDictionaries(Dictionary newDictionary, Dictionary newCompanion) {
    	selectDictionaries(newDictionary, newCompanion, TYPE);
    }
    
    public void selectDictionaries(Dictionary newDictionary, Dictionary newCompanion, String selector) {
        String newName = newDictionary.getName();
        String newType = newDictionary.lookup(selector);
        String newCompanionType = newCompanion.lookup(selector);
        // System.out.println("ChoicePanelBuilder.selectDictionary() NEW TYPE: "+newType+" NEW NAME: "+newName+" NEW COMPANION TYPE: "+newCompanionType);
        // System.out.println("-------------------");
        for (String key : modelsByKey.keySet()) {
            DictionaryModel model = modelsByKey.get(key);
            Dictionary oldDictionary = model.getDictionary();
            if (model.getCompanions().size() > 0) {
                Dictionary oldCompanion = model.getCompanions().get(0).getDictionary();
                String oldName = oldDictionary.getName();
                String oldType = oldDictionary.lookup(selector);
                String oldCompanionType = oldCompanion.lookup(selector);
                // System.out.println("DictionaryPanelBuilder.selectDictionaries() OLD TYPE: "+oldType+" OLD NAME: "+oldName+" OLD COMPANION TYPE: "+oldCompanionType);

                if (newName.equals(oldName) && newType.equals(oldType) && newCompanionType.equals(oldCompanionType)) {
                    // System.out.println("DictionaryPanelBuilder.selectDictionaries() FOUND");
                    dictionarySelectors.get(key).select();
                    selectedModel.setDictionary(newDictionary);

                    DictionaryModel companion = selectedModel.getCompanions().get(0);
                    if (companion != null) {
                        companion.setDictionary(newCompanion);
                    }
                    return;
                }
            }
        }
        // System.out.println("DictionaryPanelBuilder.selectDictionaries() NOT FOUND");
    }

    public void selectDictionary(Dictionary newDictionary) {
    	selectDictionary(newDictionary, TYPE);
    }
    
    public void selectDictionary(Dictionary newDictionary, String selector) {
        if (newDictionary != null /* && newDictionary.found(TYPE) */) {
            String newName = newDictionary.getName();
            String newType = newDictionary.lookup(selector);

            // System.out.println("DictionaryPanelBuilder.selectDictionary() NEW TYPE: "+newType+" NEW NAME: "+newName);
            if (newName != null && newType != null) {
                for (String key : modelsByKey.keySet()) {
                    DictionaryModel model = modelsByKey.get(key);
                    if (model.getCompanions().size() == 0) {
                        Dictionary oldDictionary = model.getDictionary();
                        String oldName = oldDictionary.getName();
                        String oldType = oldDictionary.lookup(selector);

                        // System.out.println("DictionaryPanelBuilder.selectDictionary() OLD TYPE: "+oldType+" OLD NAME: "+oldName);

                        if (newName.equals(oldName) && newType.equals(oldType)) {
                            // System.out.println("DictionaryPanelBuilder.selectDictionary() FOUND");
                            dictionarySelectors.get(key).select();
                            selectedModel.setDictionary(newDictionary);
                            return;
                        }
                    }
                }
                logger.warn("NOT FOUND: if the model you are trying to select has a companion, use the metod 'selectDictionaries(dictionary,companion) ' instead");
            } else {
                logger.warn("DICTIONARY NAME OR TYPE ARE NULL. NAME: {}, TYPE: {}", newName, newType);
            }
        } else {
            logger.warn("NULL DICTIONARY");
        }
    }

    public void setShowing(String hideable, String group) {
        selectModelAfterSelection = false;
        super.setShowing(hideable, group);
        selectModelAfterSelection = true;
    }

    public void selectDictionaryByModel(DictionaryModel model, Dictionary newDictionary) {
        String key = model.getKey();
        dictionarySelectors.get(key).select();
        selectedModel.setDictionary(newDictionary);
    }

    public void selectDictionaryByKey(String key, Dictionary newDictionary) {
        dictionarySelectors.get(key).select();
        selectedModel.setDictionary(newDictionary);
    }

    @Override
    protected void afterSelection(String selectedKey) {
        super.afterSelection(selectedKey);
        DictionaryModel model = modelsByKey.get(selectedKey);
        // System.out.println("DictionaryPanelBuilder.afterSelection() "+selectedKey);
        if (model != null && selectModelAfterSelection) {
            setSelectedModel(model);
            model.refresh();
            if (model.getCompanions().size() > 0) {
                model.getCompanions().get(0).refresh();
            }
        } else {
            // System.err.println("setSelectedModel -> -> -> -> -> -> Model is NULLLL");
        }
    }

    class DictionarySelector {

        private List<GroupController> controllersStack = new ArrayList<GroupController>();
        private List<KeydRowGroup> groupsStack = new ArrayList<KeydRowGroup>();

        public DictionarySelector(Stack<GroupController> controllers, Stack<KeydRowGroup> groups) {
            this.controllersStack.addAll(controllers);
            this.groupsStack.addAll(groups);
        }

        public void select() {
            for (int i = 0; i < controllersStack.size(); i++) {
                GroupController controller = controllersStack.get(i);
                KeydRowGroup group = groupsStack.get(i);
                // System.out.println("DictionaryPanelBuilder.DictionarySelector.select() ["+i+"] "+group.groupKey);
                controller.setSelectedKey(group.groupKey);
            }
        }

    }

}

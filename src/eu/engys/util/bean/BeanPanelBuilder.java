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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.ui.builder.GroupController;
import eu.engys.util.ui.builder.PanelBuilder;

public class BeanPanelBuilder extends PanelBuilder {

    private static final Logger logger = LoggerFactory.getLogger(BeanPanelBuilder.class);

    private BeanModel selectedModel;
    private Map<String, BeanModel> modelsByKey = new HashMap<>();
    private Map<String, BeanSelector> beanSelectors = new HashMap<String, BeanSelector>();

    private boolean selectModelAfterSelection = true;

    public BeanPanelBuilder() {
        super();
    }

    public BeanPanelBuilder(String name) {
        super(name);
    }

    public void startBean(String groupName, BeanModel<?> model) {
        String groupKey = model.getBean().getClass().getCanonicalName();

        startGroup(groupKey, groupName);

        modelsByKey.put(groupKey, model);
        beanSelectors.put(groupKey, new BeanSelector(controllers, groups));

        checkForParent();
    }

    public void endBean() {
        endGroup();
    }

    public BeanModel getSelectedModel() {
        return selectedModel;
    }

    private void setSelectedModel(BeanModel selectedModel) {
        if (selectedModel != null) {
            this.selectedModel = selectedModel;
        }
    }

    @SuppressWarnings("unchecked")
    public void selectBean(Object newBean) {
        if (newBean != null) {
            Class<?> newType = newBean.getClass();
            //ut.println("DictionaryPanelBuilder.selectDictionary() NEW TYPE: "+newType);
            for (String key : modelsByKey.keySet()) {
                BeanModel model = modelsByKey.get(key);
                Object oldBean = model.getBean();
                Class<?> oldType = oldBean.getClass();
                //System.out.println("DictionaryPanelBuilder.selectDictionary() OLD TYPE: "+oldType);
                if (newType.equals(oldType)) {
                    //System.out.println("DictionaryPanelBuilder.selectDictionary() FOUND");
                    beanSelectors.get(key).select();
                    setSelectedModel(model);
                    selectedModel.setBean(newBean);
                    return;
                }
            }
            logger.warn("NOT FOUND: if the model you are trying to select has a companion, use the metod 'selectDictionaries(dictionary,companion) ' instead");
        } else {
            logger.warn("NULL DICTIONARY");
        }
    }

    @Override
    protected void afterSelection(String selectedKey) {
        super.afterSelection(selectedKey);
        BeanModel model = modelsByKey.get(selectedKey);
        // System.out.println("DictionaryPanelBuilder.afterSelection() "+selectedKey);
        if (model != null && selectModelAfterSelection) {
            setSelectedModel(model);
        } else {
            // System.err.println("setSelectedModel -> -> -> -> -> -> Model is NULLLL");
        }
    }

    class BeanSelector {

        private List<GroupController> controllersStack = new ArrayList<GroupController>();
        private List<KeydRowGroup> groupsStack = new ArrayList<KeydRowGroup>();

        public BeanSelector(Stack<GroupController> controllers, Stack<KeydRowGroup> groups) {
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

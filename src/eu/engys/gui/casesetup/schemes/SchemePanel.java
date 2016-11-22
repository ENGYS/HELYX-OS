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
package eu.engys.gui.casesetup.schemes;

import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.util.ui.ComponentsFactory.doubleField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import eu.engys.core.project.Model;
import eu.engys.util.ui.textfields.DoubleField;
import net.java.dev.designgridlayout.Componentizer;

public class SchemePanel {

    private String fieldName;
    private DoubleField value1;
    private DoubleField value2;
    private DoubleField value3;
    private JComboBox<SchemeTemplate> choice;
    private AdvectionSchemes schemes;
    private Model model;

    public SchemePanel(Model model, AdvectionSchemes schemes, String fieldName) {
        this.model = model;
        this.schemes = schemes;
        this.fieldName = fieldName;
        layoutComponents();
    }

    private void layoutComponents() {
        this.value1 = doubleField(0.0, 1.0);
        this.value1.setVisible(false);
        this.value1.setName(fieldName + ".0");

        this.value2 = doubleField(0.0, 1.0);
        this.value2.setVisible(false);
        this.value2.setName(fieldName + ".1");

        this.value3 = doubleField(0.0, 1.0);
        this.value3.setVisible(false);
        this.value3.setName(fieldName + ".2");

        this.choice = new JComboBox<SchemeTemplate>();
        this.choice.setName(fieldName);

        if (fieldName.equals(U)) {
            if (model.getState().isCoupled()) {
                if (model.getState().isRANS()) {
                    addAllChoices(schemes.getCoupledVectorSchemesRANS());
                } else if (model.getState().isLES()) {
                    addAllChoices(schemes.getCoupledVectorSchemesLES());
                }
            } else {
                addAllChoices(schemes.getVectorSchemes());
            }
        } else {
            addAllChoices(schemes.getScalarSchemes());
        }

        this.choice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SchemeTemplate template = choice.getItemAt(choice.getSelectedIndex());
                if (template != null) {
                    value1.setVisible(template.hasValue1());
                    value2.setVisible(template.hasValue2());
                    value3.setVisible(template.hasValue3());
                }
            }
        });
    }

    void load() {
        Scheme scheme = schemes.load(fieldName);
        choice.setSelectedItem(scheme.getTemplate());
        value1.setDoubleValue(scheme.getValue1());
        value2.setDoubleValue(scheme.getValue2());
        value3.setDoubleValue(scheme.getValue3());
    }

    void save() {
        Scheme scheme = new Scheme();
        scheme.setField(fieldName);
        scheme.setTemplate(choice.getItemAt(choice.getSelectedIndex()));
        scheme.setValue1(value1.getDoubleValue());
        scheme.setValue2(value2.getDoubleValue());
        scheme.setValue3(value3.getDoubleValue());
        schemes.save(scheme);
    }

    private void addAllChoices(List<SchemeTemplate> templates) {
        for (SchemeTemplate template : templates) {
            choice.addItem(template);
        }
    }

    JComponent getPanel() {
        return Componentizer.create().minToPref(choice).minAndMore(value1).minAndMore(value2).minAndMore(value3).component();
    }
}

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


package eu.engys.gui.casesetup.schemes;

import static eu.engys.util.ui.ComponentsFactory.doubleField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.java.dev.designgridlayout.Componentizer;
import eu.engys.gui.casesetup.schemes.AdvectionSchemes.Scheme;
import eu.engys.gui.casesetup.schemes.AdvectionSchemes.SchemeTemplate;
import eu.engys.util.ui.textfields.DoubleField;

public class SchemePanel {

    private String fieldName;
    private DoubleField value1;
    private DoubleField value2;
    private DoubleField value3;
    private JComboBox<SchemeTemplate> choice;
    private AdvectionSchemes schemes;

    public SchemePanel(AdvectionSchemes schemes, String fieldName) {
        this.schemes = schemes;
        this.fieldName = fieldName;

        layoutComponents();
    }

    void load() {
        Scheme scheme = schemes.readScheme(fieldName);
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
        schemes.writeScheme(scheme);
    }

    private void layoutComponents() {
        value1 = doubleField();
        value2 = doubleField();
        value3 = doubleField();

        value1.setVisible(false);
        value2.setVisible(false);
        value3.setVisible(false);

        value1.setName(fieldName + ".0");
        value2.setName(fieldName + ".1");
        value3.setName(fieldName + ".2");

        choice = new JComboBox<SchemeTemplate>();
        choice.setName(fieldName);
        
        if (fieldName.equals("U")) {
            for (SchemeTemplate scheme : schemes.getVectorSchemes()) {
                choice.addItem(scheme);
            }
        } else {
            for (SchemeTemplate scheme : schemes.getScalarSchemes()) {
                choice.addItem(scheme);
            }
        }

        choice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SchemeTemplate scheme = choice.getItemAt(choice.getSelectedIndex());
                if (scheme != null) {
                    value1.setVisible(scheme.hasValue1());
                    value2.setVisible(scheme.hasValue2());
                    value3.setVisible(scheme.hasValue3());
                }
            }
        });

        // PropertyChangeListener listener = new PropertyChangeListener() {
        // @Override
        // public void propertyChange(PropertyChangeEvent evt) {
        // if (evt.getPropertyName().equals("value")) {
        // choice.setSelectedIndex(choice.getSelectedIndex());
        // if (choice.getSelectedIndex() == 5) {
        // choice.setEnabled(false);
        // } else {
        // choice.setEnabled(true);
        // }
        // }
        // }
        // };

        // value1.addPropertyChangeListener(listener);
        // value2.addPropertyChangeListener(listener);
        // advectionBuilder.addComponent(name, row);
    }

    JComponent getPanel() {
        return Componentizer.create().minToPref(choice).minAndMore(value1).minAndMore(value2).minAndMore(value3).component();
    }
}

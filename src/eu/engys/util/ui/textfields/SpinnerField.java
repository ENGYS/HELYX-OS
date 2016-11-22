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
package eu.engys.util.ui.textfields;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import eu.engys.util.ui.ExecUtil;

public class SpinnerField extends JSpinner implements Serializable {

	public SpinnerField() {
		this(-Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
	}

	public SpinnerField(int min, int max) throws IllegalArgumentException {
		this(min, max, 0 < min ? min : 0);
	}

	public SpinnerField(int min, int max, int value) throws IllegalArgumentException {
	    super(new SpinnerNumberModel(value, min, max, 1));

        final JFormattedTextField textField = getTextField();
        textField.setHorizontalAlignment(JTextField.LEFT);
	    
		NumberFormatter def = new NullableNumberFormatter();
		def.setValueClass(Integer.class);
		def.setMinimum(min);
		def.setMaximum(max);
		
		NumberFormatter disp = new NullableNumberFormatter(new IntegerField.IntegerDisplayFormat());
		disp.setValueClass(Integer.class);
		disp.setMinimum(min);
		disp.setMaximum(max);
		
		NumberFormatter ed = new NullableNumberFormatter(new IntegerField.IntegerEditFormat());
		ed.setValueClass(Integer.class);
		ed.setMinimum(min);
		ed.setMaximum(max);
		
		DefaultFormatterFactory dff = new DefaultFormatterFactory(def, disp, ed);
		textField.setFormatterFactory(dff);
        textField.setColumns(4);

        textField.addFocusListener(new SpinnerFocusListener());
    }
		
    public JFormattedTextField getTextField() {
        return ((JSpinner.DefaultEditor) getEditor()).getTextField();
	}

	public void setIntValue(int value) {
		super.setValue(Integer.valueOf(value));
	}
	
	public int getIntValue() {
		Object value = super.getValue();
		if (value != null && value instanceof Integer) {
			return ((Integer) super.getValue()).intValue();
		}
		return 0;
	}

    private class SpinnerFocusListener implements FocusListener {

        @Override
        public void focusGained(final FocusEvent e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ExecUtil.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ((JTextField) e.getSource()).selectAll();
                        }
                    });
                }
            }).start();
        }

        @Override
        public void focusLost(final FocusEvent e) {
            ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((JTextField) e.getSource()).getCaret().setDot(((JTextField) e.getSource()).getCaretPosition());
                }
            });
        }

    }

}

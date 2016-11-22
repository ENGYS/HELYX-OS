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

import java.awt.Insets;
import java.text.ParseException;

import javax.swing.text.DefaultFormatterFactory;

import eu.engys.util.ui.textfields.verifiers.StringVerifier;
/**
 * Provides a JFormattedTextField that accepts only Strings.
 */
public class StringField extends PromptTextField {

    public StringField() {
        this("");
    }

    public StringField(boolean checkEmptyStrings, boolean checkForbidden) {
        this("", checkEmptyStrings, checkForbidden);
    }

    public StringField(String text, boolean checkEmptyStrings, boolean checkForbidden) {
        this(text, -1, checkEmptyStrings, checkForbidden);
    }

    public StringField(String text) {
        this(text, -1, true, true);
    }

    public StringField(int columns) {
        this("", columns, true, true);
    }

    public StringField(String text, int columns, boolean checkEmptyStrings, boolean checkForbidden) {
        super();
        setFormatterFactory(new DefaultFormatterFactory(new StringFormatter()));
        setText(text);
        setInputVerifier(new StringVerifier(this));
        setColumns(columns != -1 ? columns : STRING_FIELD_COLUMNS);
        setToVerifier(checkEmptyStrings, checkForbidden);
    }

    public StringField(Insets insets) {
        super(insets);
    }

    public void setStringValue(String value) {
        super.setValue(value);
    }

    public String getStringValue() {
        return (String) super.getValue();
    }

    public void setToVerifier(boolean checkEmptyStrings, boolean checkForbidden) {
        ((StringVerifier) getInputVerifier()).setCheckEmptyStrings(checkEmptyStrings);
        ((StringVerifier) getInputVerifier()).setCheckForbidden(checkForbidden);
    }

    class StringFormatter extends AbstractFormatter {
        @Override
        public Object stringToValue(String text) throws ParseException {
            return text;
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            return value != null ? value.toString() : "";
        }
    }

}

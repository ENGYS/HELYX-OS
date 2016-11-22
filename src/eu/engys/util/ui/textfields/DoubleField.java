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

import java.awt.Font;
import java.awt.Insets;
import java.beans.Transient;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import eu.engys.util.ui.textfields.verifiers.DoubleVerifier;

/**
 * Provides a JFormattedTextField that accepts only doubles. Allows for the setting of the number
 * of decimal places displayed, and min/max values allowed.
 * 
 * See also
 *  https://docs.oracle.com/javase/tutorial/uiswing/components/formattedtextfield.html
 *  http://stackoverflow.com/a/12978182
 *  http://www.javalobby.org/java/forums/t20551.html
 * 
 */
public class DoubleField extends PromptTextField implements Serializable {

    private static final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

    public static final int DEFAULT_PLACES = 10;

    private double minValue = -Double.MAX_VALUE;
    private double maxValue = Double.MAX_VALUE;
    
    private DefaultFormatterFactory factory;
    private DoubleVerifier verifier;

    private Font normalFont;
    private Font nonuniformFont;
    
    private Double defaultValue;
    
    public DoubleField(Insets insets) {
        super(insets);
    }
    
    public DoubleField() {
        this(-Double.MAX_VALUE, Double.MAX_VALUE, Double.valueOf(0), DEFAULT_PLACES);
    }

    public DoubleField(int places) {
        this(-Double.MAX_VALUE, Double.MAX_VALUE, Double.valueOf(0), places);
    }

    public DoubleField(double min, double max) throws IllegalArgumentException {
        this(min, max, Double.valueOf(0), DEFAULT_PLACES);
    }

    public DoubleField(double min, double max, Double value) throws IllegalArgumentException {
        this(min, max, value, DEFAULT_PLACES);
    }

    public DoubleField(double min, double max, Double value, int places) throws IllegalArgumentException {
        this.defaultValue = value;
        this.minValue = min;
        this.maxValue = max;
        this.verifier = new DoubleVerifier(this, min, max);

        setValue(value);
        setInputVerifier(verifier);

        NumberFormatter def = new NullableNumberFormatter();
        def.setValueClass(Double.class);
        def.setMinimum(minValue);
        def.setMaximum(maxValue);

        NumberFormatter displayFormatter = new NullableNumberFormatter(getFormatForDISPLAY(places));
        displayFormatter.setValueClass(Double.class);
        displayFormatter.setMinimum(minValue);
        displayFormatter.setMaximum(maxValue);

        NumberFormatter editFormatter = new NullableNumberFormatter(getFormatForEDIT(places));
        editFormatter.setValueClass(Double.class);
        editFormatter.setMinimum(minValue);
        editFormatter.setMaximum(maxValue);

        this.factory = new DefaultFormatterFactory(def, displayFormatter, editFormatter);
        setFormatterFactory(factory);

        this.normalFont = super.getFont();
        this.nonuniformFont = super.getFont().deriveFont(Font.ITALIC);

        setColumns(NUMBER_FIELD_COLUMNS);
    }
    
    public static AdaptativeFormat getFormatForDISPLAY(int places) {
        return new AdaptativeFormat(new DoubleDisplayFormat(places), new DoubleScientificFormat(places), places);
    }

    private static AdaptativeFormat getFormatForEDIT(int places) {
        return new AdaptativeFormat(new DoubleEditFormat(places), new DoubleScientificFormat(places), places);
    }

    @Override
    @Transient
    public Font getFont() {
        if (nonuniformFont != null && getText().equals(DoubleVerifier.NONUNIFORM))
            return nonuniformFont;
        else if (normalFont != null)
            return normalFont;

        return super.getFont();
    }

    @Override
    protected void invalidEdit() {
    }
    
    /*
     * If you write by hand something like 1.2E+06 the formatter does not like it. It requires no + sign
     */
    @Override
    public String getText() {
		return super.getText().replace('e', 'E').replace("E+", "E");
    }
    
    /*
     * If you prompt something like 1.2E+06 the formatter does not like it. It requires no + sign
     */
    @Override
    protected String getFixedinputString(String content) {
        return content.replace('e', 'E').replace("E+", "E");
    }
    
    public void setDoubleValue(double value) {
        super.setValue(Double.valueOf(value));
    }
    
    public double getDoubleValue() {
        Object value = super.getValue();
        if (value != null && value instanceof Double) {
            return ((Double) value).doubleValue();
        }
        return Double.NaN;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        verifier.setMinValue(minValue);
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        verifier.setMaxValue(maxValue);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +(getName() != null ? getName() : "noname" ) + "] value: " + String.valueOf(getDoubleValue());
    }

    public static double[] toArray(DoubleField[] field) {
        double[] value = new double[field.length];
        for (int i = 0; i < value.length; i++) {
            value[i] = field[i].getDoubleValue();
        }
        return value;
    }

    public static class DoubleDisplayFormat extends DecimalFormat {
        public DoubleDisplayFormat(int places) {
            super();
            setDecimalFormatSymbols(decimalFormatSymbols);
            setMinimumIntegerDigits(1);
            setMaximumIntegerDigits(Integer.MAX_VALUE);
            setMinimumFractionDigits(1);
            setMaximumFractionDigits(places);
            setParseIntegerOnly(false);
            setGroupingUsed(true);
        }
    }
    
    public static class DoubleEditFormat extends DecimalFormat {
        public DoubleEditFormat(int places) {
            super();
            setDecimalFormatSymbols(decimalFormatSymbols);
            setMinimumIntegerDigits(1);
            setMaximumIntegerDigits(Integer.MAX_VALUE);
            setMinimumFractionDigits(0);
            setMaximumFractionDigits(places);
            setParseIntegerOnly(false);
            setGroupingUsed(false);
        }
    }

    public static class DoubleScientificFormat extends DecimalFormat {
        public DoubleScientificFormat(int places) {
            super("#.#E0##");
            setDecimalFormatSymbols(decimalFormatSymbols);
            setMinimumIntegerDigits(1);
            setMaximumIntegerDigits(1);
            setMinimumFractionDigits(0);
            setMaximumFractionDigits(places);
            setParseIntegerOnly(false);
            setGroupingUsed(false);
        }
    }
    
//    public static void main(String[] args) {
//        System.err.println(new AdaptativeFormat(new DoubleEditFormat(10), new DoubleScientificFormat(10), 10).format(1e-14));
//    }
}

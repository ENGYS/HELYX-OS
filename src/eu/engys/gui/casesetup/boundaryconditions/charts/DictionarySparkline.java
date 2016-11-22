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
package eu.engys.gui.casesetup.boundaryconditions.charts;

import static eu.engys.util.RegexpUtils.DOUBLE;
import static eu.engys.util.RegexpUtils.SCALAR_PATTERN;
import static eu.engys.util.RegexpUtils.VECTOR_PATTERN;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.StartWithFinder;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryError;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryListener;
import eu.engys.core.dictionary.parser.ListField2;

public class DictionarySparkline extends SparklineChart implements DictionaryListener {
    
    private boolean vector;
    private DictionaryModel dictionaryModel;
    private StartWithFinder finder;

    public DictionarySparkline(DictionaryModel dictionaryModel, String dictionaryKey, boolean vector) {
        super(vector);
        this.dictionaryModel = dictionaryModel;
        this.finder = new StartWithFinder(dictionaryKey);
        this.vector = vector;
        dictionaryModel.addDictionaryListener(this);
    }
    
    @Override
    public void dictionaryChanged() throws DictionaryError {
        updateChart();
    }
    
    @Override
    protected Double[][] parseData() {
        Dictionary dict = dictionaryModel.getDictionary();
        Double[][] data = new Double[0][0];
        if (dict.found(finder)) {
            DefaultElement element = dict.lookup(finder);
            if (element instanceof ListField2) {
                // Fix for alpha1 which is read with DictionaryReader2
                data = parseTable(ListField2.convertToString((ListField2)element));
            } else if(element instanceof FieldElement){
                data = parseTable(((FieldElement)element).getValue().trim());
            }
        }
        return data;
    }

    private Double[][] parseTable(String tableData) {
        List<Double[]> data = new ArrayList<>();
        if (tableData.startsWith("(") && tableData.endsWith(")")) {
            tableData = tableData.substring(1, tableData.length() - 1).trim();
            try {
                Pattern regex = Pattern.compile(vector ? VECTOR_PATTERN : SCALAR_PATTERN);
                Matcher regexMatcher = regex.matcher(tableData);
                while (regexMatcher.find()) {
                    Double[] row = parseRow(regexMatcher.group(0));
                    data.add(row);
                }
            } catch (PatternSyntaxException ex) {
                ex.printStackTrace();
            }
        }
        return data.toArray(new Double[0][0]);
    }

    private Double[] parseRow(String row) {
        Pattern innerRegex = Pattern.compile(DOUBLE);
        Matcher innerRegexMatcher = innerRegex.matcher(row);
        Double[] values = vector ? new Double[4] : new Double[2];
        int internalCount = 0;
        while (innerRegexMatcher.find()) {
            values[internalCount++] = Double.parseDouble(innerRegexMatcher.group(0));
        }
        return values;
    }
}
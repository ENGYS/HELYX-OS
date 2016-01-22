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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ControlDict;
import eu.engys.core.project.system.FvSchemes;

public class AdvectionSchemes {

    private static final Logger logger = LoggerFactory.getLogger(AdvectionSchemes.class);

    static class SchemeTemplate {
        private final String key;
        private final String label;
        private final Dictionary functions;

        public SchemeTemplate(String key, String label, Dictionary functions) {
            this.key = key;
            this.label = label;
            this.functions = functions;
        }

        @Override
        public String toString() {
            return label;
        }

        public boolean hasValue1() {
            return key.contains("%f");
        }

        public boolean hasValue2() {
            return key.contains("%f %f");
        }

        public boolean hasValue3() {
            return StringUtils.countMatches(key, "%f") == 3;
        }

        public boolean equalsIgnoringValues(String field, String schemeKey) {
            String keyWithFieldName = key.replace("%s", gradName(field));

            String[] schemeKeyTokens = schemeKey.split("\\s+");
            String[] keyWithFieldNameTokens = keyWithFieldName.split("\\s+");

            if (schemeKeyTokens.length != keyWithFieldNameTokens.length)
                return false;

            for (int i = 0; i < schemeKeyTokens.length; i++) {
                if (schemeKeyTokens[i].equals(keyWithFieldNameTokens[i])) {
                    continue;
                } else if (keyWithFieldNameTokens[i].equals("%f")) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }

        public List<Double> extractValues(String field, String schemeKey) {
            String keyWithFieldName = key.replace("%s", gradName(field));

            String[] schemeKeyTokens = schemeKey.split("\\s+");
            String[] keyWithFieldNameTokens = keyWithFieldName.split("\\s+");

            List<Double> values = new ArrayList<Double>();
            for (int i = 0; i < schemeKeyTokens.length; i++) {
                if (keyWithFieldNameTokens[i].equals("%f")) {
                    values.add(Double.parseDouble(schemeKeyTokens[i]));
                }
            }
            return values;
        }

        public Dictionary getFunctions() {
            return functions;
        }
    }

    static class Scheme {
        private SchemeTemplate template;
        private String field;
        private double value1;
        private double value2;
        private double value3;

        public void setField(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        public void setValue1(double value1) {
            this.value1 = value1;
        }

        public double getValue1() {
            return value1;
        }

        public void setValue2(double value2) {
            this.value2 = value2;
        }

        public double getValue2() {
            return value2;
        }

        public void setValue3(double value3) {
            this.value3 = value3;
        }
        
        public double getValue3() {
            return value3;
        }

        public void setTemplate(SchemeTemplate template) {
            this.template = template;
        }

        public SchemeTemplate getTemplate() {
            return template;
        }
    }

    private ArrayList<SchemeTemplate> scalar = new ArrayList<SchemeTemplate>();
    private ArrayList<SchemeTemplate> vector = new ArrayList<SchemeTemplate>();
    private Model model;

    public AdvectionSchemes(Model model) {
        this.model = model;

        Dictionary defaultSchemes = model.getDefaults().getDefaultSchemes();
        if (defaultSchemes != null) {
            ListField scalarSchemes = defaultSchemes.getList("scalar");
            ListField vectorSchemes = defaultSchemes.getList("vector");

            for (DefaultElement el : scalarSchemes.getListElements()) {
                if (el instanceof Dictionary) {
                    Dictionary d = (Dictionary) el;
                    addScalarScheme(new SchemeTemplate(d.lookup("key"), d.lookup("label"), d.subDict("functions")));
                }
            }

            for (DefaultElement el : vectorSchemes.getListElements()) {
                if (el instanceof Dictionary) {
                    Dictionary d = (Dictionary) el;
                    addVectorScheme(new SchemeTemplate(d.lookup("key"), d.lookup("label"), d.subDict("functions")));
                }
            }
        } else {
            logger.warn("Advection schemes defaults not found!");
        }
    }

    public void addScalarScheme(SchemeTemplate scheme) {
        scalar.add(scheme);
    }

    public void addVectorScheme(SchemeTemplate scheme) {
        vector.add(scheme);
    }

    public List<SchemeTemplate> getScalarSchemes() {
        return scalar;
    }

    public List<SchemeTemplate> getVectorSchemes() {
        return vector;
    }

    public void writeScheme(Scheme scheme) {
        FvSchemes fvSchemes = model.getProject().getSystemFolder().getFvSchemes();
        Dictionary divSchemes = fvSchemes.getDivSchemes();
        String gradName = gradName(scheme.field);
        String divName = div(gradName);
        String value = "";
        if (divSchemes != null && scheme.getTemplate() != null) {
            String keyWithFieldName = scheme.template.key.replace("%s", gradName);
            if (scheme.template.hasValue1()) {
                if (scheme.template.hasValue3()) {
                    value = String.format(keyWithFieldName, scheme.value1, scheme.value2, scheme.value3);
                } else if (scheme.template.hasValue2()) {
                    value = String.format(keyWithFieldName, scheme.value1, scheme.value2);
                } else {
                    value = String.format(keyWithFieldName, scheme.value1);
                }
            } else {
                value = keyWithFieldName;
            }
            divSchemes.add(divName, value);

            if (scheme.getTemplate().functions != null) {
                ControlDict controlDict = model.getProject().getSystemFolder().getControlDict();
                controlDict.functionObjectsToDict();
                Dictionary functions = controlDict.subDict("functions");
                if (functions == null) {
                    functions = new Dictionary("functions");
                    controlDict.add(functions);
                }
                functions.merge(scheme.getTemplate().functions);
                controlDict.functionObjectsToList();
            }
        }
    }

    public Scheme readScheme(String field) {
        String schemeKey = readSchemeKeyFor(div(gradName(field)));
        SchemeTemplate template = searchTemplate(field, schemeKey);
        Scheme scheme = new Scheme();
        if (template != null) {
            List<Double> values = template.extractValues(field, schemeKey);
            scheme.setTemplate(template);
            scheme.setField(field);
            scheme.setValue1(values.size() > 0 ? values.get(0) : 0);
            scheme.setValue2(values.size() > 1 ? values.get(1) : 0);
            scheme.setValue3(values.size() > 2 ? values.get(2) : 0);
        } else {
            logger.error("Template Scheme {} not found for field {}", schemeKey, field);
        }

        return scheme;
    }

    private String readSchemeKeyFor(String divName) {
        FvSchemes fvSchemes = model.getProject().getSystemFolder().getFvSchemes();
        Dictionary divSchemes = fvSchemes.getDivSchemes();
        if(divSchemes != null){
            if(divSchemes.found(divName)){
                return divSchemes.lookup(divName);
            } else {
                logger.error(String.format("Scheme Key not found for div '%s'", divName));
                return "";
            }
        } else {
            logger.error("DivSchemes is NULL");
            return "";
        }
    }

    private SchemeTemplate searchTemplate(String field, String scheme) {
        List<SchemeTemplate> templates = field.startsWith("U") ? vector : scalar;
        for (SchemeTemplate t : templates) {
            if (t.equalsIgnoringValues(field, scheme)) {
                return t;
            }
        }
        return null;
    }

    private static String div(String gradName) {
        return String.format("div(phi,%s)", gradName);
    }

    private static String gradName(String field) {
        if (field.equals("ILambda"))
            return "Ii_h";
        else
            return field;
    }

}

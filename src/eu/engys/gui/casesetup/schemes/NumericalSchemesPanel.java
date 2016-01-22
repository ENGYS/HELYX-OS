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

import static eu.engys.core.project.zero.fields.Fields.AOA;
import static eu.engys.core.project.zero.fields.Fields.CO2;
import static eu.engys.core.project.zero.fields.Fields.EPSILON;
import static eu.engys.core.project.zero.fields.Fields.ILAMBDA;
import static eu.engys.core.project.zero.fields.Fields.K;
import static eu.engys.core.project.zero.fields.Fields.NU_TILDA;
import static eu.engys.core.project.zero.fields.Fields.OMEGA;
import static eu.engys.core.project.zero.fields.Fields.SMOKE;
import static eu.engys.core.project.zero.fields.Fields.T;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.core.project.zero.fields.Fields.W;
import static eu.engys.util.ui.ComponentsFactory.doubleField;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryError;
import eu.engys.core.dictionary.model.DictionaryModel.DictionaryListener;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.FvSchemes;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class NumericalSchemesPanel extends DefaultGUIPanel {

    public static final String NUMERICAL_SCHEMES = "Numerical Schemes";
    public static final String LAPLACIAN_LABEL = "Laplacian";
    public static final String ADVECTION_LABEL = "Advection";

    public static final String NON_ORTHOGONAL_CORRECTION_LABEL = "Non-orthogonal Correction";
    public static final String GAUSS_LINEAR_LIMITED = "Gauss linear limited";
    public static final String GAUSS_LINEAR_LIMITED_CORRECTED = "Gauss linear limited corrected";
    public static final String GAUSS_LINEAR_UNCORRECTED = "Gauss linear uncorrected";
    public static final String GAUSS_LINEAR_CORRECTED = "Gauss linear corrected";

    private DictionaryModel laplaceModel;
    private DictionaryModel snGradModel;

    private PanelBuilder laplacianBuilder;
    private PanelBuilder advectionBuilder;

    private AdvectionSchemes schemes;
    
    private Map<String, SchemePanel> schemePanelsMap = new LinkedHashMap<String, SchemePanel>();

    @Inject
    public NumericalSchemesPanel(Model model) {
        super(NUMERICAL_SCHEMES, model);
    }

    @Override
    protected JComponent layoutComponents() {
        laplaceModel = new DictionaryModel(new Dictionary("laplacianSchemes"));
        snGradModel = new DictionaryModel(new Dictionary("snGradSchemes"));

        DoubleField field = doubleField(0.0, 1.0);
        laplacianBuilder = new PanelBuilder();
        laplacianBuilder.addComponent(NON_ORTHOGONAL_CORRECTION_LABEL, field).addPropertyChangeListener(new LaplacianFieldHandler("default", field));
        try {
            field.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        schemes = new AdvectionSchemes(model);

        advectionBuilder = new PanelBuilder();
        JPanel advectionPanel = advectionBuilder.removeMargins().getPanel();
        advectionPanel.setBorder(BorderFactory.createTitledBorder(ADVECTION_LABEL));
        advectionPanel.setName(ADVECTION_LABEL);

        JPanel laplacianPanel = laplacianBuilder.removeMargins().getPanel();
        laplacianPanel.setBorder(BorderFactory.createTitledBorder(LAPLACIAN_LABEL));
        laplacianPanel.setName(LAPLACIAN_LABEL);

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(advectionPanel);
        builder.addComponent(laplacianPanel);

        return builder.removeMargins().getPanel();
    }

    private void rebuildPanel() {
        if (model.getProject() != null) {
            FvSchemes fvSchemes = model.getProject().getSystemFolder().getFvSchemes();
            if (fvSchemes != null) {
                Dictionary divSchemes = fvSchemes.getDivSchemes();
                Dictionary laplacianSchemes = fvSchemes.getLaplacianSchemes();

                if (divSchemes != null && laplacianSchemes != null) {
                    
                    laplaceModel.setDictionary(laplacianSchemes);

                    advectionBuilder.clear();
                    schemePanelsMap.clear();

                    buildFieldPanel(advectionBuilder, U);
                    for (Field field : model.getFields().getMultiphaseUFields()) {
                        buildFieldPanel(advectionBuilder, field.getName());
                    }

                    buildFieldPanel(advectionBuilder, K);
                    buildFieldPanel(advectionBuilder, EPSILON);
                    buildFieldPanel(advectionBuilder, OMEGA);
                    buildFieldPanel(advectionBuilder, NU_TILDA);
                    buildFieldPanel(advectionBuilder, T);
                    buildFieldPanel(advectionBuilder, W);
                    buildFieldPanel(advectionBuilder, ILAMBDA);
                    buildFieldPanel(advectionBuilder, CO2);
                    buildFieldPanel(advectionBuilder, AOA);
                    buildFieldPanel(advectionBuilder, SMOKE);
                }
            }
        }
    }

    private void buildFieldPanel(PanelBuilder advectionBuilder, String fieldName) {

        Fields fields = getModel().getFields();
        if (fields.containsKey(fieldName)) {
            SchemePanel schemePanel = new SchemePanel(schemes, fieldName);
            schemePanel.load();
            schemePanelsMap.put(fieldName, schemePanel);
            advectionBuilder.addComponent(fieldName, schemePanel.getPanel());
        }
    }

    @Override
    public void load() {
        rebuildPanel();
    }

    @Override
    public void save() {
        for (SchemePanel panel : schemePanelsMap.values()) {
            panel.save();
        }
    }

    @Override
    public void materialsChanged() {
        rebuildLater();
    }

    @Override
    public void stateChanged() {
        rebuildLater();
    }

    @Override
    public void fieldsChanged() {
        rebuildLater();
    }

    private void rebuildLater() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                rebuildPanel();
            }
        });
    }

    class LaplacianFieldHandler implements PropertyChangeListener, DictionaryListener {
        private String key;
        private DoubleField field;

        public LaplacianFieldHandler(String key, DoubleField field) {
            this.key = key;
            this.field = field;
            laplaceModel.addDictionaryListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                String laplacianValue;
                String snGradValue;
                double value = field.getDoubleValue();
                if (value == 0) {
                    laplacianValue = GAUSS_LINEAR_UNCORRECTED;
                    snGradValue = "uncorrected";
                } else if (value == 1) {
                    laplacianValue = GAUSS_LINEAR_CORRECTED;
                    snGradValue = "corrected";
                } else {
                    laplacianValue = GAUSS_LINEAR_LIMITED + " " + Double.toString(value);
                    snGradValue = "limited" + " " + Double.toString(value);
                }
                laplaceModel.getDictionary().add("default", laplacianValue);
                snGradModel.getDictionary().add("default", snGradValue);
            }
        }

        @Override
        public void dictionaryChanged() throws DictionaryError {
            if (laplaceModel.getDictionary().found(key)) {
                String laplacianValue = laplaceModel.getDictionary().lookup(key);
                double value;
                if (laplacianValue.equals(GAUSS_LINEAR_UNCORRECTED)) {
                    value = 0;
                } else if (laplacianValue.equals(GAUSS_LINEAR_CORRECTED)) {
                    value = 1;
                } else if (laplacianValue.startsWith(GAUSS_LINEAR_LIMITED_CORRECTED)) {
                    laplacianValue = laplacianValue.replace(GAUSS_LINEAR_LIMITED_CORRECTED, "");
                    value = Double.parseDouble(laplacianValue);
                } else if (laplacianValue.startsWith(GAUSS_LINEAR_LIMITED)) {
                    laplacianValue = laplacianValue.replace(GAUSS_LINEAR_LIMITED, "");
                    value = Double.parseDouble(laplacianValue);
                } else {
                    throw new DictionaryError("Unknown Laplacian Scheme: " + laplacianValue);
                }

                field.setValue(value);
            }
        }
    }
}

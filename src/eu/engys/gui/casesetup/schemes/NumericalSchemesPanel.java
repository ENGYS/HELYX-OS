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

import static eu.engys.core.project.system.FvSchemes.CORRECTED;
import static eu.engys.core.project.system.FvSchemes.DEFAULT;
import static eu.engys.core.project.system.FvSchemes.LAPLACIAN_SCHEMES;
import static eu.engys.core.project.system.FvSchemes.LIMITED;
import static eu.engys.core.project.system.FvSchemes.SN_GRAD_SCHEMES;
import static eu.engys.core.project.system.FvSchemes.UNCORRECTED;
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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

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
        this.schemes = new AdvectionSchemes(model);
    }

    @Override
    protected JComponent layoutComponents() {
        this.laplaceModel = new DictionaryModel(new Dictionary(LAPLACIAN_SCHEMES));
        this.snGradModel = new DictionaryModel(new Dictionary(SN_GRAD_SCHEMES));

        DoubleField field = doubleField(0.0, 1.0);
        this.laplacianBuilder = new PanelBuilder();
        this.laplacianBuilder.addComponent(NON_ORTHOGONAL_CORRECTION_LABEL, field).addPropertyChangeListener(new LaplacianFieldHandler(DEFAULT, field));

        this.advectionBuilder = new PanelBuilder();

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(advectionBuilder.removeMargins().withTitle(ADVECTION_LABEL).withName(ADVECTION_LABEL).getPanel());
        builder.addComponent(laplacianBuilder.removeMargins().withTitle(LAPLACIAN_LABEL).withName(LAPLACIAN_LABEL).getPanel());

        return builder.removeMargins().getPanel();
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

    private void rebuildPanel() {
        if (model.getProject() != null) {
            FvSchemes fvSchemes = model.getProject().getSystemFolder().getFvSchemes();
            if (fvSchemes != null && fvSchemes.getLaplacianSchemes() != null) {
                laplaceModel.setDictionary(fvSchemes.getLaplacianSchemes());

                advectionBuilder.clear();
                schemePanelsMap.clear();

                buildFieldPanel(U);
                for (Field field : model.getFields().getMultiphaseUFields()) {
                    buildFieldPanel(field.getName());
                }
                buildFieldPanel(K);
                buildFieldPanel(EPSILON);
                buildFieldPanel(OMEGA);
                buildFieldPanel(NU_TILDA);
                buildFieldPanel(T);
                buildFieldPanel(W);
                buildFieldPanel(ILAMBDA);
                buildFieldPanel(CO2);
                buildFieldPanel(AOA);
                buildFieldPanel(SMOKE);
            }
        }
    }

    private void buildFieldPanel(String fieldName) {
        Fields fields = getModel().getFields();
        if (fields.containsKey(fieldName)) {
            SchemePanel schemePanel = new SchemePanel(model, schemes, fieldName);
            schemePanel.load();
            schemePanelsMap.put(fieldName, schemePanel);
            advectionBuilder.addComponent(fieldName, schemePanel.getPanel());
        }
    }

    private class LaplacianFieldHandler implements PropertyChangeListener, DictionaryListener {
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
                    snGradValue = UNCORRECTED;
                } else if (value == 1) {
                    laplacianValue = GAUSS_LINEAR_CORRECTED;
                    snGradValue = CORRECTED;
                } else {
                    laplacianValue = GAUSS_LINEAR_LIMITED + " " + Double.toString(value);
                    snGradValue = LIMITED + " " + Double.toString(value);
                }
                laplaceModel.getDictionary().add(DEFAULT, laplacianValue);
                snGradModel.getDictionary().add(DEFAULT, snGradValue);
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

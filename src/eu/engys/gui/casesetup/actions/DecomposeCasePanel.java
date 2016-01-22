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

package eu.engys.gui.casesetup.actions;

import static eu.engys.core.project.system.DecomposeParDict.DELTA_KEY;
import static eu.engys.core.project.system.DecomposeParDict.HIERARCHICAL_COEFFS_KEY;
import static eu.engys.core.project.system.DecomposeParDict.HIERARCHICAL_KEY;
import static eu.engys.core.project.system.DecomposeParDict.METHOD_KEY;
import static eu.engys.core.project.system.DecomposeParDict.NUMBER_OF_SUBDOMAINS_KEY;
import static eu.engys.core.project.system.DecomposeParDict.N_KEY;
import static eu.engys.core.project.system.DecomposeParDict.ORDER_KEY;
import static eu.engys.core.project.system.DecomposeParDict.SCOTCH_KEY;
import static eu.engys.core.project.system.DecomposeParDict.TYPE_KEYS;
import static eu.engys.core.project.system.DecomposeParDict.YXZ_KEY;
import static eu.engys.util.ui.ComponentsFactory.labelArrayField;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.DecomposeParDict;
import eu.engys.util.Util;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.IntegerField;

public class DecomposeCasePanel extends JPanel {

    public static final String DECOMPOSE_CASE_LABEL = "Decompose Case";
    public static final String HIERARCHY_LABEL = "Hierarchy";
    public static final String PROCESSORS_LABEL = "Processors";
    public static final String DECOMPOSITION_TYPE_LABEL = "Decomposition Type";

    public static final String HIERARCHICAL_LABEL = "Hierarchical";
    public static final String SCOTCH_LABEL = "Scotch";
    private static final String[] TYPE_LABELS = { HIERARCHICAL_LABEL, SCOTCH_LABEL };

    public enum Status {
        OK, CANCEL;

        public boolean isOK() {
            return this == OK;
        }

        public boolean isCancel() {
            return this == CANCEL;
        }
    }

    private final int X = 1;
    private final int Y = 0;
    private final int Z = 2;

//    private OkDialogAction okAction = new OkDialogAction();
//    private CancelDialogAction cancelAction = new CancelDialogAction();

    private JDialog dialog;

    private Model model;
    private DictionaryModel mainModel;
    private DictionaryModel hierarchicalDictionaryModel;
    private IntegerField nProcessorsField;
    private IntegerField[] nHierarchyField;
    private JComboBox<?> decompositionType;
    private Status status = Status.CANCEL;

    public DecomposeCasePanel(Model model) {
        super(new BorderLayout());
        setName("decompose.panel");
        this.model = model;
        this.mainModel = new DictionaryModel();
        this.hierarchicalDictionaryModel = new DictionaryModel();
        layoutComponents();
    }

    public void load() {
        DecomposeParDict decomposeParDict = model.getProject().getSystemFolder().getDecomposeParDict();
        Dictionary dictionary = new Dictionary(decomposeParDict);
        dictionary.remove(HIERARCHICAL_COEFFS_KEY);
        mainModel.setDictionary(dictionary);
        Dictionary coeffsDict = decomposeParDict.isDictionary(HIERARCHICAL_COEFFS_KEY) ? new Dictionary(decomposeParDict.subDict(HIERARCHICAL_COEFFS_KEY)) : new Dictionary(HIERARCHICAL_COEFFS_KEY);
        
        hierarchicalDictionaryModel.setDictionary(coeffsDict);
        recalculateFactors();
    }

    private void layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        decompositionType = mainModel.bindSelection(METHOD_KEY, TYPE_KEYS, TYPE_LABELS);
        nProcessorsField = mainModel.bindIntegerPositive(NUMBER_OF_SUBDOMAINS_KEY);
        nHierarchyField = hierarchicalDictionaryModel.bindIntegerArray(N_KEY, 3);

        builder.addComponent(DECOMPOSITION_TYPE_LABEL, decompositionType);
        builder.addComponent(PROCESSORS_LABEL, nProcessorsField);
        builder.addComponent(labelArrayField("x", "y", "z"));
        builder.addComponent(HIERARCHY_LABEL, nHierarchyField);

        for (IntegerField f : nHierarchyField)
            f.setEnabled(false);

        decompositionType.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                for (IntegerField f : nHierarchyField)
                    f.setEnabled(HIERARCHICAL_KEY.equals(evt.getNewValue()));

            }
        });
        nProcessorsField.addPropertyChangeListener(new RecalculateFactorsOnChange());
        nProcessorsField.setIntValue(1);

        add(builder.getPanel());
    }

    public void showDialog() {
        createDialog();
        load();
        dialog.setVisible(true);
    }
    
    private void createDialog() {
        if (dialog == null) {
            dialog = new JDialog(UiUtil.getActiveWindow(), DECOMPOSE_CASE_LABEL);
            dialog.setName("create.case.dialog");
            
            AbstractAction saveAndCloseDialogAction = new AbstractAction("OK") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (TYPE_KEYS[0].equals(decompositionType.getSelectedItem()) && !productEqualsToNumberOfSubdomain()) {
                        JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Product of Hierarchical Coefficients should be equal to the Number of Processors", "Decomposition Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    save();
                    status = Status.OK;
                    dialog.setVisible(false);
                }
            };

            final AbstractAction cancelAction = new AbstractAction("Cancel") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    status = Status.CANCEL;
                    dialog.setVisible(false);
                    dialog.dispose();
                    dialog = null;
                }
            };
            
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton okButton = new JButton(saveAndCloseDialogAction);
            okButton.setName("OK");
            buttonsPanel.add(okButton);
            
            JButton cancelButton = new JButton(cancelAction);
            cancelButton.setName("Cancel");
            buttonsPanel.add(cancelButton);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(this, BorderLayout.CENTER);
            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setSize(350, 200);
            dialog.setLocationRelativeTo(null);
            dialog.setModal(true);
            dialog.getRootPane().setDefaultButton(okButton);

            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    cancelAction.actionPerformed(null);
                }
            });
        }
    }

    private boolean productEqualsToNumberOfSubdomain() {
        int nOfSubdomains = nProcessorsField.getIntValue();
        int x = nHierarchyField[X].getIntValue();
        int y = nHierarchyField[Y].getIntValue();
        int z = nHierarchyField[Z].getIntValue();

        return nOfSubdomains == x * y * z;
    }

    public void save() {
        Dictionary d = new Dictionary(mainModel.getDictionary());
        Dictionary hierarchicalModelDictionary = new Dictionary(hierarchicalDictionaryModel.getDictionary());
//        hierarchicalModelDictionary.setName(HIERARCHICAL_COEFFS_KEY);
        fixSubdomainsOrder(hierarchicalModelDictionary);
        d.add(hierarchicalModelDictionary);
        
        DecomposeParDict decomposeParDict = model.getProject().getSystemFolder().getDecomposeParDict();
        decomposeParDict.merge(d);

        if (SCOTCH_KEY.equals(decomposeParDict.lookup(METHOD_KEY)) && decomposeParDict.found(HIERARCHICAL_COEFFS_KEY)) {
            decomposeParDict.remove(HIERARCHICAL_COEFFS_KEY);
        } else if (HIERARCHICAL_KEY.equals(decomposeParDict.lookup(METHOD_KEY)) && decomposeParDict.found(HIERARCHICAL_COEFFS_KEY)) {
            Dictionary hCoeffs = decomposeParDict.subDict(HIERARCHICAL_COEFFS_KEY);
            if (!hCoeffs.found(DELTA_KEY))
                hCoeffs.add(DELTA_KEY, "0.001");
            if (!hCoeffs.found(ORDER_KEY))
                hCoeffs.add(ORDER_KEY, YXZ_KEY);
        }
    }

    private void fixSubdomainsOrder(Dictionary dict) {
        if (dict.found(N_KEY)) {
            String subdomains = dict.lookup(N_KEY);
            String noParenthesis = subdomains.replaceAll("\\(", "").replaceAll("\\)", "");
            String trimmedLine = Util.getTrimmedSingleSpaceLine(noParenthesis);
            String[] values = trimmedLine.split(" ");
            dict.add(N_KEY, "(" + values[1] + " " + values[0] + " " + values[2] + ")");
        }
    }

    private void recalculateFactors() {
        int np = nProcessorsField.getIntValue();

        int[] factors = Util.getFactorsFor(np);
        nHierarchyField[X].setIntValue(factors[0]);
        nHierarchyField[Y].setIntValue(factors[1]);
        nHierarchyField[Z].setIntValue(factors[2]);
    }

    public Status getStatus() {
        return status;
    }

    class RecalculateFactorsOnChange implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                recalculateFactors();
            }
        }

    }

    /*
     * For tests puroposes only
     */
    public JDialog getDialog() {
        return dialog;
    }

}

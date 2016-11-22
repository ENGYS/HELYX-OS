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
package eu.engys.core.controller.actions;

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
import static eu.engys.util.ui.UiUtil.DIALOG_CANCEL_LABEL;
import static eu.engys.util.ui.UiUtil.DIALOG_OK_LABEL;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
    public static final String TIME_STEP = "Time Steps";

    public static final String HIERARCHICAL_LABEL = "Hierarchical";
    public static final String SCOTCH_LABEL = "Scotch";
    private static final String[] TYPE_LABELS = { HIERARCHICAL_LABEL, SCOTCH_LABEL };
    private static final String SELECT_ALL = "Select All";
    private static final String DESELECT_ALL = "Deselect All";
    public static final String DECOMPOSE_PANEL = "decompose.panel";

    private Map<JCheckBox, String> timeStepMap;

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
        setName(DECOMPOSE_PANEL);
        this.model = model;
        this.mainModel = new DictionaryModel();
        this.hierarchicalDictionaryModel = new DictionaryModel();
        this.timeStepMap = new HashMap<>();
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

        add(builder.getPanel(), BorderLayout.CENTER);
        add(createTimeStepsPanel(), BorderLayout.SOUTH);
    }

    private JComponent createTimeStepsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4));

        File[] zeroDirs = model.getProject().getZeroFolder().getFileManager().getZeroDirs("0");
        File[] nonZeroDirs = model.getProject().getZeroFolder().getFileManager().getNonZeroDirs("0");

        Set<String> timeSteps = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Double.valueOf(o1).compareTo(Double.valueOf(o2));
            }
        });
        for (File zeroDir : zeroDirs) {
            timeSteps.add(zeroDir.getName());
        }
        for (File nonZeroDir : nonZeroDirs) {
            timeSteps.add(nonZeroDir.getName());
        }

        for (String timeStep : timeSteps) {
            JCheckBox chk = new JCheckBox(timeStep, true);
            timeStepMap.put(chk, timeStep);
            chk.setName(timeStep);
            panel.add(chk);
        }

        panel.setBorder(BorderFactory.createTitledBorder(TIME_STEP));
        panel.setName(TIME_STEP);
        return panel;
    }

    public Set<String> getTimeSteps() {
        Set<String> timeSteps = new TreeSet<>();
        for (JCheckBox check : timeStepMap.keySet()){
            if(check.isSelected()){
                timeSteps.add(timeStepMap.get(check));
            }
        }
        return timeSteps;
    }

    public void showDialog() {
        createDialog();
        load();
        dialog.setVisible(true);
    }
    
    private void createDialog() {
        if (dialog == null) {
            dialog = new JDialog(UiUtil.getActiveWindow(), DECOMPOSE_CASE_LABEL);
            dialog.setName(DECOMPOSE_PANEL);
            
            AbstractAction saveAndCloseDialogAction = new AbstractAction(DIALOG_OK_LABEL) {
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

            final AbstractAction cancelAction = new AbstractAction(DIALOG_CANCEL_LABEL) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    status = Status.CANCEL;
                    dialog.setVisible(false);
                    dialog.dispose();
                    dialog = null;
                }
            };

            JPanel buttonsPanel = createButtonsPanel(saveAndCloseDialogAction, cancelAction);

            JScrollPane pane = new JScrollPane(this);
            pane.setBorder(BorderFactory.createEmptyBorder());

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(pane, BorderLayout.CENTER);
            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(null);
            dialog.setModal(true);

            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    cancelAction.actionPerformed(null);
                }
            });
        }
    }

    private JPanel createButtonsPanel(AbstractAction saveAndCloseDialogAction, AbstractAction cancelAction) {
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(saveAndCloseDialogAction);
        okButton.setName(DIALOG_OK_LABEL);
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setName(DIALOG_CANCEL_LABEL);
        rightButtonsPanel.add(okButton);
        rightButtonsPanel.add(cancelButton);

        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAll = new JButton(new AbstractAction(SELECT_ALL) {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JCheckBox chk : timeStepMap.keySet()) {
                    chk.setSelected(true);
                }
            }
        });

        JButton deselectAll = new JButton(new AbstractAction(DESELECT_ALL) {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JCheckBox chk : timeStepMap.keySet()) {
                    chk.setSelected(false);
                }
            }
        });

        leftButtonsPanel.add(selectAll);
        leftButtonsPanel.add(deselectAll);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(leftButtonsPanel);
        buttonsPanel.add(rightButtonsPanel);

        return buttonsPanel;
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

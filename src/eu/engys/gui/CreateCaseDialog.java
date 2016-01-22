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

package eu.engys.gui;

import static eu.engys.util.ui.ComponentsFactory.checkField;
import static eu.engys.util.ui.ComponentsFactory.intArrayField;
import static eu.engys.util.ui.ComponentsFactory.intField;
import static eu.engys.util.ui.ComponentsFactory.labelArrayField;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.engys.core.project.CaseParameters;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.FileFieldPanel;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.StringField;
import eu.engys.util.ui.textfields.verifiers.AbstractVerifier;
import eu.engys.util.ui.textfields.verifiers.AbstractVerifier.ValidationStatusListener;

public class CreateCaseDialog extends JPanel {

    public static final String CREATE_CASE_LABEL = "Create Case";
    public static final String HIERARCHY_LABEL = "Hierarchy";
    public static final String PROCESSORS_LABEL = "Processors";
    public static final String PARALLEL_LABEL = "Parallel";
    public static final String PARENT_FOLDER_LABEL = "Parent Folder";
    public static final String CASE_NAME_LABEL = "Case Name";

    enum Status {
        OK, ERROR, CANCEL
    }

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    private OkDialogAction okAction = new OkDialogAction();
    private CancelDialogAction cancelAction = new CancelDialogAction();

    private JDialog dialog;
    private FileFieldPanel fileField;
    private StringField nameField;
    private IntegerField nProcessorsField;
    private JCheckBox isParallel;

    private File baseDir;
    private IntegerField[] nHierarchyField;
    private Status status = Status.ERROR;

    public CreateCaseDialog() {
        super(new BorderLayout());
        layoutComponents();
        createDialog();
        setDefaultValues();
    }

    private void layoutComponents() {
        fileField = ComponentsFactory.fileField(SelectionMode.DIRS_ONLY, "Select a folder where to save the case", false);
        nameField = stringField();
        isParallel = checkField();
        nProcessorsField = intField();
        nHierarchyField = intArrayField(3);

        ((AbstractVerifier) nameField.getInputVerifier()).setValidationStatusListener(new ValidationStatusListener() {

            @Override
            public void validatePassed() {
                okAction.setEnabled(true);
            }

            @Override
            public void validateFailed() {
                okAction.setEnabled(false);
            }
        });

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(CASE_NAME_LABEL, nameField);
        builder.addComponent(PARENT_FOLDER_LABEL, fileField);
        builder.addComponent(PARALLEL_LABEL, isParallel);
        builder.addComponent(PROCESSORS_LABEL, nProcessorsField);
        builder.addComponent(labelArrayField("x", "y", "z"));
        builder.addComponent(HIERARCHY_LABEL, nHierarchyField[X], nHierarchyField[Y], nHierarchyField[Z]);
        nProcessorsField.setEnabled(false);
        for (IntegerField f : nHierarchyField)
            f.setEnabled(false);

        isParallel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nProcessorsField.setEnabled(isParallel.isSelected());
                nProcessorsField.setIntValue(isParallel.isSelected() ? 2 : 1);
                for (IntegerField f : nHierarchyField)
                    f.setEnabled(isParallel.isSelected());
            }
        });
        nProcessorsField.addPropertyChangeListener(new IntFieldHandler(nProcessorsField, nHierarchyField));
        nProcessorsField.setIntValue(1);

        add(builder.getPanel());
    }

    private void createDialog() {
        JButton okButton = new JButton(okAction);
        okButton.setName("button.ok");

        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setName("button.cancel");
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        
        dialog = new JDialog(UiUtil.getActiveWindow(), CREATE_CASE_LABEL);
        dialog.setName("create.case.dialog");
        dialog.setModal(true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(null);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getRootPane().setDefaultButton(okButton);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (cancelAction.isEnabled()) {
                    cancelAction();
                }
            }
        });
    }

    private void setDefaultValues() {
        File lastDir = PrefUtil.getWorkDir(PrefUtil.WORK_DIR);
        fileField.setFile(lastDir);
        
        nameField.setText("newCase");

        isParallel.setSelected(false);
        isParallel.doClick();
    }

    public void showDialog() {
        dialog.setVisible(true);
    }

    private void closeDialog() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    public CaseParameters getParameters() {
        CaseParameters params = new CaseParameters();
        params.setBaseDir(baseDir);
        params.setParallel(isParallel.isSelected());
        params.setnProcessors(nProcessorsField.getIntValue());
        params.setnHierarchy(new int[] { nHierarchyField[0].getIntValue(), nHierarchyField[1].getIntValue(), nHierarchyField[2].getIntValue() });
        return params;
    }

    private void checkStatus() {
        String warning = "Create Case Warning";
        String error = "Create Case Error";
        String parentPath = fileField.getFilePath();
        if (parentPath.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Empty parent folder", error, JOptionPane.ERROR_MESSAGE);
            status = Status.CANCEL;
        } else {
            File parent = new File(parentPath);
            if (parent.exists()) {
                if (Util.canWrite(parent)) {
                    baseDir = new File(parent, nameField.getText());
                    if (baseDir.exists()) {
                        if (baseDir.isFile()) {
                            JOptionPane.showMessageDialog(dialog, "File already exists", error, JOptionPane.ERROR_MESSAGE);
                            status = Status.CANCEL;
                        } else if (baseDir.isDirectory() && baseDir.list().length != 0) {
                            int retVal = JOptionPane.showConfirmDialog(dialog, "Folder already exists. Continue anyway?", warning, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (retVal == JOptionPane.NO_OPTION) {
                                status = Status.CANCEL;
                            } else {
                                status = Status.OK;
                            }
                        } else {
                            status = Status.OK;
                        }
                    } else {
                        status = Status.OK;
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Write permission", error, JOptionPane.ERROR_MESSAGE);
                    status = Status.CANCEL;
                }
            } else {
                String msg = String.format("Folder %s does not exist.\n Do you want to create it?", parent);
                int retVal = JOptionPane.showConfirmDialog(dialog, msg, warning, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (retVal == JOptionPane.NO_OPTION) {
                    status = Status.CANCEL;
                } else {
                    if (parent.canWrite()) {
                        status = Status.OK;
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Write permission", error, JOptionPane.ERROR_MESSAGE);
                        status = Status.CANCEL;
                    }
                }
            }
        }
    }

    private void okAction() {
        checkStatus();
        if (isOK()) {
            if (isParallel.isSelected() && !productEqualsToNumberOfSubdomain()) {
                JOptionPane.showMessageDialog(dialog, "Product of Hierarchical Coefficients should be equal to the Number of Processors", "Decomposition error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        closeDialog();
    }

    private boolean productEqualsToNumberOfSubdomain() {
        int nOfSubdomains = nProcessorsField.getIntValue();
        int x = nHierarchyField[X].getIntValue();
        int y = nHierarchyField[Y].getIntValue();
        int z = nHierarchyField[Z].getIntValue();

        return nOfSubdomains == x * y * z;
    }

    private void cancelAction() {
        status = Status.CANCEL;
        closeDialog();
    }

    public boolean isOK() {
        return status == Status.OK;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isCancel() {
        return status == Status.CANCEL;
    }

    private final class OkDialogAction extends AbstractAction implements Runnable {

        public OkDialogAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(OkDialogAction.this);
        }

        @Override
        public void run() {
            okAction();
        }
    }

    private final class CancelDialogAction extends AbstractAction implements Runnable {

        public CancelDialogAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(CancelDialogAction.this);
        }

        @Override
        public void run() {
            cancelAction();
        }
    }

    class IntFieldHandler implements PropertyChangeListener {
        private IntegerField[] nHierarchyField;
        private IntegerField nProcessorsField;

        public IntFieldHandler(IntegerField nProcessorsField, IntegerField[] nHierarchyField) {
            this.nProcessorsField = nProcessorsField;
            this.nHierarchyField = nHierarchyField;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                int np = nProcessorsField.getIntValue();

                int[] factors = Util.getFactorsFor(np);
                nHierarchyField[X].setIntValue(factors[0]);
                nHierarchyField[Y].setIntValue(factors[1]);
                nHierarchyField[Z].setIntValue(factors[2]);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("12  -> " + Arrays.toString(Util.getFactorsFor(12)));
        System.out.println("128 -> " + Arrays.toString(Util.getFactorsFor(128)));
        System.out.println("120 -> " + Arrays.toString(Util.getFactorsFor(120)));
        System.out.println("512 -> " + Arrays.toString(Util.getFactorsFor(512)));
        System.out.println("2   -> " + Arrays.toString(Util.getFactorsFor(2)));
        System.out.println("47  -> " + Arrays.toString(Util.getFactorsFor(47)));
        System.out.println("13  -> " + Arrays.toString(Util.getFactorsFor(13)));
        System.out.println("1   -> " + Arrays.toString(Util.getFactorsFor(1)));
        // System.out.println("0   -> "+Arrays.toString(getFactorsFor(0)));
    }

    public JDialog getDialog() {
        return dialog;
    }
}

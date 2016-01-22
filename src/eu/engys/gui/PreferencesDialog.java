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
import static eu.engys.util.ui.ComponentsFactory.intField;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.java.dev.designgridlayout.Componentizer;

import org.apache.commons.io.FileUtils;

import eu.engys.core.OpenFOAMEnvironment;
import eu.engys.core.executor.FileManagerSupport;
import eu.engys.core.project.defaults.DictDataFolder;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.FileFieldPanel;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;
import eu.engys.util.ui.textfields.StringField;

public class PreferencesDialog {

    private static final String DEFAULT_DICTIONARIES_LABEL = "Default Dictionaries";
    public static final String ERROR_LABEL_TEXT = "* the file no longer exist";
    private static final String PREFIX_FOR_TOOLTIP = "Full Installation Path, ";
    private static final String OPENFOAM_TOOLTIP_LINUX = "e.g. <INSTALLATION_PATH>/%s/OpenFOAM-x.x_engysEdition-x.x";
    private static final String OPENFOAM_TOOLTIP_WINDOWS = "e.g. <INSTALLATION_PATH>\\%s\\OpenFOAM-x.x_engysEdition-x.x";
    private static final String OPENFOAM_TOOLTIP_LINUX_OS = "e.g. <INSTALLATION_PATH>/%s/OpenFOAM-x.x.x";
    private static final String OPENFOAM_TOOLTIP_WINDOWS_OS = "e.g. <INSTALLATION_PATH>\\%s\\OpenFOAM-x.x.x";
    private static final String PARAVIEW_TOOLTIP_LINUX = "e.g. <INSTALLATION_PATH>/ParaView x.x.x/bin/paraview";
    private static final String PARAVIEW_TOOLTIP_WINDOWS = "e.g. <INSTALLATION_PATH>\\ParaView x.x.x\\bin\\paraview.exe";
    private static final String FIELDVIEW_TOOLTIP_LINUX = "e.g. <INSTALLATION_PATH>/fv/bin/fv";
    private static final String FIELDVIEW_TOOLTIP_WINDOWS = "e.g. <INSTALLATION_PATH>\\Intelligent Light\\FVWINxx\\bin\\fv.bat";
    private static final String ENSIGHT_TOOLTIP_LINUX = "e.g. <INSTALLATION_PATH>/CEI/bin/ensight100";
    private static final String ENSIGHT_TOOLTIP_WINDOWS = "e.g. <INSTALLATION_PATH>\\CEI\\bin\\ensight100.bat";

    private static final String TERMINAL_TOOLTIP = "Override system terminal, e.g. 'xterm'";
    private static final String FILE_MANAGER_TOOLTIP = "Override system file manager, e.g. 'nautilus'";
    private static final String DEFAULT_HOSTFILE_TOOLTIP = "Turn on/off the default hostfile (needs restart)";
    // private static final String FILE_OPENER_TOOLTIP =
    // "Override system file opener, e.g. 'gnome-open'";

    /*
     * Labels
     */

    private static final String PATHS_LABEL = "Paths";
    public static final String CORE_FOLDER_LABEL = "Core Folder";
    public static final String PARA_VIEW_EXECUTABLE_LABEL = "ParaView Executable";
    public static final String FIELD_VIEW_EXECUTABLE_LABEL = "FieldView Executable";
    public static final String EN_SIGHT_EXECUTABLE_LABEL = "EnSight Executable";

    private static final String SERVER_LABEL = "Server";
    // public static final String OUTPUT_LOG_WAIT_TIME_LABEL = "Output Log Wait Time";
    public static final String KILL_WAIT_TIME_LABEL = "Kill Wait Time";
    public static final String OUTPUT_LOG_REFRESH_INTERVAL_LABEL = "Output Log Refresh Interval";
    public static final String RUN_WAIT_TIME_LABEL = "Run Wait time";
    // public static final String STOP_WAIT_TIME_LABEL = "Stop Wait Time";
    public static final String CONNECTION_TRIES_INTERVAL_MSEC_LABEL = "Connection Tries Interval [msec]";
    public static final String CONNECTION_TRIES_LABEL = "Connection Tries";

    private static final String _3D_RENDERING_LABEL = "3D Rendering";
    public static final String ENABLE_LOD_THRESHOLD_KB_LABEL = "Enable LOD Threshold [KB]";
    public static final String LOCK_INTERACTIVE_RENDER_FOR_MSEC_LABEL = "Lock Interactive Render For [msec]";
    public static final String DISBLE_TRANSPARENCY_THRESHOLD_KB_LABEL = "Disable Transparency Threshold [KB]";

    private static final String MISC_LABEL = "Misc";
    public static final String MAX_LOG_LINES_LABEL = "Max Log Lines";
    // public static final String MAX_CHART_LINES_LABEL = "Max Chart Lines";
    public static final String HIDE_EMPTY_PATCHES_LABEL = "Hide Empty Patches";
    public static final String CUSTOM_FILE_MANAGER_LABEL = "Custom File Manager";
    public static final String CUSTOM_TERMINAL_COMMAND_LABEL = "Custom Terminal Command";
    public static final String DEFAULT_HOSTFILE_NONE_LABEL = "Default Hostfile Off (needs restart)";

    private JDialog dialog;
    private FileFieldPanel fieldViewPanel;
    private FileFieldPanel ensightPanel;
    private FileFieldPanel paraViewPanel;
    private FileFieldPanel openFoamPanel;

    private IntegerField connectionTries;
    private IntegerField connectionRefresh;
    private IntegerField waitForStopTime;
    private IntegerField waitForRunTime;
    private IntegerField scriptRefresh;
    private IntegerField waiForKillTime;
    private IntegerField logWaitTime;

    private IntegerField interactiveMemory;
    private IntegerField interactiveTime;
    private IntegerField transparencyMemory;

    private StringField defaultTerminal;
    private StringField defaultFileManager;
    private StringField defaultFileOpener;
    private JCheckBox defaultHostFile;

    private JCheckBox hideEmptyPatches;

    private IntegerField maxChartRows;
    private IntegerField maxLogRows;

    private final boolean isOS;
    private final boolean paraview;
    private final boolean fieldview;
    private final boolean ensight;
    private final boolean hasSolverPreferences;

    private JLabel errorLabel;
    private JButton okButton;
    private JButton openDefaults;
    private DictDataFolder dictDataFolder;

    public PreferencesDialog(boolean isOS, boolean paraview, boolean fieldview, boolean ensight, boolean hasSolverPreferences, DictDataFolder dictDataFolder) {
        this.isOS = isOS;
        this.paraview = paraview;// HELYX-OS or Windows OS
        this.fieldview = fieldview;// HELYX-SAS and HELYX
        this.ensight = ensight;// HELYX
        this.hasSolverPreferences = hasSolverPreferences;
        this.dictDataFolder = dictDataFolder;

        initDialog();
        load();
    }

    private void initDialog() {
        JScrollPane scrollPane = new JScrollPane(createCenterPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

        dialog = new JDialog(UiUtil.getActiveWindow(), "Preferences", ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(mainPanel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setName("PreferencesDialog");
        dialog.getRootPane().setDefaultButton(okButton);
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
                dialog.setVisible(false);
            }
        });
        okButton.setName("OK");
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        cancelButton.setName("Cancel");

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        return buttonsPanel;
    }

    /*
     * Load
     */

    public void load() {
        loadPathProperties();
        loadBatchProperties();
        loadVTKProperties();
        loadMiscProperties();

    }

    private void loadPathProperties() {
        openFoamPanel.setFile(PrefUtil.getOpenFoamEntry());
        if (paraview) {
            paraViewPanel.setFile(PrefUtil.getParaViewEntry());
        }
        if (fieldview) {
            fieldViewPanel.setFile(PrefUtil.getFieldViewEntry());
        }
        if (ensight) {
            ensightPanel.setFile(PrefUtil.getEnsightEntry());
        }

        updateErrorLabel();
    }

    private void updateErrorLabel() {
        boolean coreOk = openFoamPanel.hasExistingFile();
        if (!coreOk || !isParaViewOk() || !isFieldViewOk() || !isEnsightOk()) {
            errorLabel.setText(ERROR_LABEL_TEXT);
        } else {
            errorLabel.setText("");
        }
    }

    private void loadBatchProperties() {
        connectionTries.setIntValue(PrefUtil.getInt(PrefUtil.SERVER_CONNECTION_MAX_TRIES));
        connectionRefresh.setIntValue(PrefUtil.getInt(PrefUtil.SERVER_CONNECTION_REFRESH_TIME));
        waitForRunTime.setIntValue(PrefUtil.getInt(PrefUtil.SERVER_WAIT_FOR_RUN_REFRESH_TIME));
        scriptRefresh.setIntValue(PrefUtil.getInt(PrefUtil.SCRIPT_RUN_REFRESH_TIME));
        waiForKillTime.setIntValue(PrefUtil.getInt(PrefUtil.SCRIPT_WAIT_FOR_KILL_REFRESH_TIME));
    }

    private void loadVTKProperties() {
        interactiveMemory.setIntValue(PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_MEMORY));
        interactiveTime.setIntValue(PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_TIME));
        transparencyMemory.setIntValue(PrefUtil.getInt(PrefUtil._3D_TRANSPARENCY_MEMORY));
    }

    private void loadMiscProperties() {
        if (Util.isUnix()) {
            defaultTerminal.setStringValue(PrefUtil.getString(PrefUtil.HELYX_DEFAULT_TERMINAL));
            defaultFileManager.setStringValue(PrefUtil.getString(PrefUtil.HELYX_DEFAULT_FILE_MANAGER));
            defaultHostFile.setSelected(PrefUtil.getBoolean(PrefUtil.DEFAULT_HOSTFILE_NONE));
        }
        hideEmptyPatches.setSelected(PrefUtil.getBoolean(PrefUtil.HIDE_EMPTY_PATCHES));
        maxLogRows.setIntValue(PrefUtil.getInt(PrefUtil.BATCH_MONITOR_DIALOG_MAX_ROW));
    }

    /*
     * Save
     */

    // public for test purposes only
    public void save() {
        savePathProperties();
        saveBatchProperties();
        saveVTKProperties();
        saveMiscProperties();
    }

    private void savePathProperties() {
        PrefUtil.setOpenFoamEntry(openFoamPanel.getFile());
        if (paraview) {
            PrefUtil.setParaViewEntry(paraViewPanel.getFile());
        }
        if (fieldview) {
            PrefUtil.setFieldViewEntry(fieldViewPanel.getFile());
        }
        if (ensight) {
            PrefUtil.setEnsightEntry(ensightPanel.getFile());
        }
    }

    private void saveBatchProperties() {
        PrefUtil.putInt(PrefUtil.SERVER_CONNECTION_MAX_TRIES, connectionTries.getIntValue());
        PrefUtil.putInt(PrefUtil.SERVER_CONNECTION_REFRESH_TIME, connectionRefresh.getIntValue());
        PrefUtil.putInt(PrefUtil.SERVER_WAIT_FOR_RUN_REFRESH_TIME, waitForRunTime.getIntValue());

        PrefUtil.putInt(PrefUtil.SCRIPT_RUN_REFRESH_TIME, scriptRefresh.getIntValue());
        PrefUtil.putInt(PrefUtil.SCRIPT_WAIT_FOR_KILL_REFRESH_TIME, waiForKillTime.getIntValue());
    }

    private void saveVTKProperties() {
        PrefUtil.putInt(PrefUtil._3D_LOCK_INTRACTIVE_MEMORY, interactiveMemory.getIntValue());
        PrefUtil.putInt(PrefUtil._3D_LOCK_INTRACTIVE_TIME, interactiveTime.getIntValue());
        PrefUtil.putInt(PrefUtil._3D_TRANSPARENCY_MEMORY, transparencyMemory.getIntValue());
    }

    private void saveMiscProperties() {
        if (Util.isUnix()) {
            PrefUtil.putString(PrefUtil.HELYX_DEFAULT_TERMINAL, defaultTerminal.getStringValue());
            PrefUtil.putString(PrefUtil.HELYX_DEFAULT_FILE_MANAGER, defaultFileManager.getStringValue());
            PrefUtil.putBoolean(PrefUtil.DEFAULT_HOSTFILE_NONE, defaultHostFile.isSelected());
        }
        PrefUtil.putBoolean(PrefUtil.HIDE_EMPTY_PATCHES, hideEmptyPatches.isSelected());
        PrefUtil.putInt(PrefUtil.BATCH_MONITOR_DIALOG_MAX_ROW, maxLogRows.getIntValue());
    }

    /*
     * Layout
     */

    private Component createCenterPanel() {
        JPanel pathsPanel = createPathsPanel();
        JPanel batchPanel = createBatchPanel();
        JPanel vtkPanel = createVTKPanel();
        JPanel miscPanel = createMiscPanel();

        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(pathsPanel);
        if (hasSolverPreferences) {
            builder.addComponent(batchPanel);
        }
        builder.addComponent(vtkPanel);
        builder.addComponent(miscPanel);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        container.add(builder.getPanel(), BorderLayout.CENTER);
        container.add(errorLabel = new JLabel(""), BorderLayout.SOUTH);
        errorLabel.setForeground(Color.RED.darker());
        errorLabel.setName("error.label");
        return container;
    }

    private JPanel createPathsPanel() {
        String ofPrompt = "";
        if (Util.isWindows()) {
            if (isOS) {
                ofPrompt = String.format(OPENFOAM_TOOLTIP_WINDOWS_OS, ApplicationInfo.getVendor());
            } else {
                ofPrompt = String.format(OPENFOAM_TOOLTIP_WINDOWS, ApplicationInfo.getVendor());
            }
        } else {
            if (isOS) {
                ofPrompt = String.format(OPENFOAM_TOOLTIP_LINUX_OS, ApplicationInfo.getVendor());
            } else {
                ofPrompt = String.format(OPENFOAM_TOOLTIP_LINUX, ApplicationInfo.getVendor());
            }
        }

        String ofTooltip = PREFIX_FOR_TOOLTIP + ofPrompt;

        PanelBuilder pathBuilder = new PanelBuilder();
        addResettableComponent(pathBuilder, CORE_FOLDER_LABEL, openFoamPanel = ComponentsFactory.fileField(SelectionMode.DIRS_ONLY, ofTooltip, ofPrompt, true), PrefUtil.OPENFOAM_KEY);
        openFoamPanel.addPropertyChangeListener(new UpdateErrorLabelListener());

        if (paraview) {
            String pvPrompt = Util.isWindows() ? PARAVIEW_TOOLTIP_WINDOWS : PARAVIEW_TOOLTIP_LINUX;
            String pvTooltip = PREFIX_FOR_TOOLTIP + pvPrompt;

            paraViewPanel = ComponentsFactory.fileField(SelectionMode.FILES_ONLY, pvTooltip, pvPrompt, true);
            paraViewPanel.addPropertyChangeListener(new UpdateErrorLabelListener());
            addResettableComponent(pathBuilder, PARA_VIEW_EXECUTABLE_LABEL, paraViewPanel, PrefUtil.PARAVIEW_KEY);
        }

        if (fieldview) {
            String fvPrompt = Util.isWindows() ? FIELDVIEW_TOOLTIP_WINDOWS : FIELDVIEW_TOOLTIP_LINUX;
            String fvTooltip = PREFIX_FOR_TOOLTIP + fvPrompt;

            fieldViewPanel = ComponentsFactory.fileField(SelectionMode.FILES_ONLY, fvTooltip, fvPrompt, true);
            fieldViewPanel.addPropertyChangeListener(new UpdateErrorLabelListener());
            addResettableComponent(pathBuilder, FIELD_VIEW_EXECUTABLE_LABEL, fieldViewPanel, PrefUtil.FIELDVIEW_KEY);
        }
        if (ensight) {
            String fvPrompt = Util.isWindows() ? ENSIGHT_TOOLTIP_WINDOWS : ENSIGHT_TOOLTIP_LINUX;
            String fvTooltip = PREFIX_FOR_TOOLTIP + fvPrompt;

            ensightPanel = ComponentsFactory.fileField(SelectionMode.FILES_ONLY, fvTooltip, fvPrompt, true);
            ensightPanel.addPropertyChangeListener(new UpdateErrorLabelListener());
            addResettableComponent(pathBuilder, EN_SIGHT_EXECUTABLE_LABEL, ensightPanel, PrefUtil.ENSIGHT_KEY);
        }
        JPanel panel = pathBuilder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(PATHS_LABEL));
        return panel;
    }

    private JPanel createBatchPanel() {
        PanelBuilder batchBuilder = new PanelBuilder();
        addResettableComponent(batchBuilder, CONNECTION_TRIES_LABEL, connectionTries = intField(), PrefUtil.SERVER_CONNECTION_MAX_TRIES);
        addResettableComponent(batchBuilder, CONNECTION_TRIES_INTERVAL_MSEC_LABEL, connectionRefresh = intField(), PrefUtil.SERVER_CONNECTION_REFRESH_TIME);
        addResettableComponent(batchBuilder, RUN_WAIT_TIME_LABEL, waitForRunTime = intField(), PrefUtil.SERVER_WAIT_FOR_RUN_REFRESH_TIME);
        addResettableComponent(batchBuilder, OUTPUT_LOG_REFRESH_INTERVAL_LABEL, scriptRefresh = intField(), PrefUtil.SCRIPT_RUN_REFRESH_TIME);
        addResettableComponent(batchBuilder, KILL_WAIT_TIME_LABEL, waiForKillTime = intField(), PrefUtil.SCRIPT_WAIT_FOR_KILL_REFRESH_TIME);
        batchBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(SERVER_LABEL));
        return batchBuilder.getPanel();
    }

    private JPanel createVTKPanel() {
        PanelBuilder vtkBuilder = new PanelBuilder();
        addResettableComponent(vtkBuilder, LOCK_INTERACTIVE_RENDER_FOR_MSEC_LABEL, interactiveTime = intField(), PrefUtil._3D_LOCK_INTRACTIVE_TIME);
        addResettableComponent(vtkBuilder, ENABLE_LOD_THRESHOLD_KB_LABEL, interactiveMemory = intField(), PrefUtil._3D_LOCK_INTRACTIVE_MEMORY);
        addResettableComponent(vtkBuilder, DISBLE_TRANSPARENCY_THRESHOLD_KB_LABEL, transparencyMemory = intField(), PrefUtil._3D_TRANSPARENCY_MEMORY);
        vtkBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(_3D_RENDERING_LABEL));
        return vtkBuilder.getPanel();
    }

    private JPanel createMiscPanel() {
        PanelBuilder miscBuilder = new PanelBuilder();
        if (Util.isUnix()) {
            addResettableComponent(miscBuilder, CUSTOM_TERMINAL_COMMAND_LABEL, defaultTerminal = stringField(), PrefUtil.HELYX_DEFAULT_TERMINAL);
            defaultTerminal.setPrompt(TERMINAL_TOOLTIP);

            addResettableComponent(miscBuilder, CUSTOM_FILE_MANAGER_LABEL, defaultFileManager = stringField(), PrefUtil.HELYX_DEFAULT_FILE_MANAGER);
            defaultFileManager.setPrompt(FILE_MANAGER_TOOLTIP);

            addResettableComponent(miscBuilder, DEFAULT_HOSTFILE_NONE_LABEL, defaultHostFile = checkField(), PrefUtil.DEFAULT_HOSTFILE_NONE);
            defaultHostFile.setToolTipText(DEFAULT_HOSTFILE_TOOLTIP);
        }
        addResettableComponent(miscBuilder, HIDE_EMPTY_PATCHES_LABEL, hideEmptyPatches = checkField(), PrefUtil.HIDE_EMPTY_PATCHES);
        addResettableComponent(miscBuilder, MAX_LOG_LINES_LABEL, maxLogRows = intField(), PrefUtil.BATCH_MONITOR_DIALOG_MAX_ROW);

        openDefaults = new JButton(new AbstractAction("Show Files") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dictDataFolder.toFile() != null && dictDataFolder.toFile().exists()) {
                    FileManagerSupport.open(dictDataFolder.toFile());
                }
            }
        });
        JButton resetButton = new JButton(new ViewAction("Reset", "Reset Files To Default") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileUtils.forceDeleteOnExit(new File(ApplicationInfo.getHome(), "dictData"));
                } catch (IOException e1) {
                } finally {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Restart " + ApplicationInfo.getName() + " to complete this action.");
                }
            }
        });
        miscBuilder.addComponent(DEFAULT_DICTIONARIES_LABEL, Componentizer.create().minAndMore(openDefaults).minToPref(resetButton).component());

        miscBuilder.getPanel().setBorder(BorderFactory.createTitledBorder(MISC_LABEL));
        return miscBuilder.getPanel();
    }

    private void addResettableComponent(PanelBuilder builder, String label, JComponent compToAdd, String prefKey) {
        JButton resetButton = createResetButton(compToAdd, prefKey);
        resetButton.setName(label + ".reset");
        compToAdd.setName(label);
        builder.addComponent(label, Componentizer.create().minAndMore(compToAdd).minToPref(resetButton).component());
    }

    private JButton createResetButton(final JComponent compToAdd, final String key) {
        return new JButton(new ViewAction("Reset", "Reset Preference To Default") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object value = PrefUtil.getDefaultValue(key);
                if (compToAdd instanceof IntegerField) {
                    resetIntegerField(compToAdd, value);
                } else if (compToAdd instanceof StringField) {
                    resetStringField(compToAdd, value);
                } else if (compToAdd instanceof JCheckBox) {
                    resetBooleanField(compToAdd, value);
                } else if (compToAdd instanceof DoubleField) {
                    resetDoubleField(compToAdd, value);
                } else if (compToAdd instanceof FileFieldPanel) {
                    if(key.equals(PrefUtil.OPENFOAM_KEY)){
                        resetOpenFoamFileField(compToAdd);
                    } else {
                        resetFileField(compToAdd);
                    }
                }
            }

            private void resetFileField(final JComponent compToAdd) {
                ((FileFieldPanel) compToAdd).setFile(null);
            }

            private void resetOpenFoamFileField(final JComponent compToAdd) {
                File[] openFoamDir = OpenFOAMEnvironment.getOpenFoamDir();
                if (Util.isVarArgsNotNullAndOfSize(1, openFoamDir)) {
                    ((FileFieldPanel) compToAdd).setFile(openFoamDir[0]);
                } else {
                    ((FileFieldPanel) compToAdd).setFile(null);
                }
            }

            private void resetDoubleField(final JComponent compToAdd, Object value) {
                if (value == null) {
                    ((DoubleField) compToAdd).setDoubleValue(0);
                } else {
                    double doubleValue = Double.parseDouble(String.valueOf(value));
                    ((DoubleField) compToAdd).setDoubleValue(doubleValue);
                }
            }

            private void resetBooleanField(final JComponent compToAdd, Object value) {
                if (value == null) {
                    ((JCheckBox) compToAdd).setSelected(false);
                } else {
                    boolean booleanValue = Boolean.valueOf(String.valueOf(value));
                    ((JCheckBox) compToAdd).setSelected(booleanValue);
                }
            }

            private void resetStringField(final JComponent compToAdd, Object value) {
                if (value == null) {
                    ((StringField) compToAdd).setStringValue("");
                } else {
                    String stringValue = String.valueOf(value);
                    ((StringField) compToAdd).setStringValue(stringValue);
                }
            }

            private void resetIntegerField(final JComponent compToAdd, Object value) {
                if (value == null) {
                    ((IntegerField) compToAdd).setIntValue(0);
                } else {
                    int intValue = Integer.parseInt(String.valueOf(value));
                    ((IntegerField) compToAdd).setIntValue(intValue);
                }
            }
        });
    }

    /*
     * Utils
     */

    private boolean isParaViewOk() {
        if (paraview) {
            return paraViewPanel.hasExistingFile();
        }
        return true;
    }

    private boolean isEnsightOk() {
        if (ensight) {
            return ensightPanel.hasExistingFile();
        }
        return true;
    }

    private boolean isFieldViewOk() {
        if (fieldview) {
            return fieldViewPanel.hasExistingFile();
        }
        return true;
    }

    public void show() {
        dialog.setVisible(true);
    }

    private class UpdateErrorLabelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("value")) {
                updateErrorLabel();
            }
        }

    }

    // For test purpose only
    public JDialog getDialog() {
        return dialog;
    }
}

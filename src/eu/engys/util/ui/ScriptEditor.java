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

package eu.engys.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import eu.engys.util.IOUtils;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.SelectionMode;

public class ScriptEditor {

    public enum Syntax {
        BASH, BATCH, PYTHON, JAVA, C
    };

    public static final String BAT_COMMENT = "rem ";
    public static final String SHELL_COMMENT = "# ";

    private static ScriptEditor instance;

    private JDialog dialog;
    private RSyntaxTextArea editor;
    private DocumentListener documentListener;

    private File file;
    private List<String> defaultScript;
    private boolean modified;
    private JButton okButton;

    public static ScriptEditor getInstance() {
        if (instance == null)
            instance = new ScriptEditor();
        return instance;
    }

    private ScriptEditor() {
        initEditor();
        initListeners();
    }

    private void initEditor() {
        this.editor = new RSyntaxTextArea();
        // End of line is changed in save method
        // This is used to split text in lines
        editor.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, IOUtils.EOL);
        editor.setCodeFoldingEnabled(true);
        editor.setAntiAliasingEnabled(true);
        editor.setBackground(new Color(240, 240, 240));
        editor.setName("codeEditor");
    }

    private void initListeners() {
        documentListener = new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentModified();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentModified();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentModified();
            }
        };
    }

    private void documentModified() {
        if (!modified) {
            if (dialog != null) {
                dialog.setTitle("*" + dialog.getTitle());
                this.modified = true;
            }
        }
    }

    public void show(Syntax syntax, Path scriptPath, List<String> defaultScript) {
        String style = "";
        switch (syntax) {
        case BASH:
            style = SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
            break;
        case BATCH:
            style = SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
            break;
        case PYTHON:
            style = SyntaxConstants.SYNTAX_STYLE_PYTHON;
            break;
        case C:
            style = SyntaxConstants.SYNTAX_STYLE_C;
            break;
        case JAVA:
            style = SyntaxConstants.SYNTAX_STYLE_JAVA;
            break;

        default:
            break;
        }
        editor.setSyntaxEditingStyle(style);
        show(scriptPath, defaultScript);
    }

    public void show(Path scriptPath, List<String> defaultScript) {
        this.defaultScript = defaultScript;
        this.file = scriptPath != null ? scriptPath.toFile() : null;
        this.editor.setSyntaxEditingStyle(Util.isWindowsScriptStyle() ? SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH : SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        ExecUtil.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                initDialog();
                load();
                dialog.setVisible(true);
            }
        });
    }

    private void initDialog() {
        dialog = new JDialog(UiUtil.getActiveWindow(), ModalityType.MODELESS);
        dialog.setName("script.editor.dialog");
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        dialog.getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
        dialog.getContentPane().doLayout();
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });

        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.getRootPane().setDefaultButton(okButton);
    }

    private JPanel createMainPanel() {
        RTextScrollPane sp = new RTextScrollPane(editor);
        sp.setFoldIndicatorEnabled(true);
        sp.setBorder(BorderFactory.createEmptyBorder());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(sp);
        return mainPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton resetButton = new JButton(new ResetAction());
        resetButton.setName("reset");
        leftPanel.add(resetButton);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        okButton = new JButton(new OKAction());
        okButton.setName("OK");
        rightPanel.add(okButton);
        
        JButton cancelButton = new JButton(new CancelAction());
        cancelButton.setName("cancel");
        rightPanel.add(cancelButton);

        panel.add(leftPanel);
        panel.add(rightPanel);

        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
        buttonsPanel.add(panel, BorderLayout.CENTER);

        return buttonsPanel;
    }

    private void closeDialog() {
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
    }

    private void load() {
        editor.getDocument().removeDocumentListener(documentListener);
        if (file != null && file.exists()) {
            try {
                // IOUtils.loadFromFile(editor, file, null, Charset.defaultCharset());
                editor.setText(IOUtils.readStringFromFile(file));
                editor.setCaretPosition(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.setTitle(file.getAbsolutePath());
        } else {
            editor.setText("");
            dialog.setTitle("newScript");
        }
        editor.getDocument().addDocumentListener(documentListener);
        this.modified = false;
    }

    private void save() {
        if (file != null && file.exists()) {
            try {
                IOUtils.writeStringToFile(file, editor.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.setTitle(file.getAbsolutePath());
        } else {
            saveAs();
        }
    }

    private void saveAs() {
        File lastDir = PrefUtil.getWorkDir(PrefUtil.WORK_DIR);
        HelyxFileChooser fc = new HelyxFileChooser(lastDir.getAbsolutePath());
        fc.setParent(dialog);
        fc.setSelectionMode(SelectionMode.FILES_ONLY);
        ReturnValue retVal = fc.showSaveAsDialog();
        if (retVal.isApprove()) {
            File file = fc.getSelectedFile();
            if (file != null) {
                if (file.exists()) {
                    int answer = JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), "File already exists. Overwrite?", "File Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        setFile(file);
                        save();
                    }
                } else {
                    try {
                        file.createNewFile();
                        setFile(file);
                        save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                PrefUtil.putFile(PrefUtil.WORK_DIR, file.getParentFile());
            }
        }
    }

    private void setFile(File file) {
        this.file = file;
    }

    private class OKAction extends AbstractAction {
        public OKAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
            closeDialog();
        }
    }

    private class CancelAction extends AbstractAction {
        public CancelAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            closeDialog();
        }

    }

    private class ResetAction extends AbstractAction {
        public ResetAction() {
            super("Reset");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (file != null) {
                try {
                    File defaultFile = File.createTempFile("xxx", null);
                    IOUtils.writeLinesToFile(defaultFile, getDefaultFileLinesAndOldOnesCommented());
                    editor.setText(IOUtils.readStringFromFile(defaultFile));
                    editor.setCaretPosition(0);
                    FileUtils.deleteQuietly(defaultFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        private List<String> getDefaultFileLinesAndOldOnesCommented() throws IOException {
            List<String> commentedLines = getCurrentFileCommentedLines();

            List<String> newLines = new ArrayList<>();
            newLines.addAll(defaultScript);
            newLines.add("");
            newLines.addAll(commentedLines);
            return newLines;
        }

        private List<String> getCurrentFileCommentedLines() throws IOException {
            String comment = Util.isWindowsScriptStyle() ? BAT_COMMENT : SHELL_COMMENT;
            List<String> commentedLines = new ArrayList<>();
            for (String line : FileUtils.readLines(file)) {
                commentedLines.add(new StringBuilder(comment).append(line).toString());
            }
            return commentedLines;
        }
    }
}

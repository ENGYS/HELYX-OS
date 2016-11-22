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

package eu.engys.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class FileEditor {

    public static final String FILE_EDITOR_DIALOG = "file.editor.dialog";
    public static final String FILE_EDITOR = "fileEditor";

    private static FileEditor instance;

    private JDialog dialog;
    private RSyntaxTextArea editor;
    private DocumentListener documentListener;

    private String fileName;
    private List<String> fileContent;
    private boolean modified;
    
    private Runnable onDisposeRunnable;

    private Runnable onOKRunnable;

    private JButton okButton;

    public static FileEditor getInstance() {
        if (instance == null)
            instance = new FileEditor();
        return instance;
    }

    private FileEditor() {
        initEditor();
        initListeners();
    }

    private void initEditor() {
        this.editor = new RSyntaxTextArea();
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        editor.setCodeFoldingEnabled(true);
        editor.setAntiAliasingEnabled(true);
        editor.setBackground(new Color(240, 240, 240));
        editor.setName(FILE_EDITOR);
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
            dialog.setTitle("*" + dialog.getTitle());
            this.modified = true;
        }
    }

    public void show(final Window parent, List<String> fileContent, String fileName) {
        show(parent, fileContent, fileName, null, null, null);
    }
    
    public void show(final Window parent, List<String> fileContent, String fileName, final Runnable onShowRunnable, Runnable onDisposeRunnable, Runnable onOKRunnable) {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.onDisposeRunnable = onDisposeRunnable;
        this.onOKRunnable = onOKRunnable;
        ExecUtil.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                initDialog(parent);
                load();
                if (onShowRunnable != null) {
                    onShowRunnable.run();
                }
                dialog.setVisible(true);
            }
        });
    }

    private void initDialog(Window parent) {
        dialog = new JDialog(parent != null ? parent : UiUtil.getActiveWindow(), ModalityType.MODELESS);
        dialog.setName(FILE_EDITOR_DIALOG);
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
        RTextScrollPane scrollPane = new RTextScrollPane(editor);
        scrollPane.setFoldIndicatorEnabled(true);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane);
        return mainPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        okButton = new JButton(new OKAction());
        okButton.setName("OK");

        JButton cancelButton = new JButton(new CancelAction());
        cancelButton.setName("Cancel");

        panel.add(okButton);
        panel.add(cancelButton);

        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
        buttonsPanel.add(panel, BorderLayout.CENTER);

        return buttonsPanel;
    }

    private void closeDialog() {
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
        if (onDisposeRunnable != null) {
            onDisposeRunnable.run();
        }
    }

    private void load() {
        editor.getDocument().removeDocumentListener(documentListener);
        editor.setText("");
        try {
            for (String line : fileContent) {
                editor.append(line + "\n");
            }
            editor.setCaretPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setTitle(fileName);
        editor.getDocument().addDocumentListener(documentListener);
        this.modified = false;
    }

    private void save() {
        fileContent.clear();
        for (String line : editor.getText().split("\\n")){
            fileContent.add(line);
        }
        dialog.setTitle(fileName);
    }

    private class OKAction extends AbstractAction {
        public OKAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
            if(onOKRunnable != null){
                onOKRunnable.run();
            }
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

}

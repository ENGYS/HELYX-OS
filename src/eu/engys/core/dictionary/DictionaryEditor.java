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


package eu.engys.core.dictionary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

import eu.engys.core.dictionary.parser.DictionaryReader2;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.UiUtil;

public class DictionaryEditor {

    public static final String CODE_EDITOR = "codeEditor";
    public static final String DICTIONARY_EDITOR_DIALOG_NAME = "dictionary.editor.dialog";

    private static DictionaryEditor instance;

    private JDialog dialog;
    private RSyntaxTextArea editor;
    private DocumentListener documentListener;

    private Dictionary dictionary;
    private boolean modified;

    private Runnable onDisposeRunnable;

    private Runnable onOKRunnable;

    private JButton okButton;

    public static DictionaryEditor getInstance() {
        if (instance == null)
            instance = new DictionaryEditor();
        return instance;
    }

    private DictionaryEditor() {
        initEditor();
        initListeners();
    }

    private void initEditor() {
        this.editor = new RSyntaxTextArea();
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        editor.setCodeFoldingEnabled(true);
        editor.setAntiAliasingEnabled(true);
        editor.setBackground(new Color(240, 240, 240));
        editor.setName(CODE_EDITOR);
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

    public void show(Window parent, Dictionary dictionary) {
        show(parent, dictionary, null, null, null);
    }

    public void show(final Window parent, Dictionary dictionary, final Runnable onShowRunnable, Runnable onDisposeRunnable, Runnable onOKRunnable) {
        this.dictionary = dictionary;
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
        dialog.setName(DICTIONARY_EDITOR_DIALOG_NAME);
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
        try {
            editor.setText(dictionary.toString());
            editor.setCaretPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setTitle(dictionary.getName());
        editor.getDocument().addDocumentListener(documentListener);
        this.modified = false;
    }

    private void save() {
        dictionary.clear();
        // use name not instanceof!
        if (dictionary.getName().equals(BlockMeshDict.BLOCK_DICT)) {
            new DictionaryReader2(dictionary).read(editor.getText(), true);
        } else {
            new DictionaryReader(dictionary).read(editor.getText(), true);
        }
        dialog.setTitle(dictionary.getName());
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

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
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.textfields.FileTextField;

public class FileFieldPanel extends JPanel {
    private FileTextField textField;
    private JButton button;

    public FileFieldPanel(SelectionMode mode, String tooltip, String prompt, boolean selectFile) {
        super(new BorderLayout(4, 4));
        setOpaque(false);

        textField = new FileTextField();
        textField.setEditable(false);
        textField.setFocusable(false);
        textField.setToolTipText(tooltip);
        textField.setPrompt(prompt);

        button = createButtonFileChooser(selectFile, textField, mode);
        add(textField, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        textField.setName(name + ".text");
        button.setName(name + ".button");
    }

    public boolean hasExistingFile() {
        return textField.getText().isEmpty() || textField.hasValidFile();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (textField != null)
            textField.addPropertyChangeListener(listener);
    }

    public String getFilePath() {
        File file = textField.getValue();
        return file != null ? file.getPath() : "";
    }

    public void setFilePath(String path) {
        textField.setValue(new File(path == null ? "" : path));
    }

    public File getFile() {
        return textField.getValue();
    }

    public void setFile(File file) {
        textField.setValue(file);
    }

    private JButton createButtonFileChooser(boolean selectFile, FileTextField textField, SelectionMode mode) {
        JButton button = new JButton(new ChooseFileAction(selectFile, textField, mode));
        Dimension prefSize = button.getPreferredSize();
        button.setPreferredSize(new Dimension(24, prefSize.height));
        return button;
    }

    public FileTextField getTextField() {
        return textField;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        button.setEnabled(enabled);
    }
}

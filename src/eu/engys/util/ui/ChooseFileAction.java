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

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import eu.engys.util.Symbols;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.textfields.FileTextField;

public class ChooseFileAction extends AbstractAction {

    private final FileTextField textField;
    private final SelectionMode mode;
    private boolean selectFile;

    ChooseFileAction(boolean selectFile, FileTextField textField, SelectionMode mode) {
        super(Symbols.DOTS);
        this.selectFile = selectFile;
        this.textField = textField;
        this.mode = mode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = textField.getValue();
        HelyxFileChooser chooser = null;
        if (selectFile) {
            chooser = new HelyxFileChooser();
            chooser.selectFile(file);
        } else {
            if (file != null) {
                chooser = new HelyxFileChooser(file.getAbsolutePath());
            } else {
                chooser = new HelyxFileChooser();
            }
        }
        chooser.setParent(SwingUtilities.getWindowAncestor(textField));

        if (mode != null) {
            chooser.setSelectionMode(mode);
        }

        ReturnValue retVal = chooser.showOpenDialog();
        if (retVal.isApprove()) {
            textField.setValue(chooser.getSelectedFile());
        }

    }

}

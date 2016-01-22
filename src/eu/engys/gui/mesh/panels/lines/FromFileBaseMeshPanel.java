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

package eu.engys.gui.mesh.panels.lines;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.Controller;
import eu.engys.core.dictionary.FileEditor;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.RemoveSurfaceEvent;
import eu.engys.gui.mesh.actions.RunBlockMeshAction;
import eu.engys.util.LineSeparator;
import eu.engys.util.PrefUtil;
import eu.engys.util.Util;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ResourcesUtil;

public class FromFileBaseMeshPanel {

    private static final Logger logger = LoggerFactory.getLogger(FromFileBaseMeshPanel.class);

    public static final HelyxFileFilter BLOCKMESHDICT_FILE_FILTER = new HelyxFileFilter("Block Mesh Dictionary", BlockMeshDict.BLOCK_DICT);

    public static final String FROM_FILE_LABEL = "From File";
    
    public static final String EDIT_LABEL = "Edit";
    public static final String IMPORT_LABEL = "Import";
    public static final String CREATE_LABEL = "Create";

    private Model model;
    private Controller controller;
    private DictionaryPanelBuilder builder;
    private JButton editButton, previewButton, importButton;

    public FromFileBaseMeshPanel(Model model, Controller controller, DictionaryPanelBuilder builder) {
        this.model = model;
        this.controller = controller;
        this.builder = builder;
        builder.startGroup(FROM_FILE_LABEL);
        layoutComponents();
        builder.endGroup();
    }

    private void layoutComponents() {
        previewButton = new JButton(new RunBlockMeshAction(model, controller));
        previewButton.setName(CREATE_LABEL);

        editButton = new JButton(new AbstractAction(EDIT_LABEL, ResourcesUtil.getIcon("mesh.create.edit.icon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showEditor();
                } catch (IOException ex) {
                    logger.error("Unable to open blockMeshDict editor", ex.getMessage());
                }
            }
        });
        editButton.setName(EDIT_LABEL);

        importButton = new JButton(new AbstractAction(IMPORT_LABEL, ResourcesUtil.getIcon("mesh.import.icon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelyxFileChooser fc = createFileChooser();
                fc.showOpenDialog();
            }
        });
        importButton.setName(IMPORT_LABEL);
        builder.addSeparator("");
        builder.addComponent(importButton, editButton, previewButton);
    }
    
    public void save(){
        model.getProject().getSystemFolder().getBlockMeshDict().setFromFile(true);
        model.getGeometry().setAutoBoundingBox(false);
    }
    
    public void updateBlock(){
        if (model.getGeometry().hasBlock()) {
            EventManager.triggerEvent(this, new RemoveSurfaceEvent(model.getGeometry().getBlock()));
            model.getGeometry().setBlock(Geometry.FAKE_BLOCK);
            model.blockChanged();
        }
    }

    private HelyxFileChooser createFileChooser() {
        String initialPath = PrefUtil.getWorkDir(PrefUtil.LAST_IMPORT_DIR).getAbsolutePath();
        HelyxFileChooser chooser = new HelyxFileChooser(initialPath) {
            @Override
            public ReturnValue showOpenDialog() throws HeadlessException {
                ReturnValue retVal = super.showOpenDialog(BLOCKMESHDICT_FILE_FILTER);
                if (retVal.isApprove()) {
                    File newBlockDictFile = getSelectedFile();
                    PrefUtil.putFile(PrefUtil.LAST_IMPORT_DIR, newBlockDictFile.getParentFile());
                    try {
                        importBlockMeshDict(newBlockDictFile);
                    } catch (IOException e) {
                        logger.error("Error importing blockMeshDict from file" + e.getMessage());
                    }
                }
                return retVal;
            }

        };
        chooser.setSelectionMode(SelectionMode.FILES_ONLY);
        chooser.setTitle("Select " + BlockMeshDict.BLOCK_DICT + " File");
        chooser.setParent(SwingUtilities.getWindowAncestor(builder.getPanel()));
        return chooser;
    }

    private void importBlockMeshDict(File toImportBlockMeshDict) throws IOException {
        List<String> newBlockMeshDictContent = FileUtils.readLines(toImportBlockMeshDict);
        File currentBlockMeshDict = new File(model.getProject().getSystemFolder().getFileManager().getFile(), BlockMeshDict.BLOCK_DICT);
        newBlockMeshDictContent.add(BlockMeshDict.FROM_FILE_LINE);

        String lineEnding = Util.isWindowsScriptStyle() ? LineSeparator.DOS.getSeparator() : LineSeparator.UNIX.getSeparator();
        FileUtils.writeLines(currentBlockMeshDict, null, newBlockMeshDictContent, lineEnding);

        showEditor();
    }

    private void showEditor() throws IOException {
        final File blockMeshDictFile = new File(model.getProject().getSystemFolder().getFileManager().getFile(), BlockMeshDict.BLOCK_DICT);
        final List<String> lines = FileUtils.readLines(blockMeshDictFile);

        Runnable onShowRunnable = new Runnable() {
            @Override
            public void run() {
                importButton.setEnabled(false);
                editButton.setEnabled(false);
                previewButton.setEnabled(false);
            }
        };
        Runnable onDisposeRunnable = new Runnable() {
            @Override
            public void run() {
                importButton.setEnabled(true);
                editButton.setEnabled(true);
                previewButton.setEnabled(true);
                try {
                    String lineEnding = Util.isWindowsScriptStyle() ? LineSeparator.DOS.getSeparator() : LineSeparator.UNIX.getSeparator();
                    FileUtils.writeLines(blockMeshDictFile, null, lines, lineEnding);
                } catch (IOException e) {
                    logger.error("Error saving blockMeshDict" + e.getMessage());
                }
            }
        };

        FileEditor.getInstance().show(SwingUtilities.getWindowAncestor(builder.getPanel()), lines, BlockMeshDict.BLOCK_DICT, onShowRunnable, onDisposeRunnable, null);
    }

}

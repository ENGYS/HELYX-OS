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
package eu.engys.gui.mesh.panels.lines;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.util.ColorUtil;
import eu.engys.util.PrefUtil;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.filechooser.util.HelyxFileFilter;
import eu.engys.util.filechooser.util.SelectionMode;
import eu.engys.util.ui.ExecUtil;

public class ImportFeatureLineAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(ImportFeatureLineAction.class);

    public static final String FROM_FILE_LABEL = "Open";
    private Model model;

    public ImportFeatureLineAction(Model model) {
        super(FROM_FILE_LABEL);
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HelyxFileChooser fc = getFeatureLinesFileChooser();
        HelyxFileFilter filter = new HelyxFileFilter("EMesh File (*.eMesh, *.eMesh.gz)", "eMesh", "eMesh.gz");
        fc.setSelectionMode(SelectionMode.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);

        ReturnValue returnedValue = fc.showOpenDialog(filter);

        if (returnedValue.isApprove()) {
            importFiles(fc.getSelectedFiles());
        }
    }

    private HelyxFileChooser getFeatureLinesFileChooser() {
        File lastDir = PrefUtil.getWorkDir(PrefUtil.LAST_IMPORT_DIR);
        HelyxFileChooser fc = new HelyxFileChooser(lastDir.getAbsolutePath());
        fc.setMultiSelectionEnabled(true);
        fc.setSelectionMode(SelectionMode.FILES_ONLY);
        return fc;
    }

    private void importFiles(File[] files) {
        if (files != null && files.length > 0) {
            PrefUtil.putFile(PrefUtil.LAST_IMPORT_DIR, files[0].getParentFile());

            for (File file : files) {
                importFile(file);
            }
        }
    }

    public void importFile(final File file) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                // if (!alreadyImported(file)) {
                FeatureLine line = model.getGeometry().getFactory().readLine(file);
                addLine(line);
                // }
            }
        });
    }

    public void addLine(FeatureLine line) {
        logger.debug("ADD LINE " + line);
        line.setColor(nextColor());
        model.getGeometry().addLine(line);
        model.geometryChanged(line);

        EventManager.triggerEvent(this, new AddSurfaceEvent(line));
    }

    private Color nextColor() {
        return ColorUtil.getColor(model.getGeometry().getLines().size());
    }
}

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
package eu.engys.gui.mesh.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.util.ArchiveUtils;
import eu.engys.util.TempFolder;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public abstract class AddSTL {

    private ProgressMonitor monitor;
    private Model model;

    public AddSTL(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void execute() {
        STLFileChooserWrapper fc = new STLFileChooserWrapper(true);
        ReturnValue returnedValue = fc.showOpenDialog();

        if (returnedValue.isApprove()) {
            final File[] files = fc.getSelectedFiles();
            final AffineTransform[] transformations = fc.getSelectedTransform();

            boolean filesOK = files != null && files.length > 0;
            boolean transformationsOK = transformations != null && transformations.length > 0;
            boolean sameLength = filesOK && transformationsOK && transformations.length == files.length;

            if (sameLength) {
                monitor.setIndeterminate(false);
                monitor.start("Loading STL Files", false, new Runnable() {
                    @Override
                    public void run() {
                        List<Stl> stls = new ArrayList<>();
                        for (int i = 0; i < files.length; i++) {
                            File file = files[i];
                            AffineTransform transform = transformations[i];

                            if (ArchiveUtils.isArchive(file)) {
                                File tmpFolder = TempFolder.get(AddSTL.class.getSimpleName());
                                List<File> extractedFiles = ArchiveUtils.unarchive(file, tmpFolder);
                                for (File target : extractedFiles) {
                                    monitor.info(String.format("Loading %s-%s ", file.getAbsolutePath(), target.getName()));
                                    Stl stl = model.getGeometry().getFactory().readSTL(target, monitor);
                                    stl.setTransformation(transform);
                                    stls.add(stl);
                                }
                                FileUtils.deleteQuietly(tmpFolder);
                            } else {
                                monitor.info(String.format("Loading %s ", file.getAbsolutePath()));
                                Stl stl = model.getGeometry().getFactory().readSTL(file, monitor);
                                stl.setTransformation(transform);
                                stls.add(stl);
                            }
                        }
                        postLoad(stls);
                        monitor.end();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Unable to load selected STLs", "File Type Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public abstract void postLoad(List<Stl> stls);

}

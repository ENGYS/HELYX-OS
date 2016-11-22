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

import javax.swing.JOptionPane;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.surface.StlArea;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public abstract class AddSTLArea {

    private ProgressMonitor monitor;
    private Model model;

    public AddSTLArea(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void execute() {
        STLFileChooserWrapper fc = new STLFileChooserWrapper(false);
        ReturnValue returnedValue = fc.showOpenDialog();

        if (returnedValue.isApprove()) {
            final File file = fc.getSelectedFile();
            final AffineTransform[] transformations = fc.getSelectedTransform();

            boolean filesOK = file != null;
            boolean transformationsOK = transformations != null && transformations.length > 0;
            boolean isOK = filesOK && transformationsOK;

            if (isOK) {
                monitor.setIndeterminate(false);
                monitor.start("Loading STL File", false, new Runnable() {
                    @Override
                    public void run() {
                        AffineTransform transform = transformations[0];

                        monitor.info(String.format("Loading %s ", file.getAbsolutePath()));
                        StlArea stlArea = model.getGeometry().getFactory().readSTLArea(file, monitor);
                        stlArea.setTransformation(transform);
                        postLoad(stlArea);
                        monitor.end();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Unable to load selected STLs", "File Type Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public abstract void postLoad(StlArea stlArea);

}

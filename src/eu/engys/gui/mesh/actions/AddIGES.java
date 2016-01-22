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

package eu.engys.gui.mesh.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import eu.engys.core.controller.actions.RunCommand;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.stl.ImportIGES;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.util.TempFolder;
import eu.engys.util.filechooser.AbstractFileChooser.ReturnValue;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public abstract class AddIGES {

    private ProgressMonitor monitor;
    private Model model;

    public AddIGES(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void execute() {
        IGESFileChooserWrapper fc = new IGESFileChooserWrapper();
        ReturnValue returnedValue = fc.showOpenDialog();

        if (returnedValue.isApprove()) {
            final File[] files = fc.getSelectedFiles();
            final AffineTransform[] transformations = fc.getSelectedTransform();
            final boolean split = fc.getIGESAccessory().getSplit();
            final double precision = fc.getIGESAccessory().getPrecision();

            boolean filesOK = files != null && files.length > 0;
            boolean transformationsOK = transformations != null && transformations.length > 0;
            boolean sameLength = transformations.length == files.length;
            if (filesOK && transformationsOK && sameLength) {
                try {
                    convertIGES(files, transformations, split, precision);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Unable to load selected IGES: " + e.getMessage(), "File Type Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Unable to load selected IGES", "File Type Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void convertIGES(File[] igesToCopy, final AffineTransform[] transformations, final boolean split, double precision) throws IOException {
        final File[] copiedIgesList = new File[igesToCopy.length];
        final File[] createdStlList = new File[igesToCopy.length];
        final File tmpFolder = TempFolder.get(AddIGES.class.getSimpleName());

        for (int i = 0; i < igesToCopy.length; i++) {
            File iges = igesToCopy[i];
            FileUtils.copyFileToDirectory(iges, tmpFolder);

            copiedIgesList[i] = new File(tmpFolder, iges.getName());
            createdStlList[i] = new File(tmpFolder, FilenameUtils.removeExtension(iges.getName()) + ".stl");
        }

        Runnable loadSTLRunnable = new Runnable() {
            @Override
            public void run() {
                monitor.setIndeterminate(false);
                monitor.start("Loading IGES Files", false, new Runnable() {
                    @Override
                    public void run() {
                        Map<String, File[]> fileMap = getSTLFilesToImport(createdStlList, split);
                        List<String> keySet = new ArrayList<String>(fileMap.keySet());
                        List<Stl> stls = new ArrayList<>();
                        for (int i = 0; i < keySet.size(); i++) {
                            String key = keySet.get(i);
                            for (File stlFile : fileMap.get(key)) {
                                monitor.info(String.format("Loading %s ", stlFile.getAbsolutePath()));

                                Stl stl = model.getGeometry().getFactory().readSTL(stlFile, monitor);
                                stl.setTransformation(transformations[i]);
                                stls.add(stl);

                            }
                        }
                        postLoad(stls);
                        FileUtils.deleteQuietly(tmpFolder);
                        monitor.end();
                    }
                });
            }
        };

        RunCommand command = new ImportIGES(model, loadSTLRunnable, copiedIgesList, createdStlList, split, precision);
        command.beforeExecute();
        command.executeClient();
    }

    private Map<String, File[]> getSTLFilesToImport(File[] createdStlList, boolean split) {
        final Map<String, File[]> map = new LinkedHashMap<String, File[]>();
        for (final File stl : createdStlList) {
            if (split) {
                File[] stlComponents = stl.getParentFile().listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith(FilenameUtils.removeExtension(stl.getName())) && name.contains("Component");
                    }
                });
                map.put(stl.getName(), stlComponents);
            } else {
                map.put(stl.getName(), new File[] { stl });
            }
        }
        return map;
    }

    public abstract void postLoad(List<Stl> stls);

}

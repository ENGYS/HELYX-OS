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
package eu.engys.parallelworks;

import static eu.engys.parallelworks.ParallelWorksData.PROJECT_ARCHIVE_FILE_NAME;
import static eu.engys.parallelworks.ParallelWorksData.PROJECT_URL_FILE_NAME;

import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.util.ArchiveUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;

public class ParallelWorksDownloader {

    private static final Logger logger = LoggerFactory.getLogger(ParallelWorksDownloader.class);

    private Model model;
    private ProgressMonitor monitor;

    public ParallelWorksDownloader(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void downloadCloudResults() {
        monitor.setIndeterminate(true);
        monitor.start("Downloading results...", true, new Runnable() {
            @Override
            public void run() {
                File caseURLFile = new File(model.getProject().getBaseDir(), PROJECT_URL_FILE_NAME);
                URL remoteCaseURL = extractCaseURL(caseURLFile);
                if (remoteCaseURL != null) {
                    File destination = downloadCase(remoteCaseURL);
                    if (destination != null) {
                        ArchiveUtils.unarchive(destination, model.getProject().getBaseDir());
                    }
                }
                ParallelWorksCleaner.cleanBaseDir(model.getProject().getBaseDir());
                monitor.setIndeterminate(false);
                monitor.end();
            }
        });
    }

    private URL extractCaseURL(File caseURLFile) {
        try {
            String fileContent = FileUtils.readFileToString(caseURLFile);
            return new URL(fileContent);
        } catch (Exception e) {
            logger.error(e.getMessage());
            showErrorMessage("Unable to download remote case. Cause: " + e.getMessage());
        }
        return null;
    }

    private File downloadCase(URL remoteCaseURL) {
        File destination = new File(model.getProject().getBaseDir(), PROJECT_ARCHIVE_FILE_NAME);
        try {
            FileUtils.copyURLToFile(remoteCaseURL, destination);
            return destination;
        } catch (Exception e) {
            logger.error(e.getMessage());
            showErrorMessage("Unable to download remote case. Cause: " + e.getMessage());
        }
        return null;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showConfirmDialog(UiUtil.getActiveWindow(), message, "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
    }

}

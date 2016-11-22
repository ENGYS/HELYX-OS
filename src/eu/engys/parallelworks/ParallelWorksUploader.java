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

import static eu.engys.parallelworks.ParallelWorksData.CASE_ENV_NAME;
import static eu.engys.parallelworks.ParallelWorksData.INPUTS_ARCHIVE_FILE_NAME;
import static eu.engys.util.IOUtils.LNX_EOL;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import eu.engys.util.ArchiveUtils;
import eu.engys.util.PrefUtil;
import eu.engys.util.progress.ProgressMonitor;

public class ParallelWorksUploader {//

    private ProgressMonitor monitor;

    public ParallelWorksUploader(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public void uploadCloudResults(final String command, final Map<String, String> environment) {
        monitor.setIndeterminate(true);
        monitor.start("Uploading case...", true, new Runnable() {
            @Override
            public void run() {
                try {
                    File baseDir = new File(environment.get("CASE"));

                    writeCaseEnvFile(baseDir, environment);
                    compressCase(baseDir, command);
                    uploadCase(baseDir);

                } catch (Exception e) {
                    monitor.error(e.getMessage());
                } finally {
                    monitor.setIndeterminate(false);
                    monitor.end();
                }
            }
        });
    }

    private void writeCaseEnvFile(File baseDir, Map<String, String> environment) {
        File caseEnv = new File(baseDir, CASE_ENV_NAME);
        FileUtils.deleteQuietly(caseEnv);
        try {
            FileUtils.writeStringToFile(caseEnv, "export CASE=" + environment.get("CASE") + LNX_EOL, false);
            FileUtils.writeStringToFile(caseEnv, "export NP=" + environment.get("NP") + LNX_EOL, true);
            FileUtils.writeStringToFile(caseEnv, "export LOG=" + environment.get("LOG") + LNX_EOL, true);
            FileUtils.writeStringToFile(caseEnv, "export ENV_LOADER=" + "/opt/openfoam4/etc/bashrc" + LNX_EOL, true);
            FileUtils.writeStringToFile(caseEnv, "export VENDOR_HOME=" + "/opt" + LNX_EOL, true);
            FileUtils.writeStringToFile(caseEnv, "export PV_VERSION=" + environment.get("PV_VERSION") + LNX_EOL, true);
            FileUtils.writeStringToFile(caseEnv, "export MACHINEFILE=" + environment.get("MACHINEFILE") + LNX_EOL, true);
            FileUtils.writeStringToFile(caseEnv, "export SOLVER=" + environment.get("SOLVER") + LNX_EOL, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compressCase(File baseDir, final String command) {
        File baseDirArchive = new File(baseDir, INPUTS_ARCHIVE_FILE_NAME);

        FileUtils.deleteQuietly(baseDirArchive);
        ArchiveUtils.tarGZ(baseDirArchive, baseDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.equals(INPUTS_ARCHIVE_FILE_NAME)) {
                    return false;
                } else if (command.startsWith("mesh") && name.startsWith("processor")) {
                    return false;
                } else {
                    try {
                        Double.parseDouble(name);
                        return false;
                    } catch (NumberFormatException e) {
                        return true;
                    }
                }

            }
        }));
    }

    private void uploadCase(File baseDir) throws IOException, InterruptedException {
        File baseDirArchive = new File(baseDir, INPUTS_ARCHIVE_FILE_NAME);
        String workspace = PrefUtil.getString(PrefUtil.PW_WORKSPACE);
        ParallelWorksClient client = ParallelWorksClient.getInstance();
        String workspaceID = client.getWorkspaceID(workspace);
        client.uploadDataset(workspaceID, baseDirArchive);
        // Wait for upload to finish
        while (!client.getDatasetState().equals("ok")) {
            Thread.sleep(1000);
        }
    }

}

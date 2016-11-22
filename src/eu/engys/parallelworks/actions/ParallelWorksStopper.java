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
package eu.engys.parallelworks.actions;

import java.io.File;
import java.io.IOException;

import eu.engys.core.controller.actions.Stopper;
import eu.engys.core.executor.Executor;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ControlDict;
import eu.engys.parallelworks.ParallelWorksClient;

public class ParallelWorksStopper extends Stopper {

    public ParallelWorksStopper(Model model, Executor executor) {
        super(model, executor);
    }

    @Override
    protected void waitForExecutorToStop() throws Exception {
        uploadControlDict();
        super.waitForExecutorToStop();
    }

    private void uploadControlDict() throws IOException {
        File controlDict = model.getProject().getSystemFolder().getFileManager().getFile(ControlDict.CONTROL_DICT);
        ParallelWorksClient.getInstance().uploadFileToJob(controlDict);
    }

}

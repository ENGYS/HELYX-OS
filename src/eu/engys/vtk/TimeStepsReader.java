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
package eu.engys.vtk;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.project.Model;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkCompositeDataPipeline;
import vtk.vtkExecutive;
import vtk.vtkInformation;
import vtk.vtkInformationDoubleVectorKey;

public class TimeStepsReader {

    private Model model;
    private ProgressMonitor monitor;

    private List<Double> timesteps;

    public TimeStepsReader(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void read(double timeStep) {
        VTKOpenFOAMReader reader = new VTKOpenFOAMReader(model.getProject().getBaseDir(), model.getProject().isParallel(), monitor);//, "Time Steps");
        reader.UpdateInformation();
        reader.ReadInternalMeshOff();
        reader.ReadPatchesOff();
        reader.ReadZonesOff();
        reader.setTimeStep(timeStep);
        reader.Update();

        readTimeSteps(reader);
        reader.Delete();
    }

    private void readTimeSteps(VTKOpenFOAMReader reader) {
        vtkExecutive exe = reader.GetExecutive();
        vtkCompositeDataPipeline pipeline = (vtkCompositeDataPipeline) exe;
        vtkInformation outInfo = exe.GetOutputInformation(0);

        vtkInformationDoubleVectorKey timeStepsKey = pipeline.TIME_STEPS();
        int nTimeSteps = outInfo.Length(timeStepsKey); // Get the number of time
                                                       // steps
        List<Double> ts = new ArrayList<>();
        for (int i = 0; i < nTimeSteps; i++) {
            double timeValue = outInfo.Get(timeStepsKey, i);
            ts.add(Double.valueOf(timeValue));
        }
        this.timesteps = ts;
    }

    public List<Double> getTimesteps() {
        return timesteps;
    }

}

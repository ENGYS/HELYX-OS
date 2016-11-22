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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import eu.engys.core.project.zero.fields.Fields;
import eu.engys.util.progress.ProgressMonitor;
import vtk.vtkCompositeDataPipeline;
import vtk.vtkDataObject;
import vtk.vtkDataSet;
import vtk.vtkExecutive;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkPOpenFOAMReader;

public class VTKOpenFOAMReader {

    public static boolean debug = false;

    // public static void main(String[] args) {
    // LoggerUtil.initTestLogger(Level.DEBUG);
    // VTKSettings.LoadAllNativeLibraries();
    // Model model = new Model();
    // model.init();
    // // model.setProject(openFOAMProject.newParallelProject(new File("/home/stefano/ENGYS/examples/HELYX3/singleChannelPump_static_run")));
    // model.setProject(openFOAMProject.newParallelProject(new File("/home/stefano/ENGYS/examples/HELYX3/ECOMARINE/testKCS_01_parallel_01")));
    //
    // VTKOpenFOAMReader reader = new VTKOpenFOAMReader(model, new SilentMonitor(), "");
    // reader.ReadInternalMeshOff();
    // reader.ReadPatchesOff();
    // reader.ReadZonesOff();
    // reader.setTimeStep(0.0);
    // // reader.UpdateInformation();
    // reader.Update();
    // // reader.UpdateExtent();
    //
    // }

    public static boolean decomposePolyhedra = true;

    private vtkPOpenFOAMReader reader;

    public VTKOpenFOAMReader(File baseDir, boolean parallel, ProgressMonitor monitor/*, String meshType*/) {
        File foamFile = new File(baseDir, " ");
        reader = new vtkPOpenFOAMReader();
        if (parallel) {
            reader.SetCaseType(0);
        } else {
            reader.SetCaseType(1);
        }
        reader.SetFileName(foamFile.getAbsolutePath());
        reader.CreateCellToPointOn();

        reader.DisableAllCellArrays();
        reader.DisableAllLagrangianArrays();
        reader.DisableAllPointArrays();
        reader.DisableAllPatchArrays();

        reader.CacheMeshOff();

        reader.DecomposePolyhedraOn(); // se qui si mette OFF viene giu' tutto

        reader.ReleaseDataFlagOn();

        if (monitor != null) {
            VTKProgressMonitorWrapper progressWrapper = new VTKProgressMonitorWrapper("", reader, monitor);
            reader.AddObserver("StartEvent", progressWrapper, "onStart");
            reader.AddObserver("EndEvent", progressWrapper, "onEnd");
            reader.AddObserver("ProgressEvent", progressWrapper, "onProgress");

            monitor.setIndeterminate(false);
            monitor.setTotal(100);
//            monitor.info("-> " + meshType + " Mesh");
        }
    }

    public void ReadInternalMeshOff() {
        reader.SetPatchArrayStatus("internalMesh", 0);
        if (decomposePolyhedra) {
            reader.DecomposePolyhedraOn();
        } else {
            reader.DecomposePolyhedraOff();
        }
    }

    public void ReadInternalMeshOn() {
        reader.SetPatchArrayStatus("internalMesh", 1);
        if (decomposePolyhedra) {
            reader.DecomposePolyhedraOn();
        } else {
            reader.DecomposePolyhedraOff();
        }
    }

    public void ReadPatchesOff() {
//        Patches patches = model.getPatches().patchesToDisplay();
//        for (String patch : patches.toMap().keySet()) {
//            reader.SetPatchArrayStatus(patch, 0);
//        }
        reader.DisableAllPatchArrays();
    }

    public void ReadPatchesOn() {
//        Patches patches = model.getPatches().patchesToDisplay();
//        for (String patch : patches.toMap().keySet()) {
//            reader.SetPatchArrayStatus(patch, 1);
//        }
        reader.EnableAllPatchArrays();
    }

    public void ReadZonesOn() {
        reader.ReadZonesOn();
    }

    public void ReadZonesOff() {
        reader.ReadZonesOff();
    }

    public void setTimeStep(double timeValue) {
        vtkExecutive exe = reader.GetExecutive();
        vtkCompositeDataPipeline pipeline = (vtkCompositeDataPipeline) exe;
        pipeline.SetUpdateTimeStep(0, timeValue);
    }

    public void Delete() {
        reader.Delete();
        reader = null;
    }

    public void Update() {
        reader.Update();
        if (debug) {
            VTKUtil.printDatasetData(reader);
        }
    }

    public void UpdateExtent() {
        reader.UpdateWholeExtent();
    }

    public void UpdateInformation() {
        reader.UpdateInformation();
    }

    public vtkExecutive GetExecutive() {
        return reader.GetExecutive();
    }

    public vtkMultiBlockDataSet GetOutput() {
        return reader.GetOutput();
    }

    public List<String> getCellArrayNames() {
        vtkDataSet dataSet = getAValidDataSet(reader.GetOutput());
        if (dataSet != null) {
            return reorderNames(VTKUtil.getCellFields(dataSet));
        } else if (reader.GetNumberOfCellArrays() > 0) {
            List<String> list = new ArrayList<>();
            int cellArraysNumber = reader.GetNumberOfCellArrays();
            for (int i = 0; i < cellArraysNumber; i++) {
                list.add(reader.GetCellArrayName(i));
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> getPointArrayNames() {
        vtkDataSet dataSet = getAValidDataSet(reader.GetOutput());
        if (dataSet != null) {
            return reorderNames(VTKUtil.getPointFields(dataSet));
        } else if (reader.GetNumberOfPointArrays() > 0) {
            List<String> list = new ArrayList<>();
            int pointArraysNumber = reader.GetNumberOfPointArrays();
            for (int i = 0; i < pointArraysNumber; i++) {
                list.add(reader.GetPointArrayName(i));
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    public static List<String> reorderNames(String[] names) {
        List<String> list = new ArrayList<>();
        for (String string : names) {
            list.add(string);
        }
        List<String> fields = Arrays.asList(Fields.EDITABLE_FIELDS);
        List<String> ordered = new LinkedList<String>();
        for (String field : fields) {
            int index = list.indexOf(field);
            if (index >= 0) {
                ordered.add(list.remove(index));
            }
        }
        ordered.addAll(list);

        return ordered;
    }

    private vtkDataSet getAValidDataSet(vtkMultiBlockDataSet dataSet) {
        if (dataSet != null) {
            if (dataSet.GetNumberOfBlocks() > 0) {
                vtkDataObject block = dataSet.GetBlock(0);
                if (block instanceof vtkMultiBlockDataSet) {
                    return getAValidDataSet((vtkMultiBlockDataSet) block);
                } else if (block instanceof vtkDataSet) {
                    return (vtkDataSet) block;
                }
            }
        }
        return null;
    }

}

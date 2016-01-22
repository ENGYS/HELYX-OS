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

package eu.engys.core.project.geometry.factory;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkCellArray;
import vtk.vtkLine;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.util.VTKSettings;

public class EMESHReader {

    private static final Logger logger = LoggerFactory.getLogger(EMESHReader.class);
    private File file;

    public EMESHReader(File file) {
        this.file = file;
    }
    
    public vtkPolyData run() {
        if (file.exists()) { 
            if (VTKSettings.librariesAreLoaded()) {
                logger.info("Read eMesh " + file.getName() + " [ASCII]");
                Dictionary linesDict = DictionaryUtils.readDictionary2(file);
                List<ListField2> listFields = linesDict.getListFields2();
                vtkPolyData dataSet = new vtkPolyData();
                if (listFields.size() == 2) { 
                    ListField2 pointsList = listFields.get(0);
                    ListField2 linesList = listFields.get(1);
                    
                    List<double[]> pointsArray = pointsList.getElementsAsVectorList();
                    List<double[]> linesArray = linesList.getElementsAsVectorList();
                    
                    vtkPoints points = new vtkPoints();
                    for (double[] point : pointsArray) {
                        points.InsertNextPoint(point);
                    }
                    
                    vtkCellArray lines = new vtkCellArray();
                    for (double[] line : linesArray) {
                        vtkLine cell = new vtkLine();
                        cell.GetPointIds().SetId(0, (int) line[0]);
                        cell.GetPointIds().SetId(1, (int) line[1]);
                        
                        lines.InsertNextCell(cell);
                    }
                    
                    dataSet.SetPoints(points);
                    dataSet.SetLines(lines);
                }
                
                return dataSet;
            } else {
                logger.warn("Read eMesh: no VTK");
                return null;
            }
        } else {
            logger.warn("Read eMesh " + file.getName() + " does not exist");
            return null;
        }
    }
}

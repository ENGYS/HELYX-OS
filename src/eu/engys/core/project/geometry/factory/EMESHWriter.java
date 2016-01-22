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

import vtk.vtkIdList;
import vtk.vtkPolyData;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.core.project.geometry.FeatureLine;

public class EMESHWriter {

    private File file;
    private FeatureLine line;

    public EMESHWriter(File file, FeatureLine line) {
        this.file = file;
        this.line = line;
    }

    public void run() {
        vtkPolyData dataSet = line.getDataSet();
        if (dataSet != null) {

            Dictionary d = new Dictionary("");
            d.setFoamFile(FoamFile.getDictionaryFoamFile("classe", "parent", "name"));

            ListField2 points = new ListField2(String.valueOf(dataSet.GetNumberOfPoints()));
            ListField2 lines = new ListField2(String.valueOf(dataSet.GetNumberOfLines()));

            for (int i = 0; i < dataSet.GetNumberOfPoints(); i++) {
                double[] point = dataSet.GetPoint(i);
                ListField2 pointField = new ListField2("");
                pointField.add(new FieldElement("", String.valueOf(point[0])));
                pointField.add(new FieldElement("", String.valueOf(point[1])));
                pointField.add(new FieldElement("", String.valueOf(point[2])));

                points.add(pointField);
            }

            dataSet.GetLines().InitTraversal();
            for (int i = 0; i < dataSet.GetNumberOfLines(); i++) {
                ListField2 lineField = new ListField2("");
                vtkIdList idList = new vtkIdList();
                dataSet.GetLines().GetNextCell(idList);
                for (int j = 0; j < idList.GetNumberOfIds(); j++) {
                    int id = idList.GetId(j);
                    lineField.add(new FieldElement("", String.valueOf(id)));

                }
                lines.add(lineField);
            }

            d.add(points);
            d.add(lines);

            DictionaryUtils.writeDictionaryFile(file, d);
        }
    }
}

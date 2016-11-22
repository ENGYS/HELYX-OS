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

package eu.engys.core.project.geometry.stl;

public class NastranReader {

	/*
Step 1 Indexing the BDF file.
The first thing I do is read the BDF file once and indexing every BDF
card in the file. The result is a list with entries (line_id, card
label, card format (either comma, small (8 chars) or large (16
chars)). I also return an the file contents as a list of strings so we
don't need the file anymore.

Step 2 Parsing the card information
In this step I start digesting the card data based on the result from
the indexing routine. The first thing I do is start looking for
coordinate system information. I need this in case nodes are defined
in a local coordinate system. I translate all information (including
results like displacement fields) to the global coordinate system;
maybe not the best, but I am not sure how vtk/paraview handles local
coordinate systems).
Next I check if I have a handler for all cells in the BDF (CQUAD4,
CTRIA3 etc.). If not I issue a warning.  Handlers are simple pieces of
Python code that receive a block of BDF text for a single card, digest
the information contained inside and returns an object representing
the card (for example a vtkCell instance).

Next I parse all the grid cards translating to the global coordinate
system if necessary. The VTK classes you need are
- vtkPoints, vtkCellArray, vtkUnstructuredGrid
- vtkQuad, vtkTria etc. Note that you need only one instance of these;
they serve primarily to provide you with information like its element
type id, and the number of nodes for the element. You need this to
fill the cell datastructures later. You can ofcourse hardcode this
information directly, but I don't recommend this
- vtkIntArray, vtkFloatArray etc. for storing results, or any kind of
other information; for example I typically store the original cell and
grid identification numbers, property/material id and the thickness
- vtkXMLUnstructuredGridWriter (if you want to export to vtu)

A typical (simple) code structure would be:

points = vtkPoints()
cells   = vtkCellArray()
grid     = vtkUnstructuredGrid()

Fill the points with points.InsertNextPoint(..) or similar
Fill the cells with cells.InsertNextCell(..) or grid.InsertNextCell(..)

Assign points and possibly cells to the grid:

grid.SetPoints(points)
grid.SetCells(cells)

# Create some data
displacements = vtkFloatArray()
displacements.SetName('displacements)
displacements.SetNumberOfComponents(3)
Fill with displacement information

# Assign the displacement data to the grid
grid.GetPointData().AddArray(displacements)

A final remark. By far the hardest part is to properly parse the BDF
file. Some of the gotchas:
- Properly catching line termination in case of long formatted lines
- Parsing cards with variable length (like MPC cards)
- Parsing the unconventional floating point representation (e.g. 1.0-1
instead of 1.0e-1)
- Handling include statements in different sections of the file
- Detecting card start and end points
etc.

I hope this helps a little, feel free to ask more questions.

Regards,

Marco
	*/
}

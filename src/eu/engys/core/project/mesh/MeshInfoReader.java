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
package eu.engys.core.project.mesh;

import static eu.engys.core.project.mesh.MeshInfo.BOUNDARY_PATCHES;
import static eu.engys.core.project.mesh.MeshInfo.CELLS;
import static eu.engys.core.project.mesh.MeshInfo.CELL_ZONES;
import static eu.engys.core.project.mesh.MeshInfo.FACES;
import static eu.engys.core.project.mesh.MeshInfo.FACE_ZONES;
import static eu.engys.core.project.mesh.MeshInfo.HEXAHEDRA;
import static eu.engys.core.project.mesh.MeshInfo.INTERNAL_FACES;
import static eu.engys.core.project.mesh.MeshInfo.POINTS;
import static eu.engys.core.project.mesh.MeshInfo.POINT_ZONES;
import static eu.engys.core.project.mesh.MeshInfo.POLYHEDRA;
import static eu.engys.core.project.mesh.MeshInfo.PRISMS;
import static eu.engys.core.project.mesh.MeshInfo.PYRAMIDS;
import static eu.engys.core.project.mesh.MeshInfo.TETRAHEDRA;
import static eu.engys.core.project.mesh.MeshInfo.TET_WEDGES;
import static eu.engys.core.project.mesh.MeshInfo.WEDGES;
import static eu.engys.core.project.openFOAMProject.LOG;
import static java.lang.Integer.valueOf;

import java.io.File;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.controller.actions.RunMesh;
import eu.engys.core.dictionary.BeanToDict;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ProjectDict;
import eu.engys.util.Util;

public class MeshInfoReader {
    
    private static final int MAX_LINES = 500;

    private static final Logger logger = LoggerFactory.getLogger(MeshInfoReader.class);

    private static final String HELYXOS_SNAPPY_START_TAG = "Layer mesh : ";
    private static final String HELYX_SNAPPY_START_TAG = "Final mesh : ";
    private static final String HELYX_CHECKMESH_START_TAG = "Mesh stats";

    private Model model;

    public MeshInfoReader(Model model) {
        this.model = model;
    }

    public void read() {
        ProjectDict projectDict = model.getProject().getSystemFolder().getProjectDict();
        
        File snappy = model.getProject().getBaseDir().toPath().resolve(LOG).resolve(RunMesh.LOG_NAME).toFile();
        if (projectDict != null && projectDict.getMeshInfoDict() != null && !projectDict.getMeshInfoDict().isEmpty()) {
            logger.debug("Read mesh statistics from {}", projectDict.getName());
            read(projectDict.getMeshInfoDict());
        } else if (snappy.exists()) {
            logger.debug("Read mesh statistics from {}", snappy);
            read(snappy);
        } else {
            logger.warn("Unable to read mesh statistics. No suitable files");
        }
    }
    
    private void read(Dictionary meshInfoDict) {
        MeshInfo meshInfo = model.getMesh().getMeshInfo();
        BeanToDict.dictToBean(meshInfoDict, meshInfo);        
    }

    public void read(File log) {
        MeshInfo meshInfo = model.getMesh().getMeshInfo();
        if (log.exists()) {
            try (ReversedLinesFileReader reader = new ReversedLinesFileReader(log, 4096, Util.UTF_8)) {
                logger.debug("Read mesh statistics from {}", log);
                Stack<String> stack = new Stack<>();
                while (true) {
                    String line = reader.readLine();
                    stack.push(line);
                    if (stack.peek().startsWith(HELYX_CHECKMESH_START_TAG)) {
                        readMeshStats(meshInfo, stack, HELYX_CHECKMESH_START_TAG);
                        break;
                    } else if (stack.peek().startsWith(HELYX_SNAPPY_START_TAG)) {
                        read(meshInfo, stack, HELYX_SNAPPY_START_TAG);
                        break;
                    } else if (stack.peek().startsWith(HELYXOS_SNAPPY_START_TAG)) {
                        read(meshInfo, stack, HELYXOS_SNAPPY_START_TAG);
                        break;
                    } else if (stack.size() > MAX_LINES) {
                        logger.warn("Unable to load mesh statistics, max buffer length ({} lines) exeeded", MAX_LINES);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.warn("Unable to load mesh statistics, error is: {}", e.getMessage());
            }
        } else {
            logger.warn("Unable to load mesh statistics, file {} does not exist.", log.toString());
        }
    }

    private static final Pattern PATTERN(String label) {
        return Pattern.compile("\\s+" + label + ":\\s+(\\d+)\\s*.*");
    }
    
    private void readMeshStats(MeshInfo meshInfo, Stack<String> stack, String startTag) {
        while (!stack.isEmpty()) {
            String line = stack.pop();
            Matcher matcher = null;
            if ((matcher = PATTERN(POINTS).matcher(line)).matches()) {
                meshInfo.setPoints(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(CELLS).matcher(line)).matches()) {
                meshInfo.setCells(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(FACES).matcher(line)).matches()) {
                meshInfo.setFaces(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(INTERNAL_FACES).matcher(line)).matches()) {
                meshInfo.setInternalFaces(valueOf(matcher.group(1)));
            } 
            
            else if ((matcher = PATTERN(BOUNDARY_PATCHES).matcher(line)).matches()) {
                meshInfo.setBoundaryPatches(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(POINT_ZONES).matcher(line)).matches()) {
                meshInfo.setPointZones(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(FACE_ZONES).matcher(line)).matches()) {
                meshInfo.setFaceZones(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(CELL_ZONES).matcher(line)).matches()) {
                meshInfo.setCellZones(valueOf(matcher.group(1)));
            } 
            
            else if ((matcher = PATTERN(HEXAHEDRA).matcher(line)).matches()) {
                meshInfo.setHexahedra(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(PRISMS).matcher(line)).matches()) {
                meshInfo.setPrisms(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(WEDGES).matcher(line)).matches()) {
                meshInfo.setWedges(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(PYRAMIDS).matcher(line)).matches()) {
                meshInfo.setPyramids(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(TET_WEDGES).matcher(line)).matches()) {
                meshInfo.setTetWedges(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(TETRAHEDRA).matcher(line)).matches()) {
                meshInfo.setTetrahedra(valueOf(matcher.group(1)));
            } else if ((matcher = PATTERN(POLYHEDRA).matcher(line)).matches()) {
                meshInfo.setPolyhedra(valueOf(matcher.group(1)));
            }
            
//            else if (line.startsWith("Finished meshing in")) {
//                System.out.println("MeshInfoReader.readMeshStats() " + line);
//                Pattern pattern3 = Pattern.compile("Finished meshing in =\\s+(" + RegexpUtils.DOUBLE + ")\\s+s.");
//                Matcher matcher3 = pattern3.matcher(line);
//                if (matcher3.matches()) {
//                    meshInfo.setMeshTime(Double.valueOf(matcher3.group(1)));
//                }
//            }
        }
    }
    
    private void read(MeshInfo meshInfo, Stack<String> stack, String startTag) {
        while (!stack.isEmpty()) {
            String line = stack.pop();
            Pattern pattern1 = Pattern.compile(startTag + "cells:(\\d+)\\s+faces:(\\d+)\\s+points:(\\d+)");
            Matcher matcher1 = pattern1.matcher(line);
            if (matcher1.matches()) {
                meshInfo.setCells(valueOf(matcher1.group(1)));
                meshInfo.setFaces(valueOf(matcher1.group(2)));
                meshInfo.setPoints(valueOf(matcher1.group(3)));
//            } else if (line.startsWith("Cells per refinement level:")) {
//                meshInfo.getCellsPerRefinementLevel().clear();
//                String row = stack.pop();
//                while (!row.startsWith("Writing mesh")) {
//                    Pattern pattern2 = Pattern.compile("\\s+(\\d)\\s+(\\d+)");
//                    Matcher matcher2 = pattern2.matcher(row);
//                    if (matcher2.matches()) {
//                        meshInfo.getCellsPerRefinementLevel().add(Integer.valueOf(matcher2.group(2)));
//                    }
//                    row = stack.pop();
//                }
//            } else if (line.startsWith("Finished meshing in")) {
//                Pattern pattern3 = Pattern.compile("Finished meshing in =\\s+(" + RegexpUtils.DOUBLE + ")\\s+s.");
//                Matcher matcher3 = pattern3.matcher(line);
//                if (matcher3.matches()) {
//                    meshInfo.setMeshTime(Double.valueOf(matcher3.group(1)));
//                }
            }
        }
    }

    /* CHECK MESH

Mesh stats
    faces per cell:   4
    boundary patches: 6

Overall number of cells of each type:
    hexahedra:     0
    prisms:        0
    wedges:        0
    pyramids:      0
    tet wedges:    0
    tetrahedra:    608646
    polyhedra:     0

Mesh OK.

End
    */
    
    /* SNAPPYHEXMESH

Final Mesh Summary Report
-------------------------

Mesh stats
----------

    edges:               662929
    layers cells:        66012
    layer coverage:      99.9 %
    boundary patches:    5
    mesh regions:        1
    domain volume:       0.0014602
    domain bounding box: ( -0.20115 -0.010411 0.053001 ) ( 0.091596 0.20405 0.2185 )

Overall number of cells of each type
------------------------------------

    hexahedra:  176434 (80.449 %)
    prisms:     25826 (11.8 %)
    wedges:     4865 (2.22 %)
    pyramids:   43 (0.0196 %)
    tet wedges: 866 (0.395 %)
    tetrahedra: 4555 (2.08 %)
    polyhedra:  6722 (3.07 %)

Cells per refinement level
--------------------------

    0   219311 (spacing: 1.99 mm)

    */

}

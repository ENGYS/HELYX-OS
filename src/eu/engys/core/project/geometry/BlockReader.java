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
package eu.engys.core.project.geometry;

import static eu.engys.core.project.system.BlockMeshDict.BLOCKS_KEY;
import static eu.engys.core.project.system.BlockMeshDict.PATCHES_KEY;
import static eu.engys.core.project.system.BlockMeshDict.VERTICES_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.ADD_LAYERS_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LAYERS_KEY;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.core.project.system.SnappyHexMeshDict;

public class BlockReader {

//    private static final String DEFAULT_MAX_VALUE = "(1 1 1)";
//    private static final String DEFAULT_MIN_VALUE = "(-1 -1 -1)";
//    private static final String DEFAULT_ELEMENTS_VALUE = "(10 10 10)";
    private Geometry geometry;

    public BlockReader(Geometry geometry) {
        this.geometry = geometry;
    }

    /*
     * Here the user has selected a block mesh of type: user defined. I need to load the data from blockMeshDict to visualise the block.
     */
    public MultiPlane loadBlock(BlockMeshDict blockMeshDict, SnappyHexMeshDict snappyHexMeshDict) {
        MultiPlane block = new MultiPlane("BoundingBox");
        loadBlocksFromBlockMeshDict(blockMeshDict, block);
        loadVerticesFromBlockMeshDict(blockMeshDict, block);
        
        if (blockMeshDict.found(PATCHES_KEY)) {
            ListField2 patches = blockMeshDict.getList2(PATCHES_KEY);
            String[] patchesList = extractPatches(patches);
            if (patchesList.length == 6) {
                loadPatches(patchesList, block);
            } else {
                loadDefaultPatches(block);
            }
        } else {
            loadDefaultPatches(block);
        }
        
        block.updatePlanes();
        
        loadLayers(snappyHexMeshDict, block);
        geometry.setBlock(block);
        geometry.setCellSize(block.getDelta());
        return block;
    }

    /*
     * Blocks
     */

    private void loadBlocksFromBlockMeshDict(BlockMeshDict blockMeshDict, MultiPlane block) {
        if (blockMeshDict.found(BLOCKS_KEY)) {
            ListField2 blocks = blockMeshDict.getList2(BLOCKS_KEY);
            block.setElements(extractElements(blocks));
        } else {
            block.setElements(MultiPlane.DEFAULT_ELEMENTS);
        }
    }

    /*
     * Vertices
     */

    private void loadVerticesFromBlockMeshDict(BlockMeshDict blockMeshDict, MultiPlane block) {
        if (blockMeshDict.found(VERTICES_KEY)) {
            ListField2 vertices = blockMeshDict.getList2(VERTICES_KEY);
            block.setMin(extractMin(vertices));
            block.setMax(extractMax(vertices));
        } else {
            block.setMin(MultiPlane.DEFAULT_MIN);
            block.setMax(MultiPlane.DEFAULT_MAX);
        }
    }

    /*
     * Patches
     */

    private void loadPatches(String[] patchesList, MultiPlane block) {
        for (int i = 0; i < patchesList.length; i++) {
            block.addPlane(patchesList[i]);
        }
    }

    private void loadDefaultPatches(MultiPlane block) {
        block.addPlane("ffminx");
        block.addPlane("ffmaxx");
        block.addPlane("ffminy");
        block.addPlane("ffmaxy");
        block.addPlane("ffminz");
        block.addPlane("ffmaxz");
    }

    private void loadLayers(SnappyHexMeshDict snappyHexMeshDict, MultiPlane block) {
        if (snappyHexMeshDict.isDictionary(ADD_LAYERS_CONTROLS_KEY) && snappyHexMeshDict.subDict(ADD_LAYERS_CONTROLS_KEY).isDictionary(LAYERS_KEY)) {
            Dictionary layers = snappyHexMeshDict.subDict(ADD_LAYERS_CONTROLS_KEY).subDict(LAYERS_KEY);
            for (PlaneRegion plane : block.getPlanes()) {
                if (layers.isDictionary(plane.getName())) {
                    plane.getLayerDictionary().merge(layers.subDict(plane.getName()));
                }
            }
        }
    }

    /*
     * Utils
     */

    public static int[] extractElements(ListField2 blocks) {
        if (blocks.isEmpty()) {
            return MultiPlane.DEFAULT_ELEMENTS;
        } else {
            ListField2 element = (ListField2) blocks.getListElements().get(2);
            FieldElement x = ((FieldElement) element.getListElements().get(0));
            FieldElement y = ((FieldElement) element.getListElements().get(1));
            FieldElement z = ((FieldElement) element.getListElements().get(2));
            
            int ix = Integer.parseInt(x.getValue());
            int iy = Integer.parseInt(y.getValue());
            int iz = Integer.parseInt(z.getValue());
            
            return new int[] {ix, iy, iz};
        }
    }

    public static double[] extractMin(ListField2 vertices) {
        if (vertices.isEmpty()) {
            return MultiPlane.DEFAULT_MIN;
        } else {
            ListField2 firstElement = (ListField2) vertices.getListElements().get(0);
            FieldElement x = ((FieldElement) firstElement.getListElements().get(0));
            FieldElement y = ((FieldElement) firstElement.getListElements().get(1));
            FieldElement z = ((FieldElement) firstElement.getListElements().get(2));

            double dx = Double.parseDouble(x.getValue());
            double dy = Double.parseDouble(y.getValue());
            double dz = Double.parseDouble(z.getValue());
            
            return new double[] {dx, dy, dz};
        }
    }

    public static double[] extractMax(ListField2 vertices) {
        if (vertices.isEmpty()) {
            return MultiPlane.DEFAULT_MAX;
        } else {
            ListField2 firstElement = (ListField2) vertices.getListElements().get(vertices.getListElements().size() - 2);
            FieldElement x = ((FieldElement) firstElement.getListElements().get(0));
            FieldElement y = ((FieldElement) firstElement.getListElements().get(1));
            FieldElement z = ((FieldElement) firstElement.getListElements().get(2));

            double dx = Double.parseDouble(x.getValue());
            double dy = Double.parseDouble(y.getValue());
            double dz = Double.parseDouble(z.getValue());
            
            return new double[] {dx, dy, dz};
        }
    }

    private String[] extractPatches(ListField2 patches) {
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < patches.getListElements().size(); i++) {
            if (i % 3 == 1) {
                tokens.add(((FieldElement) patches.getListElements().get(i)).getValue());
            }
        }
        return tokens.toArray(new String[0]);
    }
}

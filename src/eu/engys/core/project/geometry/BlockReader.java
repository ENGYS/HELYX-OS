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

package eu.engys.core.project.geometry;

import static eu.engys.core.project.geometry.Surface.MAX_KEY;
import static eu.engys.core.project.geometry.Surface.MIN_KEY;
import static eu.engys.core.project.system.BlockMeshDict.BLOCKS_KEY;
import static eu.engys.core.project.system.BlockMeshDict.ELEMENTS_KEY;
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

    private static final String DEFAULT_MAX_VALUE = "(1 1 1)";
    private static final String DEFAULT_MIN_VALUE = "(-1 -1 -1)";
    private static final String DEFAULT_ELEMENTS_VALUE = "(10 10 10)";
    private Geometry geometry;

    public BlockReader(Geometry geometry) {
        this.geometry = geometry;
    }

    /*
     * Here the user has selected a block mesh of type: user defined. I need to load the data from blockMeshDict to visualise the block.
     */
    public MultiPlane loadBlock(BlockMeshDict blockMeshDict, SnappyHexMeshDict snappyHexMeshDict) {
        Dictionary blockDict = new Dictionary("block");
        MultiPlane block = null;
        loadBlocksFromBlockMeshDict(blockMeshDict, blockDict);
        loadVerticesFromBlockMeshDict(blockMeshDict, blockDict);
        if (blockMeshDict.found(PATCHES_KEY)) {
            ListField2 patches = blockMeshDict.getList2(PATCHES_KEY);
            String[] patchesList = extractPatches(patches);
            if (patchesList.length == 6) {
                block = loadPatches(patchesList, blockDict);
            } else {
                block = loadDefaultPatches();
            }
        } else {
            block = loadDefaultPatches();
        }
        block.setGeometryDictionary(blockDict);

        loadLayers(snappyHexMeshDict, block);
        geometry.setBlock(block);
        geometry.setCellSize(block.getDelta());
        return block;
    }

    /*
     * Patches
     */

    private MultiPlane loadPatches(String[] patchesList, Dictionary blockDict) {
        MultiPlane block = new MultiPlane("BoundingBox");
        for (int i = 0; i < patchesList.length; i++) {
            blockDict.add("patch" + i, patchesList[i]);
            block.addPlane(patchesList[i]);
        }
        return block;
    }

    private MultiPlane loadDefaultPatches() {
        MultiPlane block = new MultiPlane("BoundingBox");
        block.addPlane("ffminx");
        block.addPlane("ffmaxx");
        block.addPlane("ffminy");
        block.addPlane("ffmaxy");
        block.addPlane("ffminz");
        block.addPlane("ffmaxz");
        return block;
    }

    /*
     * Vertices
     */

    private void loadVerticesFromBlockMeshDict(BlockMeshDict blockMeshDict, Dictionary d) {
        if (blockMeshDict.found(VERTICES_KEY)) {
            ListField2 vertices = blockMeshDict.getList2(VERTICES_KEY);
            d.add(MIN_KEY, extractMin(vertices));
            d.add(MAX_KEY, extractMax(vertices));
        } else {
            loadDefaultVertices(d);
        }
    }

    private void loadDefaultVertices(Dictionary d) {
        d.add(MIN_KEY, DEFAULT_MIN_VALUE);
        d.add(MAX_KEY, DEFAULT_MAX_VALUE);
    }

    /*
     * Blocks
     */

    private void loadBlocksFromBlockMeshDict(BlockMeshDict blockMeshDict, Dictionary d) {
        if (blockMeshDict.found(BLOCKS_KEY)) {
            ListField2 blocks = blockMeshDict.getList2(BLOCKS_KEY);
            d.add(ELEMENTS_KEY, extractElements(blocks));
        } else {
            loadDefaultBlocks(d);
        }
    }

    private void loadDefaultBlocks(Dictionary d) {
        d.add(ELEMENTS_KEY, DEFAULT_ELEMENTS_VALUE);
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

    private String extractElements(ListField2 blocks) {
        if (blocks.isEmpty()) {
            return DEFAULT_ELEMENTS_VALUE;
        } else {
            StringBuffer sb = new StringBuffer("(");
            ListField2 element = (ListField2) blocks.getListElements().get(2);
            FieldElement x = ((FieldElement) element.getListElements().get(0));
            FieldElement y = ((FieldElement) element.getListElements().get(1));
            FieldElement z = ((FieldElement) element.getListElements().get(2));
            sb.append(x.getValue() + " ");
            sb.append(y.getValue() + " ");
            sb.append(z.getValue() + " ");
            sb.append(")");
            return sb.toString();
        }
    }

    private String extractMin(ListField2 vertices) {
        if (vertices.isEmpty()) {
            return DEFAULT_MIN_VALUE;
        } else {
            StringBuffer sb = new StringBuffer("(");
            ListField2 firstElement = (ListField2) vertices.getListElements().get(0);
            FieldElement x = ((FieldElement) firstElement.getListElements().get(0));
            FieldElement y = ((FieldElement) firstElement.getListElements().get(1));
            FieldElement z = ((FieldElement) firstElement.getListElements().get(2));
            sb.append(x.getValue() + " ");
            sb.append(y.getValue() + " ");
            sb.append(z.getValue() + " ");
            sb.append(")");
            return sb.toString();
        }
    }

    private String extractMax(ListField2 vertices) {
        if (vertices.isEmpty()) {
            return DEFAULT_MAX_VALUE;
        } else {
            StringBuffer sb = new StringBuffer("(");
            ListField2 firstElement = (ListField2) vertices.getListElements().get(vertices.getListElements().size() - 2);
            FieldElement x = ((FieldElement) firstElement.getListElements().get(0));
            FieldElement y = ((FieldElement) firstElement.getListElements().get(1));
            FieldElement z = ((FieldElement) firstElement.getListElements().get(2));
            sb.append(x.getValue() + " ");
            sb.append(y.getValue() + " ");
            sb.append(z.getValue() + " ");
            sb.append(")");
            return sb.toString();
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

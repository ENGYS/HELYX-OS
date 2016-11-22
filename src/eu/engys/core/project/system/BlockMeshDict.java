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
package eu.engys.core.project.system;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import eu.engys.core.dictionary.BlockMeshWriter;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.dictionary.parser.DictionaryReader2;
import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.core.project.geometry.BoundingBox;

public class BlockMeshDict extends Dictionary {

    public static final String BLOCK_DICT = "blockMeshDict";

    public static final String BLOCKS_KEY = "blocks";
    public static final String VERTICES_KEY = "vertices";
    public static final String PATCHES_KEY = "patches";
    public static final String SPACING_KEY = "spacing";
    // public static final String FROM_FILE_KEY = "fromFile";
    public static final String BOUNDARY_KEY = "boundary";
    public static final String ELEMENTS_KEY = "elements";

    public static final String HEX_KEY = "hex";
    public static final String SIMPLE_GRADING_KEY = "simpleGrading";
    public static final String WALL_KEY = "wall";
    public static final String FROM_FILE_LINE = "fromFile true;";

    private boolean fromFile;

    public BlockMeshDict() {
        super(BLOCK_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, BLOCK_DICT));
    }

    public BlockMeshDict(File blockMeshFile) {
        super(BLOCK_DICT, blockMeshFile);
    }

    public void check() throws DictionaryException {
    }

    @Override
    protected void readDictionaryFromString(String text) {
        new DictionaryReader2(this).read(text);
    }

    @Override
    public void readDictionary(File file) {
        new DictionaryReader2(this).read(file);
    }

    @Override
    protected String write() {
        return new BlockMeshWriter(this).write();
    }

    public void setBoundingBox(BoundingBox boundingBox, double spacing, boolean shouldConsiderSpacing) {
        double xmin = boundingBox.getXmin();
        double xmax = boundingBox.getXmax();
        double ymin = boundingBox.getYmin();
        double ymax = boundingBox.getYmax();
        double zmin = boundingBox.getZmin();
        double zmax = boundingBox.getZmax();

        if (shouldConsiderSpacing) {// only HELYX-OS
            boolean emptyBoundingBox = (xmin == 0) && (xmax == 0) && (ymin == 0) && (ymax == 0) && (zmin == 0) && (zmax == 0);

            /* aggiungo un po'di spacing */
            xmin -= spacing;
            xmax += spacing;
            ymin -= spacing;
            ymax += spacing;
            zmin -= spacing;
            zmax += spacing;

            double xDelta = Math.abs(xmax - xmin);
            double yDelta = Math.abs(ymax - ymin);
            double zDelta = Math.abs(zmax - zmin);

            int xElements = emptyBoundingBox ? 10 : (int) Math.ceil(xDelta / spacing);
            int yElements = emptyBoundingBox ? 10 : (int) Math.ceil(yDelta / spacing);
            int zElements = emptyBoundingBox ? 10 : (int) Math.ceil(zDelta / spacing);
            
            // BLOCKS

            ListField2 blocksList = new ListField2(BLOCKS_KEY);
            blocksList.add(new FieldElement("", HEX_KEY));

            ListField2 hexList = new ListField2("");
            hexList.add(new FieldElement("", "0"));
            hexList.add(new FieldElement("", "1"));
            hexList.add(new FieldElement("", "2"));
            hexList.add(new FieldElement("", "3"));
            hexList.add(new FieldElement("", "4"));
            hexList.add(new FieldElement("", "5"));
            hexList.add(new FieldElement("", "6"));
            hexList.add(new FieldElement("", "7"));
            blocksList.add(hexList);

            ListField2 elementsList = new ListField2("");
            elementsList.add(new FieldElement("", String.valueOf(xElements)));
            elementsList.add(new FieldElement("", String.valueOf(yElements)));
            elementsList.add(new FieldElement("", String.valueOf(zElements)));
            blocksList.add(elementsList);

            blocksList.add(new FieldElement("", SIMPLE_GRADING_KEY));

            ListField2 lastList = new ListField2("");
            lastList.add(new FieldElement("", "1"));
            lastList.add(new FieldElement("", "1"));
            lastList.add(new FieldElement("", "1"));
            blocksList.add(lastList);

            add(blocksList);
            
            // VERTICES

            double[] min = new double[] { xmin, ymin, zmin };
            double[] max = emptyBoundingBox ? new double[] { xmax, ymax, zmax } : new double[] { xmin + xElements * spacing, ymin + yElements * spacing, zmin + zElements * spacing };

            ListField2 verticesList = new ListField2(VERTICES_KEY);
            verticesList.add(getPointList(min[0], min[1], min[2]));
            verticesList.add(getPointList(max[0], min[1], min[2]));
            verticesList.add(getPointList(max[0], max[1], min[2]));
            verticesList.add(getPointList(min[0], max[1], min[2]));
            verticesList.add(getPointList(min[0], min[1], max[2]));
            verticesList.add(getPointList(max[0], min[1], max[2]));
            verticesList.add(getPointList(max[0], max[1], max[2]));
            verticesList.add(getPointList(min[0], max[1], max[2]));

            add(verticesList);
        }
    }

    private ListField2 getPointList(double x, double y, double z) {
        ListField2 list = new ListField2("");
        list.add(new FieldElement("", String.valueOf(x)));
        list.add(new FieldElement("", String.valueOf(y)));
        list.add(new FieldElement("", String.valueOf(z)));
        return list;
    }

    public boolean isFromFile() {
        return fromFile;
    }

    public void setFromFile(boolean fromFile) {
        this.fromFile = fromFile;
    }

    public static boolean containsFromFileLine(File file) {
        try {
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (line.trim().equals(BlockMeshDict.FROM_FILE_LINE)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}

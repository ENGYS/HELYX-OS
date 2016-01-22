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


package eu.engys.core.project.mesh;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.ReversedLinesFileReader;

import eu.engys.core.controller.actions.RunMesh;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.util.RegexpUtils;

public class Mesh {

    private static final String HELYXOS_MESH_START_TAG = "Layer mesh : ";
    private static final String HELYX_MESH_START_TAG = "Final mesh : ";
    private long numberOfPoints = 0;
    private long numberOfCells = 0;
    private long numberOfFaces = 0;
    private double meshTime = 0;

    private List<Integer> cellsPerRefinementLevel = new ArrayList<>();

    private int memorySize = 0;
    private double[] bounds = new double[6];

    private List<Double> timeSteps = new LinkedList<>();

    private Map<String, FieldItem> cellFieldMap = new LinkedHashMap<>();
    private Map<String, FieldItem> pointFieldMap = new LinkedHashMap<>();
    
    private Map<Double, List<String>> timeStepCellFieldsMap = new HashMap<Double, List<String>>();
    private Map<Double, List<String>> timeStepPointFieldsMap = new HashMap<Double, List<String>>();
    private List<String> regions;

    public long getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public long getNumberOfCells() {
        return numberOfCells;
    }

    public void setNumberOfCells(int numberOfCells) {
        this.numberOfCells = numberOfCells;
    }

    public long getNumberOfFaces() {
        return this.numberOfFaces;
    }

    public void setNumberOfFaces(int numberOfFaces) {
        this.numberOfFaces = numberOfFaces;
    }

    public double getMeshTime() {
        return meshTime;
    }

    public List<Integer> getCellsPerRefinementLevel() {
        return cellsPerRefinementLevel;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public double[] getBounds() {
        return bounds;
    }

    public void setBounds(double[] bounds) {
        this.bounds = bounds;
    }

    public List<Double> getTimeSteps() {
        return timeSteps;
    }

    public void setTimeSteps(List<Double> timeSteps) {
        this.timeSteps = timeSteps;
    }

    public List<String> getRegions() {
        return regions;
    }
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    public Map<String, FieldItem> getCellFieldMap() {
        return cellFieldMap;
    }

    public Map<String, FieldItem> getPointFieldMap() {
        return pointFieldMap;
    }
    
    public Map<Double, List<String>> getTimeStepCellFieldsMap() {
        return timeStepCellFieldsMap;
    }
    
    public Map<Double, List<String>> getTimeStepPointFieldsMap() {
        return timeStepPointFieldsMap;
    }

    public void readStatistics(Model model) {
        Path log = model.getProject().getBaseDir().toPath().resolve(openFOAMProject.LOG).resolve(RunMesh.LOG_NAME);
        if (Files.exists(log)) {
            try (ReversedLinesFileReader reader = new ReversedLinesFileReader(log.toFile())) {
                Stack<String> stack = new Stack<>();
                while (true) {
                    String line = reader.readLine();
                    stack.push(line);
                    if (stack.peek().startsWith(HELYX_MESH_START_TAG)) {
                        read(stack, HELYX_MESH_START_TAG);
                        break;
                    } else if (stack.peek().startsWith(HELYXOS_MESH_START_TAG)) {
                        read(stack, HELYXOS_MESH_START_TAG);
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private void read(Stack<String> stack, String startTag) {
        while (!stack.isEmpty()) {
            String line = stack.pop();
            Pattern pattern1 = Pattern.compile(startTag + "cells:(\\d+)\\s+faces:(\\d+)\\s+points:(\\d+)");
            Matcher matcher1 = pattern1.matcher(line);
            if (matcher1.matches()) {
                this.numberOfCells = Integer.valueOf(matcher1.group(1));
                this.numberOfFaces = Integer.valueOf(matcher1.group(2));
                this.numberOfPoints = Integer.valueOf(matcher1.group(3));
            } else if (line.startsWith("Cells per refinement level:")) {
                this.cellsPerRefinementLevel.clear();
                String row = stack.pop();
                while (!row.startsWith("Writing mesh")) {
                    Pattern pattern2 = Pattern.compile("\\s+(\\d)\\s+(\\d+)");
                    Matcher matcher2 = pattern2.matcher(row);
                    if (matcher2.matches()) {
                        this.cellsPerRefinementLevel.add(Integer.valueOf(matcher2.group(2)));
                    }
                    row = stack.pop();
                }
            } else if (line.startsWith("Finished meshing in")) {
                Pattern pattern3 = Pattern.compile("Finished meshing in =\\s+(" + RegexpUtils.DOUBLE + ")\\s+s.");
                Matcher matcher3 = pattern3.matcher(line);
                if (matcher3.matches()) {
                    this.meshTime = Double.valueOf(matcher3.group(1));
                }
            }
        }
    }

}

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
package eu.engys.core.project.zero.patches;

import static eu.engys.core.project.zero.patches.BoundaryType.OPENING;
import static eu.engys.core.project.zero.patches.BoundaryType.OPENING_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.PATCH;
import static eu.engys.core.project.zero.patches.BoundaryType.PROCESSOR;
import static eu.engys.core.project.zero.patches.BoundaryType.PROCESSOR_CYCLIC;
import static eu.engys.core.project.zero.patches.BoundaryType.WALL;
import static eu.engys.core.project.zero.patches.Patch.N_FACES_KEY;
import static eu.engys.core.project.zero.patches.Patch.PHYSICAL_TYPE_KEY;
import static eu.engys.core.project.zero.patches.Patch.TYPE_KEY;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class PatchesReader {

    private static final Logger logger = LoggerFactory.getLogger(Patches.class);
    private ProgressMonitor monitor;
    private Model model;

    public PatchesReader(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public Patches read(File... boundaryFiles) {
        Patches patches = new Patches();

        final Patches[] parallelPatches = new Patches[boundaryFiles.length];

        Runnable[] runnables = new Runnable[boundaryFiles.length];
        for (int i = 0; i < boundaryFiles.length; i++) {
            final File boundary = boundaryFiles[i];
            final int index = i;
            runnables[i] = new Runnable() {
                @Override
                public void run() {
                    Patches readPatches = readBoundary(boundary);
                    parallelPatches[index] = readPatches;
                }
            };
        }
        ExecUtil.execParallelAndWait(runnables);

        for (Patches readPatches : parallelPatches) {
            merge(patches, readPatches);
        }
        patches.setParallelPatches(parallelPatches);

        return patches;
    }

    private void merge(Patches patches, Patches readPatches) {
        for (Patch patch : readPatches) {
            if (!patches.contains(patch)) {
                patches.add(new Patch(patch));
            }
        }
    }

    Patches readBoundary(File boundary) {
        logger.info("READ: Patches {}", boundary.getAbsolutePath());
        Patches patches = new Patches();
        if (boundary.exists()) {
            try {
                String boundaryString = IOUtils.readStringFromFile(boundary);
                boundaryString = boundaryString.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");

                Pattern pattern = Pattern.compile("(\\d*)\\s*\\(([^#]*)\\)");
                Matcher matcher = pattern.matcher(boundaryString);
                if (matcher.find()) {
                    if (matcher.groupCount() == 2) {
                        String nPatch = matcher.group(1);
                        String patchesString = matcher.group(2);
                        Dictionary dict = DictionaryUtils.readDictionary(patchesString);
                        List<String> notKnownPatches = new ArrayList<String>();
                        for (Dictionary patchDict : dict.getDictionaries()) {
                            Patch bm = dictToPatch(model, notKnownPatches, patchDict);
                            patches.add(bm);
                        }
                        monitor.setCurrent(null, monitor.getCurrent() + 1, 2);

                        if (!nPatch.isEmpty() && Integer.parseInt(nPatch) != patches.size()) {
                            monitor.error("Number of read patches (" + patches.size() + ") is invalid (expected " + nPatch + ").", 2);
                            logger.error("Number of read patches (" + patches.size() + ") is invalid (expected " + nPatch + ").");
                        } else if (!notKnownPatches.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Unknown patches type: \n");
                            for (String string : notKnownPatches) {
                                sb.append("\t " + string + "\n");
                            }
                            monitor.warning(sb.toString(), 2);
                            logger.warn(sb.toString());
                        }
                    }
                }
            } catch (Exception e) {
                monitor.warning("Cannot read the file: " + e.getMessage(), 2);
                logger.warn("Cannot read the file", e);
            }
        } else {
            monitor.warning("Boundary file does not exist", 2);
            logger.warn("Boundary file does not exist");
        }
        return patches;
    }

    public static Patch dictToPatch(Model model, List<String> notKnownPatches, Dictionary d) {
        String patchName = d.getName();
        String patchType = d.lookup(TYPE_KEY);
        String physicalType = d.lookup(PHYSICAL_TYPE_KEY);
        String nFaces = d.lookup(N_FACES_KEY);

        Patch patch = new Patch(patchName);
        patch.setDictionary(d);
        patch.setName(patchName);
        patch.setVisible(true);
        patch.setEmpty(nFaces != null && Integer.valueOf(nFaces) == 0);
        patch.setType(patchType);

        if (BoundaryType.isPatch(patchType)) {
            boolean isCoupled = model.getState() != null && model.getState().isCoupled();
            if (isCoupled && BoundaryType.isSymmetryPhysicalType(physicalType)) {
                patch.setPhysicalType(BoundaryType.SYMMETRY_PLANE);
            } else if (BoundaryType.isKnown(physicalType)) {
                patch.setPhysicalType(BoundaryType.getType(physicalType));
            } else {
                if (BoundaryType.isKnown(OPENING_KEY)) {
                    patch.setPhysicalType(OPENING);
                } else {
                    patch.setPhysicalType(PATCH);
                }
            }
        } else if (BoundaryType.isWall(patchType)) {
            if (BoundaryType.isKnown(physicalType)) {
                patch.setPhysicalType(BoundaryType.getType(physicalType));
            } else {
                patch.setPhysicalType(WALL);
            }
        } else if (BoundaryType.isMappedPatch(patchType)) {
            if (BoundaryType.isKnown(physicalType)) {
                patch.setPhysicalType(BoundaryType.getType(physicalType));
            } else {
                patch.setPhysicalType(WALL);
            }
        } else if (BoundaryType.isMappedWall(patchType)) {
            if (BoundaryType.isKnown(physicalType)) {
                patch.setPhysicalType(BoundaryType.getType(physicalType));
            } else {
                patch.setPhysicalType(WALL);
            }
        } else if (BoundaryType.isKnown(patchType)) {
            patch.setPhysicalType(BoundaryType.getType(patchType));
        } else if (BoundaryType.isProcessor(patchType)) {
            patch.setPhysicalType(PROCESSOR);
        } else if (BoundaryType.isProcessorCyclic(patchType)) {
            patch.setPhysicalType(PROCESSOR_CYCLIC);
        } else {
            notKnownPatches.add(patchName + ": " + patchType);
            patch.setPhysicalType(BoundaryType.getDefaultType());
        }

        logger.debug("[PATCH] name: {}, type: {}, physicaltype: {}", patch.getName(), patch.getType(), patch.getPhysicalType());

        return patch;
    }
}

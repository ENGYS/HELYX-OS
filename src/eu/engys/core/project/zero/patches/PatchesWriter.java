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

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.zero.patches.BoundaryType.CYCLIC_AMI_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.CYCLIC_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.MAPPED_PATCH_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.MAPPED_WALL_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.PATCH_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.PROCESSOR_CYCLIC_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.PROCESSOR_KEY;
import static eu.engys.core.project.zero.patches.BoundaryType.SYMMETRY_KEY;
import static eu.engys.core.project.zero.patches.Patch.N_FACES_KEY;
import static eu.engys.core.project.zero.patches.Patch.OFFSETS_KEY;
import static eu.engys.core.project.zero.patches.Patch.PHYSICAL_TYPE_KEY;
import static eu.engys.core.project.zero.patches.Patch.START_FACE_KEY;
import static eu.engys.core.project.zero.patches.Patch.TYPE_KEY;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.dictionary.DictionaryWriter;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.project.Model;
import eu.engys.util.IOUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class PatchesWriter {

    private static final Logger logger = LoggerFactory.getLogger(Patches.class);

    private ProgressMonitor monitor;

    private Model model;

    public PatchesWriter(Model model, ProgressMonitor monitor) {
        this.model = model;
        this.monitor = monitor;
    }

    public void write(final Patches patches, File... boundaryFiles) {
        Runnable[] runnables = new Runnable[boundaryFiles.length];
        for (int i = 0; i < boundaryFiles.length; i++) {
            final File boundary = boundaryFiles[i];
            runnables[i] = new Runnable() {
                @Override
                public void run() {
                    writeBoundary(patches, boundary);
                }
            };
        }
        // ExecUtil.execInParallelAndWait(runnables);
        ExecUtil.execSerial(runnables);
    }

    private void writeBoundary(Patches patches, File boundaryFile) {
        monitor.setCurrent(null, monitor.getCurrent() + 1, 2);
        logger.info("WRITE: Boundary {}", boundaryFile.getAbsolutePath());

        Map<String, String> originalNames = new HashMap<>();
        Map<String, String> phisicalTypes = new HashMap<>();
        Map<String, String> types = new HashMap<>();
        Map<String, Dictionary> dictionaries = new HashMap<>();

        for (Patch patch : patches) {
            phisicalTypes.put(patch.getName(), patch.getPhysicalType().getKey());
            types.put(patch.getName(), patch.getType());
            originalNames.put(patch.getOriginalName(), patch.getName());
            dictionaries.put(patch.getName(), new Dictionary(patch.getDictionary()));
        }

        try {
            String boundaryString = IOUtils.readStringFromFile(boundaryFile);

            StringBuffer sb = new StringBuffer(boundaryString.length());

            boundaryString = boundaryString.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");

            Pattern pattern = Pattern.compile("(\\d+)\\s*\\(([^#]*)\\)");
            Matcher matcher = pattern.matcher(boundaryString);

            if (matcher.find()) {
                if (matcher.groupCount() == 2) {
                    String nPatch = matcher.group(1);

                    FoamFile foamFile = FoamFile.getDictionaryFoamFile("polyBoundaryMesh", "\"0/polyMesh\"", "boundary");
                    new DictionaryWriter(foamFile).writeDictionary(sb, "");

                    sb.append(nPatch + "(\n");
                    String patchesString = matcher.group(2);

                    Dictionary patchesStringAsDictionary = DictionaryUtils.readDictionary(patchesString);
                    for (Dictionary originalPatchDict : patchesStringAsDictionary.getDictionaries()) {
                        String originalName = originalPatchDict.getName();
                        String newName = originalNames.get(originalName);

                        Dictionary patchDict = new Dictionary(newName);
                        patchDict.add(N_FACES_KEY, originalPatchDict.lookup(N_FACES_KEY));
                        patchDict.add(START_FACE_KEY, originalPatchDict.lookup(START_FACE_KEY));

                        if (phisicalTypes.containsKey(newName)) {
                            String type = types.get(newName);
                            String physicalType = phisicalTypes.get(newName);
                            
                            boolean isCoupled = model.getState() != null && model.getState().isCoupled();

                            if (BoundaryType.isPatchPhysicalType(physicalType)) {
                                patchDict.add(TYPE_KEY, PATCH_KEY);
                                patchDict.add(PHYSICAL_TYPE_KEY, physicalType);
                            } else if (isCoupled && BoundaryType.isSymmetryPlanePhysicalType(physicalType)) {
                                patchDict.add(TYPE_KEY, PATCH_KEY);
                                patchDict.add(PHYSICAL_TYPE_KEY, SYMMETRY_KEY);
                            } else if (BoundaryType.isFreeSurfacePhysicalType(physicalType)) {
                                patchDict.add(TYPE_KEY, type);
                                patchDict.add(PHYSICAL_TYPE_KEY, physicalType);

                                if (type.equals(MAPPED_PATCH_KEY) || type.equals(MAPPED_WALL_KEY)) {
                                    patchDict.add(OFFSETS_KEY, originalPatchDict.lookup(OFFSETS_KEY));
                                    Dictionary dictionary = dictionaries.get(newName);
                                    if (dictionary.found(N_FACES_KEY))
                                        dictionary.remove(N_FACES_KEY);
                                    if (dictionary.found(START_FACE_KEY))
                                        dictionary.remove(START_FACE_KEY);
                                    if (dictionary.found(OFFSETS_KEY))
                                        dictionary.remove(OFFSETS_KEY);

                                    patchDict.merge(dictionary);
                                }
                            } else {
                                patchDict.add(TYPE, physicalType);
                            }

                            if (physicalType.equals(CYCLIC_KEY) || physicalType.equals(CYCLIC_AMI_KEY) || physicalType.equals(PROCESSOR_KEY) || physicalType.equals(PROCESSOR_CYCLIC_KEY)) {
                                Dictionary dictionary = dictionaries.get(newName);
                                if (dictionary.found(N_FACES_KEY))
                                    dictionary.remove(N_FACES_KEY);
                                if (dictionary.found(START_FACE_KEY))
                                    dictionary.remove(START_FACE_KEY);

                                patchDict.merge(dictionary);
                            }
                        } else {
                            patchDict.add(TYPE_KEY, originalPatchDict.lookup(TYPE_KEY));
                        }

                        new DictionaryWriter(patchDict).writeDictionary(sb, "    ");
                    }

                    sb.append(")");
                }
            }

            FileWriter outStream = new FileWriter(boundaryFile);
            outStream.write(sb.toString());
            outStream.close();

        } catch (IOException e) {
            monitor.warning("Cannot read or write boundary file");
            logger.warn("Cannot read or write boundary file", e);
        }

    }
}

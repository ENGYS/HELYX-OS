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

import static eu.engys.core.dictionary.DictionaryUtils.copyIfFound;
import static eu.engys.core.project.system.SnappyHexMeshDict.ADD_LAYERS_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.AUTO_BLOCK_MESH_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.BLOCK_DATA_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CASTELLATED_MESH_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CELL_ZONE_INSIDE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CELL_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_TYPE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FEATURES_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.GEOMETRY_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LAYERS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LEVEL_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_CELLS_ACROSS_GAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.PROXIMITY_INCREMENT_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINEMENTS_REGIONS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINEMENTS_SURFACES_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.factory.DefaultGeometryFactory;
import eu.engys.core.project.geometry.surface.Region;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.system.BlockMeshDict;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.util.progress.ProgressMonitor;

public class GeometryReader {

    private static final Logger logger = LoggerFactory.getLogger(GeometryReader.class);

    private Geometry geometry;

    private SnappyHexMeshDict snappyHexMeshDict;
    private Dictionary castellatedDict;
    private Dictionary refinementSurfaces;
    private Dictionary refinementRegions;
    private Dictionary geometryDict;
    private Dictionary layers;

    public GeometryReader(Geometry geometry) {
        this.geometry = geometry;
    }

    public void loadGeometry(Model model, ProgressMonitor monitor) {
        snappyHexMeshDict = model.getProject().getSystemFolder().getSnappyHexMeshDict();

        if (hasAValidStructure(snappyHexMeshDict)) {
            initDictionaries();
            loadFeatureLines(model, monitor);
            loadSurfaces(model, monitor);
        }

        if (snappyHexMeshDict == null) {
            return;
        }

        if (!snappyHexMeshDict.found(AUTO_BLOCK_MESH_KEY) || !snappyHexMeshDict.lookup(AUTO_BLOCK_MESH_KEY).equals("true")) {
            BlockMeshDict blockMeshDict = model.getProject().getSystemFolder().getBlockMeshDict();
            if (blockMeshDict != null) {
                geometry.setAutoBoundingBox(false);
                if (!blockMeshDict.isFromFile()) {
                    new BlockReader(geometry).loadBlock(blockMeshDict, snappyHexMeshDict);
                }
            } else {
                logger.warn("No patches found in blockMeshDict");
            }
        } else {
            geometry.setAutoBoundingBox(true);
            if (snappyHexMeshDict.found(BLOCK_DATA_KEY)) {
                double[] blockData = snappyHexMeshDict.lookupDoubleArray(BLOCK_DATA_KEY);
                geometry.setCellSize(new double[] { blockData[0], blockData[0], blockData[0] });
            }
        }
        model.geometryChanged();
    }

    private boolean hasAValidStructure(SnappyHexMeshDict snappyHexMeshDict) {
        if (snappyHexMeshDict != null && snappyHexMeshDict.found(GEOMETRY_KEY) && snappyHexMeshDict.found(CASTELLATED_MESH_CONTROLS_KEY)) {
            Dictionary castellatedDict = snappyHexMeshDict.subDict(CASTELLATED_MESH_CONTROLS_KEY);
            if (castellatedDict.found(REFINEMENTS_SURFACES_KEY)) {
                if (castellatedDict.found(REFINEMENTS_REGIONS_KEY)) {
                    if (snappyHexMeshDict.found(ADD_LAYERS_CONTROLS_KEY) && snappyHexMeshDict.subDict(ADD_LAYERS_CONTROLS_KEY).found(LAYERS_KEY)) {
                        return true;
                    } else {
                        logger.error("SnappyHexMeshDict bad structure: addLayersControls is missing.");
                    }
                } else {
                    logger.error("SnappyHexMeshDict bad structure: refinementRegions is missing.");
                }
            } else {
                logger.error("SnappyHexMeshDict bad structure: refinementSurfaces is missing.");
            }
        } else {
            logger.error("SnappyHexMeshDict missing or with bad structure");
        }
        return false;
    }

    private void initDictionaries() {
        geometryDict = snappyHexMeshDict.subDict(GEOMETRY_KEY);
        castellatedDict = snappyHexMeshDict.subDict(CASTELLATED_MESH_CONTROLS_KEY);
        refinementSurfaces = castellatedDict.subDict(REFINEMENTS_SURFACES_KEY);
        refinementRegions = castellatedDict.subDict(REFINEMENTS_REGIONS_KEY);
        layers = snappyHexMeshDict.subDict(ADD_LAYERS_CONTROLS_KEY).subDict(LAYERS_KEY);
    }

    private void loadFeatureLines(final Model model, final ProgressMonitor monitor) {
        if (castellatedDict.found(FEATURES_KEY) && castellatedDict.isList(FEATURES_KEY)) {
//            Dictionary lines = new Dictionary("");
//            lines.add(new ListField(castellatedDict.getList(FEATURES_KEY)));
            ListField list = castellatedDict.getList(FEATURES_KEY);
            for (DefaultElement el : list.getListElements()) {
                if (el instanceof Dictionary) {
                    Dictionary dict = (Dictionary) el;
                    final FeatureLine line = (FeatureLine) geometry.getFactory().loadSurface(dict, model, monitor);
                    line.fromDictionary(dict);

                    logger.info("LINE: " + line.getName());
                    geometry.addLine( line);
                }
                
            }
        }
    }

    private void loadSurfaces(final Model model, final ProgressMonitor monitor) {
        DefaultGeometryFactory.clearSTLCache();
        List<Dictionary> dictionaries = geometryDict.getDictionaries();

        monitor.setTotal(dictionaries.size());
        monitor.info("STLS:", 1);
        
        final List<Surface> surfaces = Collections.synchronizedList(new ArrayList<Surface>());
        for (int i = 0; i < dictionaries.size(); i++) {
            final Dictionary dict = dictionaries.get(i);
            surfaces.add(geometry.getFactory().loadSurface(dict, model, monitor));
        }

        String[] surfaceNames = new String[surfaces.size()];
        for (int i = 0; i < surfaces.size(); i++) {
            Surface surface = surfaces.get(i);
            if (surface != null) {
                surfaceNames[i] = surface.getName();
                loadSurface(surface);
            } else {
                logger.warn("A surface is null");
            }
        }
        monitor.info("Surfaces:" + Arrays.toString(surfaceNames), 1);
    }

    private void loadSurface(Surface surface) {
        geometry.addSurface(surface);
        readRefinementSurfaces(surface);
        readRefinementRegions(surface);
        readLayers(surface);
    }

    void readRefinementSurfaces(Surface surface) {
        String name = surface.getName();
        if (refinementSurfaces.found(name)) {
            logger.info("SURFACE: " + name);
            Dictionary surfaceDict = refinementSurfaces.subDict(name);
            if (isAFaceZone(surfaceDict)) {
                readSurfaceAsACellZone(surface, surfaceDict);
            } else {
                readSurface(surface, surfaceDict);
            }
        }
    }

    private void readSurface(Surface surface, Dictionary surfaceDict) {
        surface.setSurfaceDictionary(surfaceDict);
        if (surfaceDict.found("regions") && surface.getType().isStl()) {
            Dictionary regionsDict = surfaceDict.subDict("regions");
            Stl stl = (Stl) surface;
            for (Region region : stl.getRegions()) {
                if (regionsDict.found(region.getName())) {
                    region.setSurfaceDictionary(regionsDict.subDict(region.getName()));
                }
            }
        }
    }

    private void readSurfaceAsACellZone(Surface surface, Dictionary surfaceDict) {
        
        surface.setZoneDictionary(surfaceDict);
        if (isACellZone(surfaceDict)) {
            surfaceDict.add("isCellZone", "true");
        } else {
            surfaceDict.add("isCellZone", "false");
        }
        if (!surfaceDict.found(FACE_TYPE_KEY)) {
            surfaceDict.add(FACE_TYPE_KEY, "internal");
        }
        surfaceDict.add(CELL_ZONE_INSIDE_KEY, "inside");

        Dictionary sd = surface.getSurfaceDictionary();
        copyIfFound(sd, surfaceDict, LEVEL_KEY);
        copyIfFound(sd, surfaceDict, PROXIMITY_INCREMENT_KEY);
        copyIfFound(sd, surfaceDict, MAX_CELLS_ACROSS_GAP_KEY);
    }

    private boolean isACellZone(Dictionary surfaceDict) {
        return surfaceDict.found(CELL_ZONE_KEY);
    }

    private boolean isAFaceZone(Dictionary surfaceDict) {
        return surfaceDict.found(FACE_ZONE_KEY);
    }

    void readRefinementRegions(Surface surface) {
        String name = surface.getName();
        if (refinementRegions.found(name)) {
            logger.info("VOLUME: " + name);
            Dictionary volumeDict = refinementRegions.subDict(name);

            String mode = volumeDict.lookup("mode");
            if ("inside".equals(mode) || "outside".equals(mode) || "distance".equals(mode)) {
                surface.setVolumeDictionary(volumeDict);
            } else {
                logger.error("Volume dictionary does not contain a valid ('inside', 'outside' or 'distance') mode");
            }
        } else {
            surface.getVolumeDictionary().add("mode", "none");
        }
    }

    void readLayers(Surface surface) {
        if (surface.hasRegions()) {
            if (surface.isSingleton()) {
                if (surface.isAppendRegionName()) {
                    Region region = surface.getRegions()[0];
                    if (layers.found(region.getPatchName())) {
                        region.setLayerDictionary(layers.subDict(region.getPatchName()));
                    }
                } else {
                    if (layers.found(surface.getPatchName())) {
                        surface.setLayerDictionary(layers.subDict(surface.getPatchName()));
                    }
                }
            } else {
                for (Region region : surface.getRegions()) {
                    if (layers.found(region.getPatchName())) {
                        region.setLayerDictionary(layers.subDict(region.getPatchName()));
                    }
                }
            }
        } else {
            if (layers.found(surface.getPatchName())) {
                surface.setLayerDictionary(layers.subDict(surface.getPatchName()));
            }
        }
    }

    // private boolean levelNotZeroZero(Dictionary surfaceDictionary) {
    // if (surfaceDictionary.found("level")) {
    // String[] level = surfaceDictionary.lookupArray("level");
    // return Integer.parseInt(level[0]) != 0 || Integer.parseInt(level[1]) !=
    // 0;
    // }
    // return false;
    // }
}

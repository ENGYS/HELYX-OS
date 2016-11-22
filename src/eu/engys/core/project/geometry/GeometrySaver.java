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
import static eu.engys.core.project.system.SnappyHexMeshDict.BAFFLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.BOUNDARY_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CASTELLATED_MESH_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CELL_ZONE_INSIDE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CELL_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.DISTANCE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.EXPANSION_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_TYPE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FCH_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FEATURES_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FINAL_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.GEOMETRY_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.GROWN_UP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.INSIDE;
import static eu.engys.core.project.system.SnappyHexMeshDict.IS_CELL_ZONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LAYERS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LEVELS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LEVEL_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_CELLS_ACROSS_GAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MODE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.NONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SURFACE_LAYERS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.OUTSIDE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.PROXIMITY_INCREMENT_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINEMENTS_REGIONS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REFINEMENTS_SURFACES_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.REGIONS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.TWO_SIDED_KEY;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.surface.Region;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.util.Util;

public class GeometrySaver {

    private Logger logger = LoggerFactory.getLogger(GeometrySaver.class);

    private Model model;
    private Geometry geometry;

    private Dictionary geometryDict;
    private Dictionary castellatedDict;
    private Dictionary refinementSurfaces;
    private Dictionary refinementRegions;
    private Dictionary layers;

    public GeometrySaver(Model model, Geometry geometry) {
        this.model = model;
        this.geometry = geometry;
    }

    public void save() {
        SnappyHexMeshDict snappyHexMeshDict = model.getProject().getSystemFolder().getSnappyHexMeshDict();

        if (snappyHexMeshDict == null) {
            return;
        }
        initDictionaries(snappyHexMeshDict);

        saveFeatureLines(model);
        saveSurfaces(model);
        saveBlock();

        // logger.info("Geometry: \n {}", snappyHexMeshDict);
    }

    private void initDictionaries(SnappyHexMeshDict snappyHexMeshDict) {
        geometryDict = snappyHexMeshDict.subDict(GEOMETRY_KEY);
        layers = snappyHexMeshDict.subDict(ADD_LAYERS_CONTROLS_KEY).subDict(LAYERS_KEY);
        geometryDict.clear();
        layers.clear();
        
        castellatedDict = snappyHexMeshDict.subDict(CASTELLATED_MESH_CONTROLS_KEY);
        if(castellatedDict != null){
            refinementSurfaces = castellatedDict.subDict(REFINEMENTS_SURFACES_KEY);
            refinementRegions = castellatedDict.subDict(REFINEMENTS_REGIONS_KEY);
            refinementSurfaces.clear();
            refinementRegions.clear();
        }
    }

    private void saveFeatureLines(Model model) {
        ListField lines = new ListField(FEATURES_KEY);
        for (FeatureLine line : geometry.getLines()) {
            lines.add(line.toDictionary());
        }
        castellatedDict.add(lines);
    }

    private void saveSurfaces(Model model) {
        for (Surface surface : geometry.getSurfaces()) {
            saveToGeometry(surface);
            if (isACellZone(surface)) {
                saveToRefinementSurfacesAsCellZone(surface);
            } else if (hasSurfaceRefinement(surface)) {
                saveToRefinementSurfaces(surface);
            }
            saveToRefinementRegions(surface);

            saveToLayers(surface);

            fixEmptySurfacesToZeroLevel(surface);
        }
    }

    private void saveToGeometry(Surface surface) {
        Dictionary geometryDictionary = surface.toGeometryDictionary();
        if (geometryDictionary != null && geometryDictionary.found(Dictionary.TYPE)) {
            geometryDict.add(geometryDictionary);
        }
    }

    private void saveToRefinementSurfacesAsCellZone(Surface surface) {
        Dictionary surfaceDictionary = surface.getSurfaceDictionary();
        if (surfaceDictionary.found(LEVEL_KEY)) {
            Dictionary zoneDictionary = surface.getZoneDictionary();
            Dictionary sd = new Dictionary(zoneDictionary);

            copyIfFound(sd, surfaceDictionary, LEVEL_KEY);
            copyIfFound(sd, surfaceDictionary, PROXIMITY_INCREMENT_KEY);
            copyIfFound(sd, surfaceDictionary, MAX_CELLS_ACROSS_GAP_KEY);

            if (sd.found(FACE_ZONE_KEY)) {
                sd.add(CELL_ZONE_INSIDE_KEY, INSIDE);
                if (sd.found(IS_CELL_ZONE_KEY)) {
                    if (sd.lookup(IS_CELL_ZONE_KEY).equals("true")) {
                        if (sd.found(CELL_ZONE_KEY)) {
                            sd.add(CELL_ZONE_KEY, sd.lookup(CELL_ZONE_KEY));
                        } else {
                            sd.add(CELL_ZONE_KEY, sd.lookup(FACE_ZONE_KEY));
                        }
                    } else {
                        sd.remove(CELL_ZONE_KEY);
                    }
                    sd.remove(IS_CELL_ZONE_KEY);
                }
                sd.remove(REGIONS_KEY);
            }
            refinementSurfaces.add(sd);
        }
    }

    private void saveToRefinementSurfaces(Surface surface) {
        Dictionary surfaceDictionary = surface.getSurfaceDictionary();
        if (surface.getType().isStl()) {
            Stl stl = (Stl) surface;
            Dictionary regions = null;

            if (surfaceDictionary.found(REGIONS_KEY)) {
                regions = surfaceDictionary.subDict(REGIONS_KEY);
            } else {
                regions = new Dictionary(REGIONS_KEY);
                surfaceDictionary.add(regions);
            }

            for (Region region : stl.getRegions()) {
                Dictionary regionDictionary = region.getSurfaceDictionary();
                if (!surfaceRefinementIsZero(region)) {
                    regions.add(regionDictionary);
                }
            }

            if (regions.isEmpty()) {
                surfaceDictionary.remove(REGIONS_KEY);
            }

            if (surfaceDictionary.found(REGIONS_KEY) && !surfaceDictionary.found(LEVEL_KEY)) {
                surfaceDictionary.add(LEVEL_KEY, "(0 0)");
            }
        }

        if (!surfaceDictionary.isEmpty()) {
            refinementSurfaces.add(surfaceDictionary);
        } else {
            refinementSurfaces.remove(surfaceDictionary.getName());
        }
    }

    private void saveToRefinementRegions(Surface surface) {
        Dictionary volumeDictionary = surface.getVolumeDictionary();
        if (volumeDictionary.found(LEVELS_KEY)) {
            String mode = volumeDictionary.lookup(MODE_KEY);
            if (mode != null && (mode.equals(INSIDE) || mode.equals(OUTSIDE_KEY) || mode.equals(DISTANCE_KEY))) {
                refinementRegions.add(new Dictionary(volumeDictionary));
            }
        }
    }

    private void saveToLayers(Surface surface) {
        if (surface.hasRegions()) {
            if (surface.isSingleton()) {
                Region region = surface.getRegions()[0];
                if (hasLayers(region)) {
                    addToLayers(region.getPatchName(), region);
                } else if (hasLayers(surface)) {
                    addToLayers(region.getPatchName(), surface);
                }
            } else {
                for (Region region : surface.getRegions()) {
                    if (hasLayers(region)) {
                        addToLayers(region.getPatchName(), region);
                        if (isBoundaryOrBaffleZone(surface.getZoneDictionary())) {
                            addToLayers(region.getPatchName() + "_slave", region);
                        }
                    } else if (hasLayers(surface)) {
                        addToLayers(region.getPatchName(), surface);
                        if (isBoundaryOrBaffleZone(surface.getZoneDictionary())) {
                            addToLayers(region.getPatchName() + "_slave", surface);
                        }
                    }
                }
            }
        } else {
            if (hasLayers(surface)) {
                addToLayers(surface.getPatchName(), surface);
                if (isBoundaryOrBaffleZone(surface.getZoneDictionary())) {
                    addToLayers(surface.getPatchName() + "_slave", surface);
                }
            }
        }
    }

    private void saveBlock() {
        if (geometry.hasBlock()) {
            saveToLayers(geometry.getBlock());
        }
    }

    private void addToLayers(String patchName, Surface surface) {
        if (isGrownUpLayers(surface)) {
            Dictionary layerDict = new Dictionary(patchName);
            layerDict.add(GROWN_UP_KEY, "true");
            layerDict.add(N_SURFACE_LAYERS_KEY, "0");
            layers.add(layerDict);
        } else {
            Dictionary layerDict = new Dictionary(surface.getLayerDictionary());
            layerDict.setName(patchName);
            layers.add(layerDict);
        }
    }

    private boolean hasSurfaceRefinement(Surface surface) {
        Dictionary volumeDictionary = surface.getVolumeDictionary();
        return volumeDictionary == null || !volumeDictionary.found(MODE_KEY) || volumeDictionary.lookup(MODE_KEY).equals(DISTANCE_KEY) || volumeDictionary.lookup(MODE_KEY).equals(NONE_KEY);
    }

    private void fixEmptySurfacesToZeroLevel(Surface surface) {
        if (!refinementSurfaces.found(surface.getName()) && !refinementRegions.found(surface.getName())) {
            Dictionary surfaceDictionary = surface.getSurfaceDictionary();
            surfaceDictionary.add(LEVEL_KEY, "(0 0)");
            refinementSurfaces.add(surfaceDictionary);
        }
    }

    private boolean surfaceRefinementIsZero(Surface surface) {
        Dictionary surfaceDictionary = surface.getSurfaceDictionary();
        return surfaceDictionary.isEmpty() || (surfaceDictionary.found(LEVEL_KEY) && Arrays.equals(surfaceDictionary.lookupIntArray(LEVEL_KEY), new int[2]));
    }

    private boolean isACellZone(Surface surface) {
        Dictionary zoneDictionary = surface.getZoneDictionary();
        return zoneDictionary != null && zoneDictionary.found(FACE_TYPE_KEY) && !zoneDictionary.lookup(FACE_TYPE_KEY).equals(NONE_KEY);
    }

    private boolean isGrownUpLayers(Surface surface) {
        Dictionary layerDictionary = surface.getLayerDictionary();
        return layerDictionary.found(GROWN_UP_KEY) && layerDictionary.lookup(GROWN_UP_KEY).equals("true");
    }

    private boolean hasNSurfaceLayers(Surface surface) {
        Dictionary layerDictionary = surface.getLayerDictionary();
        return layerDictionary.found(N_SURFACE_LAYERS_KEY) && !layerDictionary.lookup(N_SURFACE_LAYERS_KEY).equals("0");
    }

    private boolean hasLayers(Surface surface) {
        Dictionary layerDictionary = surface.getLayerDictionary();
        int total = checkValue(layerDictionary, N_SURFACE_LAYERS_KEY) + checkValue(layerDictionary, MAX_LAYER_THICKNESS_KEY) + checkValue(layerDictionary, FINAL_LAYER_THICKNESS_KEY) + checkValue(layerDictionary, EXPANSION_RATIO_KEY) + checkValue(layerDictionary, FCH_KEY);

        return isGrownUpLayers(surface) || hasNSurfaceLayers(surface) || (total > 0 && total == 3);
    }

    private int checkValue(Dictionary d, String key) {
        return Util.boolToInt(d.found(key) && d.lookupDouble(key) != 0);
    }

    private boolean isTwoSided(Dictionary surfaceDictionary) {
        return surfaceDictionary.found(TWO_SIDED_KEY) && Boolean.parseBoolean(surfaceDictionary.lookup(TWO_SIDED_KEY));
    }

    private boolean isBoundaryOrBaffleZone(Dictionary zoneDictionary) {
        return zoneDictionary.found(FACE_TYPE_KEY) && (zoneDictionary.lookup(FACE_TYPE_KEY).equals(BAFFLE_KEY) || zoneDictionary.lookup(FACE_TYPE_KEY).equals(BOUNDARY_KEY));
    }
}

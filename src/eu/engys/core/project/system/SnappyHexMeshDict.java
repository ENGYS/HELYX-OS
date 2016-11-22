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

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;

public class SnappyHexMeshDict extends Dictionary {


    public static final String SNAPPY_DICT = "snappyHexMeshDict";

    // GENERAL
    public static final String CASTELLATED_MESH_KEY = "castellatedMesh";
    public static final String SNAP_KEY = "snap";
    public static final String ADD_LAYERS_KEY = "addLayers";
    public static final String AUTO_BLOCK_MESH_KEY = "autoBlockMesh";
    public static final String BLOCK_DATA_KEY = "blockData";
    public static final String CRACK_DETECTION_KEY = "crackDetection";
    public static final String CRACK_TOL_KEY = "crackTol";
    public static final String FINAL_DECOMPOSITION_KEY = "finalDecomposition";
    public static final String HIERARCHICAL_KEY = "hierarchical";
    public static final String PTSCOTCH_KEY = "ptscotch";
    public static final String[] DECOMPOSITION_KEYS = new String[] { PTSCOTCH_KEY, HIERARCHICAL_KEY };

    // CASTELLATED
    public static final String CASTELLATED_MESH_CONTROLS_KEY = "castellatedMeshControls";

    public static final String LOCATION_IN_MESH = "locationInMesh";
    public static final String MAX_LOCAL_CELLS_KEY = "maxLocalCells";
    public static final String MAX_GLOBAL_CELLS_KEY = "maxGlobalCells";
    public static final String MIN_REFINEMENT_CELLS_KEY = "minRefinementCells";
    public static final String N_CELLS_BETWEEN_LEVELS_KEY = "nCellsBetweenLevels";
    public static final String RESOLVE_FEATURE_ANGLE_KEY = "resolveFeatureAngle";
    public static final String FEATURE_REFINE_ANGLE_KEY = "featureRefineAngle";
    public static final String REFINE_SURFACE_BOUNDARY_KEY = "refineSurfaceBoundary";
    public static final String MIN_BAFFLE_ANGLE_KEY = "minBaffleAngle";
    public static final String CURVATURE_REFINE_ANGLE_KEY = "curvatureRefineAngle";
    public static final String ALLOW_FREE_STANDING_ZONE_FACES_KEY = "allowFreeStandingZoneFaces";
    public static final String BALANCE_THEN_REFINE_KEY = "balanceThenRefine";
    public static final String MAX_LOAD_UNBALANCE_KEY = "maxLoadUnbalance";
    public static final String SPLIT_CELLS_KEY = "splitCells";
    public static final String MIN_ZONE_REGION_SIZE_KEY = "minZoneRegionSize";

    // CASTELLATED-OS
    public static final String PLANAR_ANGLE_KEY = "planarAngle";

    // SNAP
    public static final String SNAP_CONTROLS_KEY = "snapControls";

    public static final String GLOBAL_FEATURE_EDGES_KEY = "globalFeatureEdges";
    public static final String N_SMOOTH_PATCH_KEY = "nSmoothPatch";
    public static final String DIRECT_FEATURE_SNAPPING_KEY = "directFeatureSnapping";
    public static final String TOLERANCE_KEY = "tolerance";
    public static final String REGION_FEATURE_LINES_KEY = "regionFeatureLines";
    public static final String N_RELAX_ITER_SNAP_KEY = "nRelaxIter";
    public static final String GLOBAL_REGION_SNAP_KEY = "globalRegionSnap";
    public static final String SNAP_SURF_BOUNDARY_KEY = "snapSurfBoundary";
    public static final String COLLAPSE_TOL_KEY = "collapseTol";
    public static final String SPLIT_DEGENERATE_CELLS_KEY = "splitDegenerateCells";
    public static final String N_PRE_FEATURE_ITER_KEY = "nPreFeatureIter";
    public static final String EXPLICIT_FEATURE_SNAP_KEY = "explicitFeatureSnap";
    public static final String IMPLICIT_FEATURE_SNAP_KEY = "implicitFeatureSnap";
    public static final String GEOMETRY_FEATURE_LINES_KEY = "geometryFeatureLines";
    public static final String ZONE_FEATURE_SNAPPING_KEY = "zoneFeatureSnapping";
    public static final String N_SOLVER_ITER_KEY = "nSolveIter";
    public static final String N_FEATURE_ITER_KEY = "nFeatureIter";
    public static final String N_OUTER_ITER_KEY = "nOuterIter";
    public static final String N_SLIVER_SMOOTHS_KEY = "nSliverSmooths";
    public static final String ENLARGE_STENCIL_KEY = "enlargeStencil";
    public static final String FEATURE_SNAP_CHECKS_KEY = "featureSnapChecks";
    public static final String SMOOTH_SNAPPED_SURFACE_KEY = "smoothSnappedSurface";

    // SNAP-OS
    public static final String MULTI_REGION_FEATURE_SNAP_KEY = "multiRegionFeatureSnap";
    public static final String N_FEATURE_SNAP_ITER_KEY = "nFeatureSnapIter";

    // LAYERS
    public static final String ADD_LAYERS_CONTROLS_KEY = "addLayersControls";

    public static final String EXPANSION_RATIO_KEY = "expansionRatio";
    public static final String FINAL_LAYER_THICKNESS_KEY = "finalLayerThickness";
    public static final String RELATIVE_SIZES_KEY = "relativeSizes";
    public static final String MIN_THICKNESS_KEY = "minThickness";
    public static final String FEATURE_ANGLE_MERGE_KEY = "featureAngleMerge";
    public static final String FEATURE_ANGLE_TERMINATE_KEY = "featureAngleTerminate";
    public static final String N_RELAX_ITER_LAYERS_KEY = "nRelaxIter";
    public static final String N_RELAXED_ITER_KEY = "nRelaxedIter";
    public static final String MIN_MEDIAL_AXIS_ANGLE_KEY = "minMedialAxisAngle";
    public static final String NO_ERRORS_KEY = "noErrors";
    public static final String MAX_LAYER_ITER_KEY = "maxLayerIter";
    public static final String MAX_THICKNESS_TO_MEDIAL_RATIO_KEY = "maxThicknessToMedialRatio";
    public static final String MAX_FACE_THICKNESS_RATIO_KEY = "maxFaceThicknessRatio";
    public static final String WRITE_VTK_KEY = "writeVTK";
    public static final String PROJECT_GROWN_UP_KEY = "projectGrownUp";
    public static final String LAYER_RECOVERY_KEY = "layerRecovery";
    public static final String PRE_BALANCE_KEY = "preBalance";
    public static final String GROW_CONCAVE_EDGE_KEY = "growConcaveEdge";
    public static final String GROW_CONVEX_EDGE_KEY = "growConvexEdge";
    public static final String GROW_ZONE_LAYERS_KEY = "growZoneLayers";
    public static final String GROW_UP_PATCHES_KEY = "growUpPatches";
    public static final String N_SMOOTH_SURFACE_NORMALS_KEY = "nSmoothSurfaceNormals";
    public static final String N_SMOOTH_NORMALS_KEY = "nSmoothNormals";
    public static final String MAX_CELL_DISTORTION_KEY = "maxCellDistortion";
    public static final String MAX_PROJECTION_DISTANCE_KEY = "maxProjectionDistance";

    // LAYERS-OS
    public static final String N_GROW_KEY = "nGrow";
    public static final String N_LAYER_ITER_KEY = "nLayerIter";
    public static final String N_BUFFER_CELLS_NO_EXTRUDE_KEY = "nBufferCellsNoExtrude";
    public static final String N_SMOOTH_THICKNESS_KEY = "nSmoothThickness";
    public static final String SLIP_FEATURE_ANGLE_KEY = "slipFeatureAngle";
    public static final String FEATURE_ANGLE_KEY = "featureAngle";

    // QUALITY
    public static final String MESH_QUALITY_CONTROLS_KEY = "meshQualityControls";

    public static final String N_VOL_SMOOTH_ITER_KEY = "nVolSmoothIter";
    public static final String MAX_NON_ORTHO_KEY = "maxNonOrtho";
    public static final String MAX_BOUNDARY_SKEWNESS_KEY = "maxBoundarySkewness";
    public static final String MAX_INTERNAL_SKEWNESS_KEY = "maxInternalSkewness";
    public static final String MAX_CONCAVE_KEY = "maxConcave";
    public static final String MIN_FLATNESS_KEY = "minFlatness";
    public static final String MIN_VOL_KEY = "minVol";
    public static final String MIN_TET_QUALITY_KEY = "minTetQuality";
    public static final String MIN_AREA_KEY = "minArea";
    public static final String MIN_TWIST_KEY = "minTwist";
    public static final String MIN_DETERMINANT_KEY = "minDeterminant";
    public static final String MIN_FACE_WEIGHT_KEY = "minFaceWeight";
    public static final String MIN_VOL_RATIO_KEY = "minVolRatio";
    public static final String MIN_TRIANGLE_TWIST_KEY = "minTriangleTwist";
    public static final String MIN_VOL_COLLAPSE_RATIO_KEY = "minVolCollapseRatio";
    public static final String MIN_INTERNAL_WARPAGE_KEY = "minInternalWarpage";
    public static final String MAX_BOUNDARY_WARPAGE_KEY = "maxBoundaryWarpage";
    public static final String FACE_FACE_CELLS_KEY = "faceFaceCells";
    public static final String MIN_SNAP_RELATIVE_VOLUME_KEY = "minSnapRelativeVolume";
    public static final String N_SMOOTH_SCALE_KEY = "nSmoothScale";
    public static final String SMOOTH_ALIGNED_EDGES_KEY = "smoothAlignedEdges";
    public static final String ERROR_REDUCTION_KEY = "errorReduction";
    public static final String MIN_SNAP_RELATIVE_TET_VOLUME_KEY = "minSnapRelativeTetVolume";
    public static final String MAX_GAUSS_GREEN_CENTROID_KEY = "maxGaussGreenCentroid";
    public static final String BAFFLE_ALL_POINTS_BOUNDARY_KEY = "baffleAllPointsBoundary";

    // REPATCH
    public static final String REPATCH_REGIONS_KEY = "repatchRegions";

    public static final String LOCATION_KEY = "location";
    public static final String ZONE_KEY = "zone";
    public static final String EXCLUDE_PATCHES_KEY = "excludePatches";
    public static final String PATCH_KEY = "patch";

    // WRAPPER
    public static final String WRAPPER_KEY = "wrapper";

    public static final String WRAP_KEY = "wrap";
    public static final String OUTLETS_KEY = "outlets";
    public static final String VOL_SOURCES_KEY = "volSources";
    public static final String VOL_DISTANCE_KEY = "volDistance";
    public static final String MESH_IN_MM_KEY = "meshInMM";
    public static final String MAX_ITER_KEY = "maxIter";
    public static final String WRITE_FIELDS_KEY = "writeFields";
    public static final String INVERT_KEY = "invert";
    public static final String SIGMA_KEY = "sigma";
    public static final String CUTOFF_KEY = "cutoff";
    public static final String EXCLUDE_POINTS_KEY = "excludePoints";

    // MISC
    public static final String DEBUG_KEY = "debug";
    public static final String MERGE_TOLERANCE_KEY = "mergeTolerance";

    // FEATURES LINES
    public static final String FILE_KEY = "file";
    public static final String FEATURES_KEY = "features";
    public static final String REFINE_FEATURE_EDGES_ONLY_KEY = "refineFeatureEdgesOnly";

    // REFINEMENT SURFACES + REGIONS
    public static final String REFINEMENTS_SURFACES_KEY = "refinementSurfaces";
    public static final String REFINEMENTS_REGIONS_KEY = "refinementRegions";
    public static final String PROXIMITY_INCREMENT_KEY = "proximityIncrement";

    // GEOMETRY
    public static final String GEOMETRY_KEY = "geometry";
    public static final String LAYERS_KEY = "layers";
    public static final String GAP_LEVEL_INCREMENT_KEY = "gapLevelIncrement";
    public static final String MAX_CELLS_ACROSS_GAP_KEY = "maxCellsAcrossGap";
    public static final String MAX_LAYER_THICKNESS_KEY = "maxLayerThickness";
    public static final String FCH_KEY = "fch";
    public static final String GROWN_UP_KEY = "grownUp";
    public static final String N_SURFACE_LAYERS_KEY = "nSurfaceLayers";

    public static final String MODE_KEY = "mode";
    public static final String INSIDE = "inside";
    public static final String OUTSIDE_KEY = "outside";
    public static final String DISTANCE_KEY = "distance";
    public static final String NONE_KEY = "none";
    public static final String TWO_SIDED_KEY = "twoSided";

    // ZONES
    public static final String FACE_TYPE_KEY = "faceType";
    public static final String LEVEL_KEY = "level";
    public static final String LEVELS_KEY = "levels";
    public static final String REGIONS_KEY = "regions";
    public static final String CELL_ZONE_KEY = "cellZone";
    public static final String FACE_ZONE_KEY = "faceZone";
    public static final String CELL_ZONE_INSIDE_KEY = "cellZoneInside";
    public static final String IS_CELL_ZONE_KEY = "isCellZone";

    public static final String INTERNAL_KEY = "internal";
    public static final String BOUNDARY_KEY = "boundary";
    public static final String BAFFLE_KEY = "baffle";

    public static final String BAFFLE_CHECKS_KEY = "baffleChecks";

    public SnappyHexMeshDict() {
        super(SNAPPY_DICT);
        setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, SNAPPY_DICT));
    }

    public SnappyHexMeshDict(File snappyHexMeshFile) {
        super(snappyHexMeshFile);
    }

    public SnappyHexMeshDict(SnappyHexMeshDict snappyHexMeshDict) {
        super(snappyHexMeshDict);
    }

    public void check() throws DictionaryException {
    }

    public Dictionary getGeometry() {
        return subDict(GEOMETRY_KEY);
    }

    public boolean isAutoBlockMesh() {
        return found(AUTO_BLOCK_MESH_KEY) && Boolean.parseBoolean(lookup(AUTO_BLOCK_MESH_KEY));
    }

    // protected void parseDictionary(String text) {
    // Pattern pattern = Pattern.compile("features\\s+\\(([^)]*)\\s*\\)\\s*;");
    // Matcher matcher = pattern.matcher(text);
    // StringBuffer result = new StringBuffer();
    // String group = null;
    // if (matcher.find()) {
    // if (matcher.groupCount() == 1) {
    // group = matcher.group(1);
    // //System.out.println("SnappyHexMeshDict.parseDictionary() "+group);
    // }
    // matcher.appendReplacement(result, "");
    // matcher.appendTail(result);
    //
    // text = result.toString();
    // }
    // super.parseDictionary(text);
    //
    // if (group != null && found("castellatedMeshControls")) {
    // subDict("castellatedMeshControls").add("features", "("+group+")");
    // }
    //
    // }
    //
}

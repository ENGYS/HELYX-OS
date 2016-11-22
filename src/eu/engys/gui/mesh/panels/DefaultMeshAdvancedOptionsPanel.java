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
package eu.engys.gui.mesh.panels;

import static eu.engys.core.project.system.SnappyHexMeshDict.ADD_LAYERS_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CASTELLATED_MESH_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.ERROR_REDUCTION_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_BOUNDARY_SKEWNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_CONCAVE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_INTERNAL_SKEWNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_NON_ORTHO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MESH_QUALITY_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_AREA_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_DETERMINANT_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_FACE_WEIGHT_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_FLATNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_TET_QUALITY_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_TRIANGLE_TWIST_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_TWIST_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_VOL_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_VOL_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SMOOTH_SCALE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.SNAP_CONTROLS_KEY;
import static eu.engys.util.TooltipUtils.NEW_LINE;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class DefaultMeshAdvancedOptionsPanel extends DefaultGUIPanel {

    public static final String NAME = "general.options.tabbedPane";
    
    public static final String GENERAL_LABEL = "General";
    public static final String CASTELLATED_MESH_CONTROLS_LABEL = "Refinements";
    public static final String SNAP_CONTROLS_LABEL = "Snapping";
    public static final String LAYERS_CONTROLS_LABEL = "Layers";
    public static final String QUALITY_CONTROLS_LABEL = "Quality";

    // GENERAL
    public static final String CASTELLATED_MESH_LABEL = "Castellated Mesh";
    public static final String CASTELLATED_MESH_TOOLTIP = "Generate a refined castellated mesh";
    public static final String SNAPPING_LABEL = "Snapping";
    public static final String SNAPPING_TOOLTIP = "Snap surface mesh points to the geometry and merge boundary faces";
    public static final String LAYERS_ADDITION_LABEL = "Layers Addition";
    public static final String LAYERS_ADDITION_TOOLTIP = "Perform generation of layers";
    public static final String DEBUG_LABEL = "Debug";
    public static final String DEBUG_TOOLTIP = "Control of debug output:" + NEW_LINE + "0: Write only final mesh" + NEW_LINE + "1: Write intermediate meshes" + NEW_LINE + "2: Write cell level information";
    public static final String MERGE_TOLERANCE_LABEL = "Merge Tolerance";
    public static final String MERGE_TOLERANCE_TOOLTIP = "Merge tolerance specified as a function of the initial bounding box size";

    // CASTELLATED
    public static final String MAX_LOCAL_CELLS_LABEL = "Max Local Cells";
    public static final String MAX_LOCAL_CELLS_TOOLTIP = "Termination of refinement if number of cells on processor n exceeds this value";
    public static final String MAX_GLOBAL_CELLS_LABEL = "Max Global Cells";
    public static final String MAX_GLOBAL_CELLS_TOOLTIP = "No further refinement if global number of cells exceeds this value";
    public static final String MIN_REFINEMENT_CELLS_LABEL = "Min Refinement Cells";
    public static final String MIN_REFINEMENT_CELLS_TOOLTIP = "Terminate refinement iteration if number of cells to refine falls below this value";
    public static final String MAX_LOAD_UNBALANCE_LABEL = "Max Load Unbalance";
    public static final String MAX_LOAD_UNBALANCE_TOOLTIP = "When running in parallel allow some imbalance in the number of cells per processor to avoid expensive re-balancing operations. This value is expressed as a fraction of perfect balance (i.e. overall number of cells / number of processors). Set to 0 will always perform balancing";
    public static final String CELLS_BETWEEN_LEVELS_LABEL = "Cells Between Levels";
    public static final String CELLS_BETWEEN_LEVELS_TOOLTIP = "Specify the number of buffer layers between two neighbouring volume refinement levels in the mesh";
    public static final String RESOLVE_FEATURE_ANGLE_LABEL = "Resolve Feature Angle";
    public static final String RESOLVE_FEATURE_ANGLE_TOOLTIP = "The surface is refined to the maximum level if local curvature is greater than this angle. Set to a negative value to disable";
    public static final String ALLOW_FREE_STANDING_ZONE_FACES_LABEL = "Allow Free Standing Zone Faces";
    public static final String ALLOW_FREE_STANDING_ZONE_FACES_TOOLTIP = "Allow free standing zone faces in addition to zone faces in between different cell zones. Can be set globally or on a per zone basis";

    // SNAP
    public static final String RELAX_ITERATIONS_SNAP_LABEL = "Correction Steps";
    public static final String RELAX_ITERATIONS_SNAP_TOOLTIP = "Maximum number of corrections steps to revert mesh back to error free state";
    public static final String TOLERANCE_LABEL = "Tolerance";
    public static final String TOLERANCE_TOOLTIP = "Relative distance for points to be attached by surface feature point";
    public static final String SMOOTH_PATCH_LABEL = "Smooth Patch";
    public static final String SMOOTH_PATCH_TOOLTIP = "Number of patch smoothing iterations performed finding correspondance to surface";
    public static final String SOLVER_ITERATIONS_LABEL = "Solver Iterations";
    public static final String SOLVER_ITERATIONS_TOOLTIP = "Specifies the number of displacement smoothing iterations";

    // LAYERS
    public static final String RELAX_ITERATIONS_LAYERS_LABEL = "Correction Steps";
    public static final String RELAX_ITERATIONS_LAYERS_TOOLTIP = "Number of correction steps to revert mesh back to error free state";
    public static final String RELAXED_ITERATIONS_LABEL = "Relaxed Layer Iterations";
    public static final String RELAXED_ITERATIONS_TOOLTIP = "Number of layer iterations before a set of relaxed mesh quality constraints are applied";
    public static final String MIN_MEDIAL_AXIS_ANGLE_LABEL = "Min Medial Axis Angle";
    public static final String MIN_MEDIAL_AXIS_ANGLE_TOOLTIP = "Angle used to select medial axis points";
    public static final String MAX_THICKNESS_TO_MEDIAL_RATIO_LABEL = "Max Thickness To Medial Ratio";
    public static final String MAX_THICKNESS_TO_MEDIAL_RATIO_TOOLTIP = "Reduce layer thickness where ratio of layer thickness to distance to medial axis is above this value";
    public static final String MAX_FACE_THICKNESS_RATIO_LABEL = "Max Face Thickness Ratio";
    public static final String MAX_FACE_THICKNESS_RATIO_TOOLTIP = "Measure of surface face warp-age. Layer growth is terminated on faces where this value is exceeded";
    public static final String INTERIOR_MESH_SMOOTHING_ITERATIONS_LABEL = "Interior Mesh Smoothing Iterations";
    public static final String INTERIOR_MESH_SMOOTHING_ITERATIONS_TOOLTIP = "Number of interior normals smoothing iterations performed before projecting the mesh";
    public static final String SURFACE_NORMALS_SMOOTHING_ITERATIONS_LABEL = "Surface Normals Smoothing Iterations";
    public static final String SURFACE_NORMALS_SMOOTHING_ITERATIONS_TOOLTIP = "Number of surface normals smoothing iterations performed before projecting the mesh";
    public static final String MIN_THICKNESS_LABEL = "Min Thickness";
    public static final String MIN_THICKNESS_TOOLTIP = "Relative measure of layer thickness, layers terminated if their thickness fall below this value";
//    public static final String RELATIVE_SIZES_LABEL = "Relative Sizes";
//    public static final String RELATIVE_SIZES_TOOLTIP = "Whether to use a relative or absolute sizing for layer control";
    public static final String FINAL_LAYER_THICKNESS_LABEL = "Final Layer Thickness";
    public static final String FINAL_LAYER_THICKNESS_TOOLTIP = "Requested thickness for final cell layer";
    public static final String EXPANSION_RATIO_LABEL = "Expansion Ratio";
    public static final String EXPANSION_RATIO_TOOLTIP = "Global setting of growth factor for layer growth. Can be overwritten locally on each patch";

    // QUALITY
    public static final String ERROR_REDUCTION_LABEL = "Error Reduction";
    public static final String ERROR_REDUCTION_TOOLTIP = "Ratio used for scaling of displacement during each error reduction iteration";
    public static final String SMOOTH_SCALE_LABEL = "Smooth Scale";
    public static final String SMOOTH_SCALE_TOOLTIP = "Number of sub-smoothing iterations during scaling back";
    public static final String MIN_TRIANGLE_TWIST_LABEL = "Min Triangle Twist";
    public static final String MIN_TRIANGLE_TWIST_TOOLTIP = "Minimum triangle twist. Set to a positive value for Fluent compatibility";
    public static final String MIN_VOL_RATIO_LABEL = "Min Vol Ratio";
    public static final String MIN_VOL_RATIO_TOOLTIP = "Minimum volume ratio between adjacent cells. Set to a negative value to disable";
    public static final String MIN_FACE_WEIGHT_LABEL = "Min Face Weight";
    public static final String MIN_FACE_WEIGHT_TOOLTIP = "Face based interpolation weight metric. Set to a negative value to disable";
    public static final String MIN_DETERMINANT_LABEL = "Min Determinant";
    public static final String MIN_DETERMINANT_TOOLTIP = "Minimum normalised cell-determinant. Set to a negative value to disable";
    public static final String MIN_TWIST_LABEL = "Min Twist";
    public static final String MIN_TWIST_TOOLTIP = "Minimum face twist. Set to a negative value to disable";
    public static final String MIN_AREA_LABEL = "Min Area";
    public static final String MIN_AREA_TOOLTIP = "Minimum face area. Set to a negative value to disable";
    public static final String MIN_TET_QUALITY_LABEL = "Min Tetrahedral Quality";
    public static final String MIN_TET_QUALITY_TOOLTIP = "Minimum quality of the tetrahedral elements formed by the face-centre and variable base point minimum decomposition triangles and the cell centre. This has to be a positive number for tracking to work. Set to a very large negative number (e.g. -1E30) to disable";
    public static final String MIN_VOL_LABEL = "Min Vol";
    public static final String MIN_VOL_TOOLTIP = "Minimum pyramid volume (absolute). Set to a very large negative number (e.g. -1E30) to disable";
    public static final String MIN_FLATNESS_LABEL = "Min Flatness";
    public static final String MIN_FLATNESS_TOOLTIP = "Ratio of projected area to actual area. Set to a negative value to disable";
    public static final String MAX_CONCAVE_LABEL = "Max Concave";
    public static final String MAX_CONCAVE_TOOLTIP = "Maximum concavity. Set to 180 to disable";
    public static final String MAX_INTERNAL_SKEWNESS_LABEL = "Max Internal Skewness";
    public static final String MAX_INTERNAL_SKEWNESS_TOOLTIP = "Maximum internal skewness. Set to a negative value to disable";
    public static final String MAX_BOUNDARY_SKEWNESS_LABEL = "Max Boundary Skewness";
    public static final String MAX_BOUNDARY_SKEWNESS_TOOLTIP = "Maximum boundary skewness. Set to a negative value to disable";
    public static final String MAX_NON_ORTHO_LABEL = "Max Non Ortho";
    public static final String MAX_NON_ORTHO_TOOLTIP = "Maximum non-orthogonality allowed. Set to 180 to disable";

    protected DictionaryModel snappyHexMeshModel;
    protected DictionaryModel castellatedMeshControlsModel;
    protected DictionaryModel snapControlsModel;
    protected DictionaryModel layersControlsModel;
    protected DictionaryModel meshQualityControlsModel;
    private boolean toDefaults = false;

    protected PanelBuilder qualityBuilder;

    public DefaultMeshAdvancedOptionsPanel(Model model) {
        super("", model);
    }

    @Override
    protected JComponent layoutComponents() {
        snappyHexMeshModel = new DictionaryModel(new Dictionary(""));
        castellatedMeshControlsModel = new DictionaryModel(new Dictionary(""));
        snapControlsModel = new DictionaryModel(new Dictionary(""));
        layersControlsModel = new DictionaryModel(new Dictionary(""));
        meshQualityControlsModel = new DictionaryModel(new Dictionary(""));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName(NAME);
        tabbedPane.add(GENERAL_LABEL, createGeneralPanel());
        tabbedPane.add(CASTELLATED_MESH_CONTROLS_LABEL, createRefinementsPanel());
        tabbedPane.add(SNAP_CONTROLS_LABEL, createSnappingPanel());
        tabbedPane.add(LAYERS_CONTROLS_LABEL, createLayersPanel());
        tabbedPane.add(QUALITY_CONTROLS_LABEL, createQualityPanel());

        return tabbedPane;
    }

    protected abstract JPanel createGeneralPanel();

    protected abstract JPanel createRefinementsPanel();

    protected abstract JPanel createSnappingPanel();

    protected abstract JPanel createLayersPanel();

    protected JPanel createQualityPanel() {
        qualityBuilder = new PanelBuilder("options.quality.panel");
        qualityBuilder.addComponent(MAX_NON_ORTHO_LABEL, MAX_NON_ORTHO_TOOLTIP, meshQualityControlsModel.bindDoubleAngle_180(MAX_NON_ORTHO_KEY));
        qualityBuilder.addComponent(MAX_BOUNDARY_SKEWNESS_LABEL, MAX_BOUNDARY_SKEWNESS_TOOLTIP, meshQualityControlsModel.bindInteger(MAX_BOUNDARY_SKEWNESS_KEY));
        qualityBuilder.addComponent(MAX_INTERNAL_SKEWNESS_LABEL, MAX_INTERNAL_SKEWNESS_TOOLTIP, meshQualityControlsModel.bindInteger(MAX_INTERNAL_SKEWNESS_KEY));
        qualityBuilder.addComponent(MAX_CONCAVE_LABEL, MAX_CONCAVE_TOOLTIP, meshQualityControlsModel.bindDoubleAngle_180(MAX_CONCAVE_KEY));
        qualityBuilder.addComponent(MIN_FLATNESS_LABEL, MIN_FLATNESS_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_FLATNESS_KEY));
        qualityBuilder.addComponent(MIN_VOL_LABEL, MIN_VOL_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_VOL_KEY));
        qualityBuilder.addComponent(MIN_TET_QUALITY_LABEL, MIN_TET_QUALITY_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_TET_QUALITY_KEY));
        qualityBuilder.addComponent(MIN_AREA_LABEL, MIN_AREA_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_AREA_KEY));
        qualityBuilder.addComponent(MIN_TWIST_LABEL, MIN_TWIST_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_TWIST_KEY));
        qualityBuilder.addComponent(MIN_DETERMINANT_LABEL, MIN_DETERMINANT_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_DETERMINANT_KEY));
        qualityBuilder.addComponent(MIN_FACE_WEIGHT_LABEL, MIN_FACE_WEIGHT_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_FACE_WEIGHT_KEY));
        qualityBuilder.addComponent(MIN_VOL_RATIO_LABEL, MIN_VOL_RATIO_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_VOL_RATIO_KEY));
        qualityBuilder.addComponent(MIN_TRIANGLE_TWIST_LABEL, MIN_TRIANGLE_TWIST_TOOLTIP, meshQualityControlsModel.bindDouble(MIN_TRIANGLE_TWIST_KEY));
        qualityBuilder.addComponent(SMOOTH_SCALE_LABEL, SMOOTH_SCALE_TOOLTIP, meshQualityControlsModel.bindIntegerPositive(N_SMOOTH_SCALE_KEY));
        qualityBuilder.addComponent(ERROR_REDUCTION_LABEL, ERROR_REDUCTION_TOOLTIP, meshQualityControlsModel.bindDouble(ERROR_REDUCTION_KEY));
        return qualityBuilder.getPanel();
    }

    @Override
    public void resetToDefaults() {
        this.toDefaults = true;
        load();
        this.toDefaults = false;
    }

    // LOAD

    protected SnappyHexMeshDict getSnappyDict() {
        SnappyHexMeshDict snappyDict = null;
        if (toDefaults) {
            snappyDict = model.getDefaults().getDefaultSnappyHexMeshDict();
        } else {
            snappyDict = new SnappyHexMeshDict(model.getProject().getSystemFolder().getSnappyHexMeshDict());
        }
        return snappyDict;
    }

    protected abstract void loadCastellated(SnappyHexMeshDict snappyDict);

    protected void loadLayers(SnappyHexMeshDict snappyDict) {
        if (snappyDict.found(ADD_LAYERS_CONTROLS_KEY)) {
            Dictionary layersDict = new Dictionary(snappyDict.subDict(ADD_LAYERS_CONTROLS_KEY));
            snappyDict.remove(ADD_LAYERS_CONTROLS_KEY);
            this.layersControlsModel.setDictionary(layersDict);
        }
    }

    protected void loadQuality(SnappyHexMeshDict snappyDict) {
        if (snappyDict.found(MESH_QUALITY_CONTROLS_KEY)) {
            Dictionary quality = new Dictionary(snappyDict.subDict(MESH_QUALITY_CONTROLS_KEY));
            snappyDict.remove(MESH_QUALITY_CONTROLS_KEY);
            this.meshQualityControlsModel.setDictionary(quality);
        }
    }

    protected void loadSnap(SnappyHexMeshDict snappyDict) {
        if (snappyDict.found(SNAP_CONTROLS_KEY)) {
            Dictionary snap = new Dictionary(snappyDict.subDict(SNAP_CONTROLS_KEY));
            snappyDict.remove(SNAP_CONTROLS_KEY);
            this.snapControlsModel.setDictionary(snap);
        }
    }

    // SAVE

    @Override
    public void save() {
        SnappyHexMeshDict snappyHexMeshDict = model.getProject().getSystemFolder().getSnappyHexMeshDict();

        saveMisc(snappyHexMeshDict);
        saveCastellatedSubDict(snappyHexMeshDict);
        saveSnapControlsDict(snappyHexMeshDict);
        saveLayersControlsDict(snappyHexMeshDict);
        saveMeshQualityControlsDict(snappyHexMeshDict);
        saveRepatchDict(snappyHexMeshDict);

        model.getProject().getSystemFolder().setSnappyHexMeshDict(snappyHexMeshDict);
    }

    private void saveMisc(SnappyHexMeshDict snappyDict) {
        snappyDict.merge(snappyHexMeshModel.getDictionary());
    }

    private void saveCastellatedSubDict(SnappyHexMeshDict snappyDict) {
        Dictionary castellated = snappyDict.subDict(CASTELLATED_MESH_CONTROLS_KEY);
        castellated.merge(castellatedMeshControlsModel.getDictionary());
        saveWrapperDict(snappyDict, castellated);
    }

    private void saveSnapControlsDict(SnappyHexMeshDict snappyDict) {
        Dictionary snap = snappyDict.subDict(SNAP_CONTROLS_KEY);
        snap.merge(snapControlsModel.getDictionary());
    }

    private void saveLayersControlsDict(SnappyHexMeshDict snappyDict) {
        Dictionary snap = snappyDict.subDict(ADD_LAYERS_CONTROLS_KEY);
        snap.merge(layersControlsModel.getDictionary());
    }

    private void saveMeshQualityControlsDict(SnappyHexMeshDict snappyDict) {
        Dictionary snap = snappyDict.subDict(MESH_QUALITY_CONTROLS_KEY);
        snap.merge(meshQualityControlsModel.getDictionary());
    }
    
    protected void saveWrapperDict(SnappyHexMeshDict snappyDict, Dictionary castellated) {
    }

    protected void saveRepatchDict(SnappyHexMeshDict snappyDict) {
    }
    
    public void handleClose() {
    }

}

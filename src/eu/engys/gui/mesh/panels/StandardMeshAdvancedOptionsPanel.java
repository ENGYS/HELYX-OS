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


package eu.engys.gui.mesh.panels;

import static eu.engys.core.project.system.SnappyHexMeshDict.ADD_LAYERS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.ALLOW_FREE_STANDING_ZONE_FACES_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CASTELLATED_MESH_CONTROLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.CASTELLATED_MESH_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.DEBUG_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.EXPANSION_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.EXPLICIT_FEATURE_SNAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FEATURE_ANGLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FINAL_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.IMPLICIT_FEATURE_SNAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_FACE_THICKNESS_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_GLOBAL_CELLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_LOAD_UNBALANCE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_LOCAL_CELLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MAX_THICKNESS_TO_MEDIAL_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MERGE_TOLERANCE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_MEDIAL_AXIS_ANGLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_REFINEMENT_CELLS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MULTI_REGION_FEATURE_SNAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_BUFFER_CELLS_NO_EXTRUDE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_CELLS_BETWEEN_LEVELS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_FEATURE_SNAP_ITER_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_GROW_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_LAYER_ITER_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_RELAXED_ITER_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_RELAX_ITER_LAYERS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_RELAX_ITER_SNAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SMOOTH_NORMALS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SMOOTH_PATCH_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SMOOTH_SURFACE_NORMALS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SMOOTH_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SOLVER_ITER_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.PLANAR_ANGLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.RESOLVE_FEATURE_ANGLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.SLIP_FEATURE_ANGLE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.SNAP_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.TOLERANCE_KEY;

import javax.swing.JPanel;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.SnappyHexMeshDict;
import eu.engys.util.ui.builder.PanelBuilder;

public class StandardMeshAdvancedOptionsPanel extends DefaultMeshAdvancedOptionsPanel {

    
    // CASTELLATED
    public static final String PLANAR_ANGLE_LABEL = "Planar Angle";
    public static final String PLANAR_ANGLE_TOOLTIP = "Planar angle";
    
    //SNAP
    public static final String SNAP_FEATURE_ITERATIONS_LABEL = "Snap Feature Iterations";
    public static final String SNAP_FEATURE_ITERATIONS_TOOLTIP = "Number of feature edge snapping iterations";
    public static final String EXPLICIT_SNAP_FEATURE_LABEL = "Explicit Snap Feature";
    public static final String EXPLICIT_SNAP_FEATURE_TOOLTIP = "Use features in castellatedMeshControls";
    public static final String IMPLICIT_SNAP_FEATURE_LABEL = "Implicit Snap Feature";
    public static final String IMPLICIT_SNAP_FEATURE_TOOLTIP = "Detects (geometric only) features by sampling the surface";
    public static final String MULTI_REGION_FEATURE_LABEL = "Multi Region Feature";
    public static final String MULTI_REGION_FEATURE_TOOLTIP = "For explicitFeatureSnap, detect features between multiple surfaces";

    //LAYERS
    public static final String NUMBER_OF_LAYERS_NOT_GROWN_LABEL = "Layers Not Grown";
    public static final String NUMBER_OF_LAYERS_NOT_GROWN_TOOLTIP = "Number of layers of connected faces that are not grown if points get not extruded; helps convergence of layer addition close to features";
    public static final String LAYER_ADDITION_ITERATIONS_LABEL = "Layer Addition Iterations";
    public static final String LAYER_ADDITION_ITERATIONS_TOOLTIP = "Overall maximum number of layer addition iterations";
    public static final String NUMBER_OF_BUFFER_CELLS_LABEL = "Number of Buffer Cells";
    public static final String NUMBER_OF_BUFFER_CELLS_TOOLTIP = "Create buffer region for new layer terminations";
    public static final String SMOOTH_LAYER_THICKNESS_LABEL = "Smooth Layer Thickness";
    public static final String SMOOTH_LAYER_THICKNESS_TOOLTIP = "Smooth layer thickness over surface patches";
    public static final String SLIP_FEATURE_ANGLE_LABEL = "Slip Feature Angle";
    public static final String SLIP_FEATURE_ANGLE_TOOLTIP = "Sliding of vertices along a boundary will occur when the angle between the patch on which layers are added and its neighbouring patch is larger than that specified by this value";
    public static final String FEATURE_ANGLE_LABEL = "Feature Angle";
    public static final String FEATURE_ANGLE_TOOLTIP = "Angle above which surface is not extruded";
    
    @Inject
    public StandardMeshAdvancedOptionsPanel(Model model) {
        super(model);
    }

    @Override
    protected JPanel createGeneralPanel() {
        PanelBuilder builder = new PanelBuilder("options.general.panel");

        builder.addComponent(CASTELLATED_MESH_LABEL, snappyHexMeshModel.bindBoolean(CASTELLATED_MESH_KEY), CASTELLATED_MESH_TOOLTIP);
        builder.addComponent(SNAPPING_LABEL, snappyHexMeshModel.bindBoolean(SNAP_KEY), SNAPPING_TOOLTIP);
        builder.addComponent(LAYERS_ADDITION_LABEL, snappyHexMeshModel.bindBoolean(ADD_LAYERS_KEY), LAYERS_ADDITION_TOOLTIP);

        builder.addComponent(DEBUG_LABEL, snappyHexMeshModel.bindInteger(DEBUG_KEY, 0, 2), DEBUG_TOOLTIP);
        builder.addComponent(MERGE_TOLERANCE_LABEL, snappyHexMeshModel.bindDouble(MERGE_TOLERANCE_KEY), MERGE_TOLERANCE_TOOLTIP);

        return builder.getPanel();
    }

    @Override
    protected JPanel createRefinementsPanel() {
        PanelBuilder builder = new PanelBuilder("options.geometry.panel");
        builder.addComponent(MAX_LOCAL_CELLS_LABEL, castellatedMeshControlsModel.bindIntegerPositive(MAX_LOCAL_CELLS_KEY), MAX_LOCAL_CELLS_TOOLTIP);
        builder.addComponent(MAX_GLOBAL_CELLS_LABEL, castellatedMeshControlsModel.bindIntegerPositive(MAX_GLOBAL_CELLS_KEY), MAX_GLOBAL_CELLS_TOOLTIP);
        builder.addComponent(MIN_REFINEMENT_CELLS_LABEL, castellatedMeshControlsModel.bindIntegerPositive(MIN_REFINEMENT_CELLS_KEY), MIN_REFINEMENT_CELLS_TOOLTIP);
        builder.addComponent(CELLS_BETWEEN_LEVELS_LABEL, castellatedMeshControlsModel.bindInteger(N_CELLS_BETWEEN_LEVELS_KEY, 1, Integer.MAX_VALUE), CELLS_BETWEEN_LEVELS_TOOLTIP);
        builder.addComponent(RESOLVE_FEATURE_ANGLE_LABEL, castellatedMeshControlsModel.bindDoubleAngle_360(RESOLVE_FEATURE_ANGLE_KEY), RESOLVE_FEATURE_ANGLE_TOOLTIP);
        builder.addComponent(ALLOW_FREE_STANDING_ZONE_FACES_LABEL, castellatedMeshControlsModel.bindBoolean(ALLOW_FREE_STANDING_ZONE_FACES_KEY), ALLOW_FREE_STANDING_ZONE_FACES_TOOLTIP);
        builder.addComponent(PLANAR_ANGLE_LABEL, castellatedMeshControlsModel.bindDoubleAngle_360(PLANAR_ANGLE_KEY), PLANAR_ANGLE_TOOLTIP);
        builder.addComponent(MAX_LOAD_UNBALANCE_LABEL, castellatedMeshControlsModel.bindDoublePositive(MAX_LOAD_UNBALANCE_KEY), MAX_LOAD_UNBALANCE_TOOLTIP);
        return builder.getPanel();
    }

    @Override
    protected JPanel createLayersPanel() {
        PanelBuilder builder = new PanelBuilder("options.layers.panel");
        builder.addComponent(EXPANSION_RATIO_LABEL, layersControlsModel.bindDouble(EXPANSION_RATIO_KEY), EXPANSION_RATIO_TOOLTIP);
        builder.addComponent(FINAL_LAYER_THICKNESS_LABEL, layersControlsModel.bindDouble(FINAL_LAYER_THICKNESS_KEY), FINAL_LAYER_THICKNESS_TOOLTIP);
//        builder.addComponent(RELATIVE_SIZES_LABEL, layersControlsModel.bindBoolean(RELATIVE_SIZES_KEY), RELATIVE_SIZES_TOOLTIP);
        builder.addComponent(MIN_THICKNESS_LABEL, layersControlsModel.bindDouble(MIN_THICKNESS_KEY), MIN_THICKNESS_TOOLTIP);
        builder.addComponent(FEATURE_ANGLE_LABEL, layersControlsModel.bindDoubleAngle_360(FEATURE_ANGLE_KEY), FEATURE_ANGLE_TOOLTIP);
        builder.addComponent(SLIP_FEATURE_ANGLE_LABEL, layersControlsModel.bindDoubleAngle_360(SLIP_FEATURE_ANGLE_KEY), SLIP_FEATURE_ANGLE_TOOLTIP);
        builder.addComponent(RELAX_ITERATIONS_LAYERS_LABEL, layersControlsModel.bindIntegerPositive(N_RELAX_ITER_LAYERS_KEY), RELAX_ITERATIONS_LAYERS_TOOLTIP);
        builder.addComponent(RELAXED_ITERATIONS_LABEL, layersControlsModel.bindIntegerPositive(N_RELAXED_ITER_KEY), RELAXED_ITERATIONS_TOOLTIP);
        builder.addComponent(SURFACE_NORMALS_SMOOTHING_ITERATIONS_LABEL, layersControlsModel.bindIntegerPositive(N_SMOOTH_SURFACE_NORMALS_KEY), SURFACE_NORMALS_SMOOTHING_ITERATIONS_TOOLTIP);
        builder.addComponent(INTERIOR_MESH_SMOOTHING_ITERATIONS_LABEL, layersControlsModel.bindIntegerPositive(N_SMOOTH_NORMALS_KEY), INTERIOR_MESH_SMOOTHING_ITERATIONS_TOOLTIP);
        builder.addComponent(SMOOTH_LAYER_THICKNESS_LABEL, layersControlsModel.bindIntegerPositive(N_SMOOTH_THICKNESS_KEY), SMOOTH_LAYER_THICKNESS_TOOLTIP);
        builder.addComponent(MAX_FACE_THICKNESS_RATIO_LABEL, layersControlsModel.bindDouble(MAX_FACE_THICKNESS_RATIO_KEY), MAX_FACE_THICKNESS_RATIO_TOOLTIP);
        builder.addComponent(MAX_THICKNESS_TO_MEDIAL_RATIO_LABEL, layersControlsModel.bindDouble(MAX_THICKNESS_TO_MEDIAL_RATIO_KEY), MAX_THICKNESS_TO_MEDIAL_RATIO_TOOLTIP);
        builder.addComponent(MIN_MEDIAL_AXIS_ANGLE_LABEL, layersControlsModel.bindIntegerAngle_360(MIN_MEDIAL_AXIS_ANGLE_KEY), MIN_MEDIAL_AXIS_ANGLE_TOOLTIP);
        builder.addComponent(NUMBER_OF_BUFFER_CELLS_LABEL, layersControlsModel.bindIntegerPositive(N_BUFFER_CELLS_NO_EXTRUDE_KEY), NUMBER_OF_BUFFER_CELLS_TOOLTIP);
        builder.addComponent(LAYER_ADDITION_ITERATIONS_LABEL, layersControlsModel.bindIntegerPositive(N_LAYER_ITER_KEY), LAYER_ADDITION_ITERATIONS_TOOLTIP);
        builder.addComponent(NUMBER_OF_LAYERS_NOT_GROWN_LABEL, layersControlsModel.bindIntegerPositive(N_GROW_KEY), NUMBER_OF_LAYERS_NOT_GROWN_TOOLTIP);
        return builder.getPanel();
    }

    @Override
    protected JPanel createSnappingPanel() {
        PanelBuilder builder = new PanelBuilder("options.snapping.panel");
        builder.addComponent(SOLVER_ITERATIONS_LABEL, snapControlsModel.bindIntegerPositive(N_SOLVER_ITER_KEY), SOLVER_ITERATIONS_TOOLTIP);
        builder.addComponent(SMOOTH_PATCH_LABEL, snapControlsModel.bindIntegerPositive(N_SMOOTH_PATCH_KEY), SMOOTH_PATCH_TOOLTIP);
        builder.addComponent(TOLERANCE_LABEL, snapControlsModel.bindDoublePositive(TOLERANCE_KEY), TOLERANCE_TOOLTIP);
        builder.addComponent(RELAX_ITERATIONS_SNAP_LABEL, snapControlsModel.bindIntegerPositive(N_RELAX_ITER_SNAP_KEY), RELAX_ITERATIONS_SNAP_TOOLTIP);
        builder.addComponent(SNAP_FEATURE_ITERATIONS_LABEL, snapControlsModel.bindIntegerPositive(N_FEATURE_SNAP_ITER_KEY), SNAP_FEATURE_ITERATIONS_TOOLTIP);
        builder.addComponent(IMPLICIT_SNAP_FEATURE_LABEL, snapControlsModel.bindBoolean(IMPLICIT_FEATURE_SNAP_KEY), IMPLICIT_SNAP_FEATURE_TOOLTIP);
        builder.addComponent(EXPLICIT_SNAP_FEATURE_LABEL, snapControlsModel.bindBoolean(EXPLICIT_FEATURE_SNAP_KEY), EXPLICIT_SNAP_FEATURE_TOOLTIP);
        builder.addComponent(MULTI_REGION_FEATURE_LABEL, snapControlsModel.bindBoolean(MULTI_REGION_FEATURE_SNAP_KEY), MULTI_REGION_FEATURE_TOOLTIP);
        return builder.getPanel();
    }

    @Override
    public void load() {
        SnappyHexMeshDict snappyDict = getSnappyDict();
        if (snappyDict != null) {
            loadCastellated(snappyDict);
            loadLayers(snappyDict);
            loadQuality(snappyDict);
            loadSnap(snappyDict);
            this.snappyHexMeshModel.setDictionary(new SnappyHexMeshDict(snappyDict));
        }
    }

    @Override
    protected void loadCastellated(SnappyHexMeshDict snappyDict) {
        if (snappyDict.found(CASTELLATED_MESH_CONTROLS_KEY)) {
            Dictionary castellated = new Dictionary(snappyDict.subDict(CASTELLATED_MESH_CONTROLS_KEY));
            snappyDict.remove(CASTELLATED_MESH_CONTROLS_KEY);
            this.castellatedMeshControlsModel.setDictionary(castellated);
        }
    }

}

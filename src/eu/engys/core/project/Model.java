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
package eu.engys.core.project;

import java.util.Observable;

import javax.inject.Inject;

import eu.engys.core.modules.materials.MaterialsDatabase;
import eu.engys.core.project.custom.Custom;
import eu.engys.core.project.custom.CustomFile;
import eu.engys.core.project.defaults.Defaults;
import eu.engys.core.project.geometry.Geometry;
import eu.engys.core.project.geometry.factory.GeometryFactory;
import eu.engys.core.project.materials.Materials;
import eu.engys.core.project.mesh.Mesh;
import eu.engys.core.project.runtimefields.RuntimeFields;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.fieldmanipulationfunctionobjects.FieldManipulationFunctionObjects;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObject;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObjects;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.facezones.FaceZones;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patches;

public class Model extends Observable {

    private State state;
    private openFOAMProject project;
    private Geometry geometry;
    private Defaults defaults;
    private MaterialsDatabase materialsDatabase;

    private Fields fields;
    private RuntimeFields runtimeFields;
    private Patches patches;
    private CellZones cellZones;
    private FaceZones faceZones;
    private FieldManipulationFunctionObjects fieldManipulationFunctionObjects;
    private MonitoringFunctionObjects monitoringFunctionObjects;
    private Mesh mesh;

    private Materials materials;
    private TurbulenceModels turbulenceModels;
    private SolverModel solverModel;
    private Custom custom;

    private GeometryFactory geometryFactory;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public openFOAMProject getProject() {
        return project;
    }

    public void setProject(openFOAMProject project) {
        this.project = project;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public RuntimeFields getRuntimeFields() {
        return runtimeFields;
    }

    public void setRuntimeFields(RuntimeFields runtimeFields) {
        this.runtimeFields = runtimeFields;
    }

    public Patches getPatches() {
        return patches;
    }

    public void setPatches(Patches patches) {
        this.patches = patches;
    }

    public CellZones getCellZones() {
        return cellZones;
    }

    public void setCellZones(CellZones cellZones) {
        this.cellZones = cellZones;
    }

    public FaceZones getFaceZones() {
        return faceZones;
    }

    public void setFaceZones(FaceZones faceZones) {
        this.faceZones = faceZones;
    }

    public Materials getMaterials() {
        return materials;
    }

    public void setMaterials(Materials materials) {
        this.materials = materials;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Inject
    public void setGeometryFactory(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public Defaults getDefaults() {
        return defaults;
    }

    @Inject
    public void setDefaults(Defaults defaults) {
        this.defaults = defaults;
    }

    public MaterialsDatabase getMaterialsDatabase() {
        return materialsDatabase;
    }

    @Inject
    public void setMaterialsDatabase(MaterialsDatabase materialsDatabase) {
        this.materialsDatabase = materialsDatabase;
    }

    public FieldManipulationFunctionObjects getFieldManipulationFunctionObjects() {
        return fieldManipulationFunctionObjects;
    }

    public void setFieldManipulationFunctionObjects(FieldManipulationFunctionObjects fieldManipulationFunctionObjects) {
        this.fieldManipulationFunctionObjects = fieldManipulationFunctionObjects;
    }

    public MonitoringFunctionObjects getMonitoringFunctionObjects() {
        return monitoringFunctionObjects;
    }

    public void setMonitoringFunctionObjects(MonitoringFunctionObjects monitoringFunctionObjects) {
        this.monitoringFunctionObjects = monitoringFunctionObjects;
    }

    public TurbulenceModels getTurbulenceModels() {
        return turbulenceModels;
    }

    @Inject
    public void setTurbulenceModels(TurbulenceModels turbulenceModels) {
        this.turbulenceModels = turbulenceModels;
    }

    public SolverModel getSolverModel() {
        return solverModel;
    }

    public void setSolverModel(SolverModel solverModel) {
        this.solverModel = solverModel;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public void init() {
        setState(new State());
        setGeometry(new Geometry(geometryFactory));
        setMesh(new Mesh());
        setCellZones(new CellZones());
        setFaceZones(new FaceZones());
        setFields(new Fields());
        setRuntimeFields(new RuntimeFields());
        setPatches(new Patches());
        setFieldManipulationFunctionObjects(new FieldManipulationFunctionObjects());
        setMonitoringFunctionObjects(new MonitoringFunctionObjects());
        setMaterials(new Materials());
        setSolverModel(new SolverModel());
        setCustom(new Custom());

        geometryChanged();
        materialsChanged();
        patchesChanged();
        cellZonesChanged();
        faceZonesChanged();
        fieldManipulationFunctionObjectsChanged();
        monitoringFunctionObjectsChanged();
        customChanged();
    }

    public boolean hasProject() {
        return project != null;
    }

    public void stateChanged() {
        setChanged();
        notifyObservers(state);
    }


    public void fieldsChanged() {
        setChanged();
        notifyObservers(fields);
    }

    public void runtimeFieldsChanged() {
        setChanged();
        notifyObservers(runtimeFields);
    }

    public void materialsChanged() {
        setChanged();
        notifyObservers(materials);
    }
    
    public void patchesChanged() {
        setChanged();
        notifyObservers(patches);
    }


    public void boundaryTypeChanged(BoundaryType boundaryType) {
        setChanged();
        notifyObservers(boundaryType);
    }

    public void cellZonesChanged() {
        setChanged();
        notifyObservers(cellZones);
    }

    public void faceZonesChanged() {
        setChanged();
        notifyObservers(faceZones);
    }

    public void monitoringFunctionObjectsChanged() {
        setChanged();
        notifyObservers(monitoringFunctionObjects);
    }
    
    public void monitoringFunctionObjectChanged(MonitoringFunctionObject fo) {
        setChanged();
        notifyObservers(fo);
    }

    public void fieldManipulationFunctionObjectsChanged() {
        setChanged();
        notifyObservers(fieldManipulationFunctionObjects);
    }

    public void projectChanged() {
        setChanged();
        notifyObservers(project);
    }

    public void solverChanged() {
        setChanged();
        notifyObservers(state.getSolver());
    }

    public void geometryChanged() {
        setChanged();
        notifyObservers(geometry);
    }

    public void geometryChanged(Object obj) {
        setChanged();
        notifyObservers(obj);
    }

    public void blockChanged() {
        setChanged();
        notifyObservers(geometry.getBlock());
    }

    // public void solverChanged() {
    // setChanged();
    // notifyObservers(this);
    // }

    public void customFileChanged(CustomFile file) {
        setChanged();
        notifyObservers(file);
    }

    public void customChanged() {
        setChanged();
        notifyObservers(custom);
    }

    @Deprecated
    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Deprecated
    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }

}

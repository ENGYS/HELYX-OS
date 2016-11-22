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
package eu.engys.vtk.actors;

import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.PrefUtil;
import eu.engys.vtk.VTKColors;
import eu.engys.vtk.VTKUtil;
import vtk.vtkActor;
import vtk.vtkDataSetSurfaceFilter;
import vtk.vtkFeatureEdges;
import vtk.vtkLookupTable;
import vtk.vtkMapper;
import vtk.vtkOutlineFilter;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProperty;
import vtk.vtkQuadricClustering;
import vtk.vtkTransform;
import vtk.vtkUnstructuredGrid;

public abstract class DefaultActor extends vtkActor implements Actor {

    private enum SelectionState {
        BASE, SELECTED, DESELECTED
    };

    private static final double BASE_OPACITY = 1.0;
    private static final double DESELECTION_OPACITY = 0.2;
    private static final double SELECTION_OPACITY = 0.8;

    private String name;
    
    private vtkPolyDataMapper mapper;
    private vtkPolyDataMapper LODMapper;
    private vtkPolyDataMapper outlineMapper;
    private vtkPolyDataMapper edgesMapper;
    
    private vtkTransform transform;

    private boolean outline;
    private boolean profile;
    private boolean scalar;
    private SelectionState selectionState = SelectionState.BASE;
    private boolean useDeselectedState = true;

    private vtkProperty DESELECTION_PROPERTY;
    private vtkProperty SELECTION_PROPERTY;
    private vtkProperty BASE_PROPERTY;

    private int memorySize;
    private boolean useLOD;
    private vtkActor selectionActor;

    public DefaultActor(String name) {
        this.name = name;
        this.transform = new vtkTransform();
        this.selectionActor = new vtkActor();

        setUpBaseProperty();
        setUpSelectionProperty();
        setUpDeselectionProperty();
    }

    private void setUpBaseProperty() {
        BASE_PROPERTY = new vtkProperty();
        BASE_PROPERTY.SetColor(VTKColors.WHITE);
        BASE_PROPERTY.SetOpacity(BASE_OPACITY);
    }

    private void applyCommonProperty(vtkActor actor) {
        actor.GetProperty().SetRepresentationToSurface();
        actor.GetProperty().EdgeVisibilityOff();
        actor.GetProperty().SetEdgeColor(VTKColors.BLACK);
        actor.GetProperty().SetLineWidth(1);

        actor.GetProperty().LightingOn();
        actor.GetProperty().SetAmbient(VTKColors.AMBIENT);
        actor.GetProperty().SetDiffuse(VTKColors.DIFFUSE);
        actor.GetProperty().SetSpecular(VTKColors.SPECULAR);
        actor.GetProperty().SetSpecularPower(VTKColors.SPECULAR_POWER);
    }

    private void applyBaseProperty(vtkActor actor) {
        if (selectionState != SelectionState.BASE) {
            actor.GetProperty().SetColor(BASE_PROPERTY.GetColor());
            actor.GetProperty().SetOpacity(BASE_PROPERTY.GetOpacity());
        }
    }

    private void setUpSelectionProperty() {
        SELECTION_PROPERTY = new vtkProperty();
        SELECTION_PROPERTY.SetColor(VTKColors.SELECTION_COLOR);
        SELECTION_PROPERTY.SetOpacity(SELECTION_OPACITY);
    }

    private void applySelectionProperty(vtkActor actor) {
        if (selectionState != SelectionState.SELECTED) {
            actor.GetProperty().SetColor(DESELECTION_PROPERTY.GetColor());
            actor.GetProperty().SetOpacity(DESELECTION_PROPERTY.GetOpacity());
        }
    }

    private void setUpDeselectionProperty() {
        DESELECTION_PROPERTY = new vtkProperty();
        DESELECTION_PROPERTY.SetColor(VTKColors.DESELECTION_COLOR);
        DESELECTION_PROPERTY.SetOpacity(DESELECTION_OPACITY);
    }

    private void applyDeselectionProperty(vtkActor actor) {
        actor.GetProperty().SetColor(DESELECTION_PROPERTY.GetColor());
        actor.GetProperty().SetOpacity(DESELECTION_PROPERTY.GetOpacity());
    }

    protected void newActor(vtkPolyData dataset, boolean visible) {
        vtkPolyData input = new vtkPolyData();
        input.ShallowCopy(dataset);

        this.mapper = new vtkPolyDataMapper();
        this.mapper.ScalarVisibilityOff();
        this.mapper.ImmediateModeRenderingOff();
        // this.mapper.StaticOn();
        // VTKUtil.observe(mapper, getName());
        
        this.LODMapper = new vtkPolyDataMapper();
        this.LODMapper.ScalarVisibilityOff();
        this.LODMapper.ImmediateModeRenderingOff();
        //this.LODMapper.StaticOn();

        SetMapper(mapper);
        selectionActor.SetMapper(mapper);

        this.outlineMapper = new vtkPolyDataMapper();
        this.outlineMapper.ScalarVisibilityOff();
        this.outlineMapper.ImmediateModeRenderingOff();

        this.edgesMapper = new vtkPolyDataMapper();
        this.edgesMapper.ScalarVisibilityOff();
        this.edgesMapper.ImmediateModeRenderingOff();

        this.outline = false;
        this.profile = false;
        this.scalar = false;

        setInput(input);

        input.Delete();

        SetVisibility(visible ? 1 : 0);

        applyCommonProperty(this);
        applyCommonProperty(selectionActor);
        applyBaseProperty(this);
        selectionActor.GetProperty().SetColor(SELECTION_PROPERTY.GetColor());
        selectionActor.GetProperty().SetOpacity(SELECTION_PROPERTY.GetOpacity());
    }

    protected void newActor(vtkUnstructuredGrid dataset, boolean visible) {
        vtkUnstructuredGrid input = new vtkUnstructuredGrid();
        input.ShallowCopy(dataset);

        vtkDataSetSurfaceFilter filter = new vtkDataSetSurfaceFilter();
        filter.SetInputData(input);
        filter.PassThroughCellIdsOn();
        filter.PassThroughPointIdsOn();
        filter.Update();
        input.Delete();

        vtkPolyData output = filter.GetOutput();
        newActor(output, visible);
        output.Delete();
        filter.Delete();
    }

    vtkQuadricClustering createLOD() {
        int dim = 30;
        vtkQuadricClustering LODFilter = new vtkQuadricClustering();
        LODFilter.UseInputPointsOn();
        LODFilter.CopyCellDataOn();
        LODFilter.UseInternalTrianglesOff();
        LODFilter.SetNumberOfDivisions(dim, dim, dim);
        LODFilter.AutoAdjustNumberOfDivisionsOff();
        
        return LODFilter;
    }

    vtkOutlineFilter createOutline() {
        return new vtkOutlineFilter();
    }
    
    vtkFeatureEdges createEdges() {
        vtkFeatureEdges edges = new vtkFeatureEdges();
        edges.SetBoundaryEdges(1);
        edges.SetFeatureEdges(1);
        edges.SetNonManifoldEdges(0);
        edges.SetManifoldEdges(0);
        edges.SetFeatureAngle(30D);
        edges.ColoringOff();
        
        return edges;

    }
    @Override
    public void setInput(vtkPolyData input) {
        // System.out.println("DefaultActor.setInput() size: " + input.GetActualMemorySize() + " kB");
        SetMapper(null);
        
        mapper.RemoveAllInputs();
        mapper.SetInputData(input);

        int memory_limit = PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_MEMORY, 512);
        this.memorySize = input.GetActualMemorySize();
        this.useLOD = memorySize > memory_limit;
        
        if (LODMapper != null) {
            vtkQuadricClustering LODFilter = createLOD();
            LODFilter.SetInputData(input);
            LODFilter.Update();

            vtkPolyData output = LODFilter.GetOutput();
            LODMapper.RemoveAllInputs();
            LODMapper.SetInputData(output);
            LODMapper.Update();
            output.Delete();
            LODFilter.Delete();
        }

        if (outlineMapper != null) {
            vtkOutlineFilter outlineFilter = createOutline();
            outlineFilter.RemoveAllInputs();
            outlineFilter.SetInputData(input);
            outlineFilter.Update();

            vtkPolyData output = outlineFilter.GetOutput();
            outlineMapper.RemoveAllInputs();
            outlineMapper.SetInputData(output);
            outlineMapper.Update();
            
            output.Delete();
            outlineFilter.Delete();
        }

        if (edgesMapper != null) {
            vtkFeatureEdges edges = createEdges();
            edges.SetInputData(input);
            edges.Update();
            vtkPolyData output = edges.GetOutput();

            edgesMapper.RemoveAllInputs();
            edgesMapper.SetInputData(output);
            edgesMapper.Update();
            
            output.Delete();
            edges.Delete();
        }
        
        SetMapper(mapper);
    }

    @Override
    public void setInput(vtkUnstructuredGrid input) {
        vtkPolyData filter = VTKUtil.geometryFilter(input);
        setInput(filter);
        filter.Delete();
    }

    @Override
    public void interactiveOn() {
        if (!outline && !profile) {
            if (useLOD) {
                SetMapper(LODMapper);
                selectionActor.SetMapper(LODMapper);
            }
        }
    }

    @Override
    public void interactiveOff() {
        if (!outline && !profile) {
            if (useLOD) {
                SetMapper(mapper);
                selectionActor.SetMapper(mapper);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void rename(String name) {
        this.name = name;
    }

    @Override
    public vtkMapper getMapper() {
        return GetMapper();
    }

    @Override
    public boolean getVisibility() {
        return GetVisibility() == 1;
    }

    @Override
    public void setVisibility(boolean onoff) {
        SetVisibility(onoff ? 1 : 0);
        selectionActor.SetVisibility(GetVisibility());
    }

    @Override
    public double[] getBounds() {
        return GetBounds();
    }

    @Override
    public vtkActor getActor() {
        return this;
    }

    @Override
    public vtkActor getSelectionActor() {
        return selectionActor;
    }

    @Override
    public void deleteActor() {
//        if (LODFilter != null) {
//            LODFilter.RemoveAllInputs();
//            LODFilter.Delete();
//        }
        if (LODMapper != null) {
            LODMapper.RemoveAllInputs();
            LODMapper.Delete();
        }
        
        if (edgesMapper != null) {
            edgesMapper.RemoveAllInputs();
            edgesMapper.Delete();
        }
//        if (edges != null) {
//            edges.RemoveAllInputs();
//            edges.Delete();
//        }

        if (outlineMapper != null) {
            outlineMapper.RemoveAllInputs();
            outlineMapper.Delete();
        }
        
        if (mapper != null) {
//            vtkDataSet dataset = mapper.GetInputAsDataSet();
//            VTKUtil.deleteDataset(dataset);
            mapper.RemoveAllInputs();
            mapper.Delete();
        }

        SELECTION_PROPERTY.Delete();
        DESELECTION_PROPERTY.Delete();
        BASE_PROPERTY.Delete();
        
        selectionActor.SetMapper(null);
        selectionActor.Delete();

        transform.Delete();
        
        SetMapper(null);
        Delete();
    }

    @Override
    public void transformActor(boolean save, AffineTransform t) {
        vtkTransform transform = t.toVTK(this.transform);

        SetUserTransform(transform);
        selectionActor.SetUserTransform(transform);

        if (save) {
            this.transform = transform;
        }
    }

    @Override
    public vtkTransform getUserTransform() {
        return (vtkTransform) GetUserTransform();
    }

    @Override
    public void setSolidColor(double[] color, double opacity) {
        this.scalar = false;
        setColor(color, opacity);

        scalarVisibilityOff();
    }

    @Override
    public void setScalarColors(vtkLookupTable lut, FieldItem field) {
        this.scalar = true;
        setColor(VTKColors.WHITE, BASE_OPACITY);
        
        if (mapper != null) {
            setScalarColor(mapper, lut, field);
        }
        if (LODMapper != null) {
            setScalarColor(LODMapper, lut, field);
        }
        if (edgesMapper != null) {
            setScalarColor(edgesMapper, lut, field);
        }
//        if (outlineMapper != null) {
//            setScalarColor(outlineMapper, lut, field);
//        }
    }
    
    protected void setLineWidth(int width) {
        GetProperty().SetLineWidth(width);
    }

    protected void setColor(double[] color, double opacity) {
        GetProperty().SetColor(color);
        GetProperty().SetOpacity(opacity);

        BASE_PROPERTY.SetColor(color);
        BASE_PROPERTY.SetOpacity(opacity);
    }

    private static void setScalarColor(vtkMapper mapper, vtkLookupTable lut, FieldItem field) {
        mapper.SetLookupTable(lut);
        mapper.UseLookupTableScalarRangeOn();
        mapper.ScalarVisibilityOn();
        mapper.SetColorModeToMapScalars();

        if (field.getDataType().isCell()) {
            mapper.SetScalarModeToUseCellFieldData();
        } else if (field.getDataType().isPoint()) {
            mapper.SetScalarModeToUsePointFieldData();
        }

        mapper.SetScalarRange(lut.GetRange());
        mapper.SelectColorArray(field.getFieldName());
        mapper.InterpolateScalarsBeforeMappingOn();
        mapper.Update();
    }

    @Override
    public void setRepresentation(Representation representation) {
        switch (representation) {
        case SURFACE:
            GetProperty().SetRepresentationToSurface();
            GetProperty().EdgeVisibilityOff();
            setOutLine(false);
            setProfile(false);
            break;
        case WIREFRAME:
            GetProperty().SetRepresentationToWireframe();
            GetProperty().EdgeVisibilityOff();
            setOutLine(false);
            setProfile(false);
            break;
        case SURFACE_WITH_EDGES:
            GetProperty().SetRepresentationToSurface();
            GetProperty().EdgeVisibilityOn();
            setOutLine(false);
            setProfile(false);
            break;
        case OUTLINE:
            GetProperty().EdgeVisibilityOff();
            setProfile(false);
            setOutLine(true);
            break;
        case PROFILE:
            GetProperty().EdgeVisibilityOff();
            setOutLine(false);
            setProfile(true);
            break;

        default:
            break;
        }
    }

    private void setOutLine(boolean outline) {
        if (outline) {
            if (!this.outline) {
                outlineActor();
            }
        } else {
            if (this.outline) {
                deoutlineActor();
            }
        }
        this.outline = outline;
    }

    private void setProfile(boolean profile) {
        if (profile) {
            if (!this.profile) {
                profileActor();
            }
        } else {
            if (this.profile) {
                deprofileActor();
            }
        }
        this.profile = profile;
    }

    private void outlineActor() {
        SetMapper(outlineMapper);
    }

    private void deoutlineActor() {
        SetMapper(mapper);
    }

    private void profileActor() {
        SetMapper(edgesMapper);
    }

    private void deprofileActor() {
        SetMapper(mapper);
    }

    @Override
    public void restoreFromSelection() {
        if (this.selectionState != SelectionState.BASE) {
            applyBaseProperty(this);
            if (scalar) {
                scalarVisibilityOn();
            } else {
                scalarVisibilityOff();
            }
        }
        this.selectionState = SelectionState.BASE;
    }

    @Override
    public void selectActor() {
        if (this.selectionState != SelectionState.SELECTED) {
            applySelectionProperty(this);
            scalarVisibilityOff();
        }
        this.selectionState = SelectionState.SELECTED;
    }

    @Override
    public void deselectActor() {
        if (useDeselectedState) {
            if (this.selectionState != SelectionState.DESELECTED) {
                applyDeselectionProperty(this);
                scalarVisibilityOff();
            }
            this.selectionState = SelectionState.DESELECTED;
        } else {
            restoreFromSelection();
        }
    }

    private void scalarVisibilityOff() {
        if (mapper != null) {
            mapper.ScalarVisibilityOff();
        }
        if (LODMapper != null) {
            LODMapper.ScalarVisibilityOff();
        }
        if (edgesMapper != null) {
            edgesMapper.ScalarVisibilityOff();
        }
//        if (outlineMapper != null) {
//            outlineMapper.ScalarVisibilityOff();
//        }
    }

    private void scalarVisibilityOn() {
        if (mapper != null) {
            mapper.ScalarVisibilityOn();
        }
        if (LODMapper != null) {
            LODMapper.ScalarVisibilityOn();
        }
        if (edgesMapper != null) {
            edgesMapper.ScalarVisibilityOn();
        }
//        if (outlineMapper != null) {
//            outlineMapper.ScalarVisibilityOn();
//        }
    }

    @Override
    public void deselectedStateOn() {
        this.useDeselectedState = true;
    }

    @Override
    public void deselectedStateOff() {
        this.useDeselectedState = false;
    }

    @Override
    public int getMemorySize() {
        return memorySize;
    }

    @Override
    public void filterActor() {
        setVisibility(false);
    }

    @Override
    public void unfilterActor() {
        setVisibility(true);
    }
}

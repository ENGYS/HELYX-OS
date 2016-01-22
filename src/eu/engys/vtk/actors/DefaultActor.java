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

package eu.engys.vtk.actors;

import vtk.vtkActor;
import vtk.vtkDataSet;
import vtk.vtkDataSetSurfaceFilter;
import vtk.vtkFeatureEdges;
import vtk.vtkLookupTable;
import vtk.vtkMapper;
import vtk.vtkOutlineFilter;
import vtk.vtkPolyData;
import vtk.vtkPolyDataAlgorithm;
import vtk.vtkPolyDataMapper;
import vtk.vtkProperty;
import vtk.vtkQuadricClustering;
import vtk.vtkTransform;
import vtk.vtkUnstructuredGrid;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.mesh.FieldItem;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.PrefUtil;
import eu.engys.vtk.VTKColors;
import eu.engys.vtk.VTKUtil;

public abstract class DefaultActor extends vtkActor implements Actor {

    private enum SelectionState {BASE, SELECTED, DESELECTED};
    
    private static final double BASE_OPACITY = 1.0;
    private static final double DESELECTION_OPACITY = 0.2;
    private static final double SELECTION_OPACITY = 0.6;
    
    private vtkPolyDataMapper mapper;
    private vtkPolyDataMapper LODMapper;
    private String name;
    private vtkQuadricClustering LODFilter;
    private vtkTransform transform;
    private vtkPolyDataMapper outlineMapper;
    private vtkPolyDataAlgorithm outlineFilter;
    private vtkFeatureEdges edges;
    private vtkPolyDataMapper edgesMapper;
    
    private boolean outline;
    private boolean profile;
    private boolean scalar;
    private SelectionState selectionState = SelectionState.BASE;
    private boolean useDeselectedState = true;
    
    private vtkProperty DESELECTION_PROPERTY;
    private vtkProperty SELECTION_PROPERTY;
    private vtkProperty BASE_PROPERTY;
    
    private int memorySize;
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
            actor.GetProperty().SetColor(BASE_PROPERTY.GetColor());
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
        this.mapper.ImmediateModeRenderingOff();
//        this.mapper.StaticOn();
//        VTKUtil.observe(mapper, getName());
        
        SetMapper(mapper);
        selectionActor.SetMapper(mapper);
        
        this.outlineMapper = new vtkPolyDataMapper();
        this.outlineFilter = new vtkOutlineFilter();
        
        this.edgesMapper = new vtkPolyDataMapper();
        this.edges = new vtkFeatureEdges();
        this.edges.SetBoundaryEdges(1);
        this.edges.SetFeatureEdges(1);
        this.edges.SetNonManifoldEdges(0);
        this.edges.SetManifoldEdges(0);
        this.edges.SetFeatureAngle(30D);
        this.edges.ColoringOff();
        
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

        newActor(filter.GetOutput(), visible);
        filter.Delete();
    }

    void createLOD()
    {
        this.LODMapper.ImmediateModeRenderingOff();
        this.LODMapper.StaticOn();
//        VTKUtil.observe(LODMapper, "LOD_" + getName());

        int dim = 30;

        this.LODFilter.UseInputPointsOn();
        this.LODFilter.CopyCellDataOn();
        this.LODFilter.UseInternalTrianglesOff();
        this.LODFilter.SetNumberOfDivisions(dim, dim, dim);
        this.LODFilter.AutoAdjustNumberOfDivisionsOff();
    }
    
    @Override
    public void setInput(vtkPolyData input) {
//        System.out.println("DefaultActor.setInput() size: " + input.GetActualMemorySize() + " kB");
        mapper.RemoveAllInputs();
        mapper.SetInputData(input);
        
        int memory_limit = PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_MEMORY, 512);
        this.memorySize = input.GetActualMemorySize(); 
        if (memorySize > memory_limit) {
            if (LODMapper == null) {
                this.LODMapper = new vtkPolyDataMapper();
                this.LODFilter = new vtkQuadricClustering();
                
                createLOD();
            }
        } else {
            this.LODMapper = null;
            this.LODFilter = null;
        }
        
        if (LODMapper != null) {
            LODFilter.RemoveAllInputs();
            LODFilter.SetInputData(input);
            LODFilter.Update();
            
            LODMapper.RemoveAllInputs();
            LODMapper.SetInputData(LODFilter.GetOutput());
            LODMapper.Update();
        }
        
        if (outlineFilter != null) {
            outlineFilter.RemoveAllInputs();
            outlineFilter.SetInputData(input);
            outlineFilter.Update();
            
            outlineMapper.RemoveAllInputs();
            outlineMapper.SetInputData(outlineFilter.GetOutput());
            outlineMapper.Update();
        }

        if (edges != null) {
            edges.RemoveAllInputs();
            edges.SetInputData(input);
            edges.Update();
            
            edgesMapper.RemoveAllInputs();
            edgesMapper.SetInputData(edges.GetOutput());
            edgesMapper.Update();
        }
    }
    
    @Override
    public void setInput(vtkUnstructuredGrid input) {
        vtkPolyData filter = VTKUtil.geometryFilter(input);
        setInput(filter);
    }
    
    @Override
    public void interactiveOn() {
        if (!outline && !profile) {
            if (LODMapper != null) {
                SetMapper(LODMapper);
                selectionActor.SetMapper(LODMapper);
            }
        } 
    }

    @Override
    public void interactiveOff() {
        if (!outline && !profile) {
            if (LODMapper != null) {
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
        if (LODFilter != null) {
            LODMapper.RemoveAllInputs();
            LODMapper.Delete();
            LODFilter.RemoveAllInputs();
            LODFilter.Delete();
        }
        vtkDataSet dataset = mapper.GetInputAsDataSet();

        VTKUtil.deleteDataset(dataset);

        mapper.RemoveAllInputs();
        mapper.Delete();
        
        selectionActor.SetMapper(null);
        selectionActor.Delete();

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
    }
    
    protected void setLineWidth(int width) {
        GetProperty().SetLineWidth(width);

//        BASE_PROPERTY.SetLineWidth(width);
//        SELECTION_PROPERTY.SetLineWidth(width);
//        DESELECTION_PROPERTY.SetLineWidth(width);
    }
    
    protected void setColor(double[] color, double opacity) {
        GetProperty().SetColor(color);
        GetProperty().SetOpacity(opacity);
        
        BASE_PROPERTY.SetColor(color);
        BASE_PROPERTY.SetOpacity(opacity);
        
//        SELECTION_PROPERTY.SetColor(color);
//        DESELECTION_PROPERTY.SetColor(color);
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
        mapper.SelectColorArray(field.getName());
        mapper.Update();
    }
    
    @Override
    public void setRepresentation(Representation representation) {
        switch (representation) {
            case SURFACE:
                GetProperty().SetRepresentationToSurface();
                GetProperty().EdgeVisibilityOff();
                
//                BASE_PROPERTY.SetRepresentationToSurface();
//                BASE_PROPERTY.EdgeVisibilityOff();
                setOutLine(false);
                setProfile(false);
                break;
            case WIREFRAME:
                GetProperty().SetRepresentationToWireframe();
                GetProperty().EdgeVisibilityOff();
                
//                BASE_PROPERTY.SetRepresentationToWireframe();
//                BASE_PROPERTY.EdgeVisibilityOff();
                setOutLine(false);
                setProfile(false);
                break;
            case SURFACE_WITH_EDGES:
                GetProperty().SetRepresentationToSurface();
                GetProperty().EdgeVisibilityOn();
                
//                BASE_PROPERTY.SetRepresentationToSurface();
//                BASE_PROPERTY.EdgeVisibilityOn();
                setOutLine(false);
                setProfile(false);
                break;
            case OUTLINE:
//                GetProperty().SetRepresentationToSurface();
//                GetProperty().EdgeVisibilityOff();
//                
//                BASE_PROPERTY.SetRepresentationToSurface();
//                BASE_PROPERTY.EdgeVisibilityOff();
                setProfile(false);
                setOutLine(true);
                break;
                
            case PROFILE:
//                GetProperty().SetRepresentationToSurface();
//                GetProperty().EdgeVisibilityOff();
//                
//                BASE_PROPERTY.SetRepresentationToSurface();
//                BASE_PROPERTY.EdgeVisibilityOff();
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
    }

    private void scalarVisibilityOn() {
        if (mapper != null) {
            mapper.ScalarVisibilityOn();
        }
        
        if (LODMapper != null) {
            LODMapper.ScalarVisibilityOn();
        }
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
}

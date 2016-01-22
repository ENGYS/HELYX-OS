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

//package eu.engys.vtk.actors;
//
//import org.apache.commons.lang.ArrayUtils;
//
//import vtk.vtkActor;
//import vtk.vtkDataSetSurfaceFilter;
//import vtk.vtkHELYXActor;
//import vtk.vtkLookupTable;
//import vtk.vtkMapper;
//import vtk.vtkPolyData;
//import vtk.vtkPolyDataMapper;
//import vtk.vtkProperty;
//import vtk.vtkTransform;
//import vtk.vtkUnstructuredGrid;
//import eu.engys.core.project.geometry.stl.AffineTransform;
//import eu.engys.core.project.mesh.FieldItem;
//import eu.engys.gui.view3D.Actor;
//import eu.engys.gui.view3D.Representation;
//import eu.engys.util.PrefUtil;
//import eu.engys.vtk.VTKColors;
//import eu.engys.vtk.VTKUtil;

//public abstract class HelyxActor extends vtkHELYXActor implements Actor {
//
//    private static final double BASE_OPACITY = 1.0;
//    private static final double DESELECTION_OPACITY = BASE_OPACITY;//0.2;
//    private static final double SELECTION_OPACITY = BASE_OPACITY;//0.95;
//    
//    private String name;
//    private vtkTransform transform;
//    
//    private boolean outline;
//    private boolean profile;
//    private boolean scalar;
//    
//    private vtkProperty DESELECTION_PROPERTY;
//    private vtkProperty SELECTION_PROPERTY;
//    private vtkProperty baseProperty;
//    private vtkPolyDataMapper mapper;
//    
//    public HelyxActor(String name) {
//        this.name = name;
//        this.transform = new vtkTransform();
//
//        setUpBaseProperty();
//        setUpSelectionProperty();
//        setUpDeselectionProperty();
//    }
//
//    private void setUpBaseProperty() {
//        baseProperty = new vtkProperty();
//        baseProperty.SetRepresentationToSurface();
//        baseProperty.EdgeVisibilityOff();
//        baseProperty.SetEdgeColor(VTKColors.BLACK);
//        baseProperty.SetLineWidth(1);
//
////        baseProperty.SetColor(WHITE);
//        baseProperty.LightingOn();
//        baseProperty.SetOpacity(BASE_OPACITY);
//        
//        baseProperty.SetAmbient(VTKColors.AMBIENT);
//        baseProperty.SetDiffuse(VTKColors.DIFFUSE);
//        baseProperty.SetSpecular(VTKColors.SPECULAR);
//        baseProperty.SetSpecularPower(VTKColors.SPECULAR_POWER);
//    }
//
//    private void applyBaseProperty() {
//        if (!ArrayUtils.isEquals(GetProperty().GetColor(), VTKColors.WHITE)) {
//            GetProperty().SetRepresentationToSurface();
//            GetProperty().EdgeVisibilityOff();
//            GetProperty().SetEdgeColor(VTKColors.BLACK);
//            GetProperty().SetLineWidth(1);
//            
//            GetProperty().SetColor(VTKColors.WHITE);
//            GetProperty().LightingOn();
//            GetProperty().SetOpacity(BASE_OPACITY);
//            
//            GetProperty().SetAmbient(VTKColors.AMBIENT);
//            GetProperty().SetDiffuse(VTKColors.DIFFUSE);
//            GetProperty().SetSpecular(VTKColors.SPECULAR);
//            GetProperty().SetSpecularPower(VTKColors.SPECULAR_POWER);
//        }
//    }
//
//    private void setUpSelectionProperty() {
//        SELECTION_PROPERTY = new vtkProperty();
//        SELECTION_PROPERTY.SetRepresentationToSurface();
//        SELECTION_PROPERTY.EdgeVisibilityOff();
////        SELECTION_PROPERTY.EdgeVisibilityOn();
//        SELECTION_PROPERTY.SetEdgeColor(VTKColors.WHITE);
//        SELECTION_PROPERTY.SetLineWidth(1);
//
//        SELECTION_PROPERTY.SetColor(VTKColors.SELECTION_COLOR);
//        SELECTION_PROPERTY.LightingOff();
////        SELECTION_PROPERTY.SetOpacity(SELECTION_OPACITY);
//        
//        SELECTION_PROPERTY.SetAmbient(VTKColors.SELECTION_AMBIENT);
//        SELECTION_PROPERTY.SetDiffuse(VTKColors.SELECTION_DIFFUSE);
//        SELECTION_PROPERTY.SetSpecular(VTKColors.SELECTION_SPECULAR);
//        SELECTION_PROPERTY.SetSpecularPower(VTKColors.SELECTION_SPECULAR_POWER);
//    }
//    
//    private void applySelectionProperty() {
//        if (ArrayUtils.isEquals(GetProperty().GetColor(), VTKColors.WHITE)) {
//            GetProperty().SetRepresentationToSurface();
//            GetProperty().EdgeVisibilityOff();
////        GetProperty().EdgeVisibilityOn();
//            GetProperty().SetEdgeColor(VTKColors.WHITE);
//            GetProperty().SetLineWidth(1);
//            
//            GetProperty().SetColor(VTKColors.SELECTION_COLOR);
//            GetProperty().LightingOn();
////        SELECTION_PROPERTY.SetOpacity(SELECTION_OPACITY);
//            
//            GetProperty().SetAmbient(VTKColors.SELECTION_AMBIENT);
//            GetProperty().SetDiffuse(VTKColors.SELECTION_DIFFUSE);
//            GetProperty().SetSpecular(VTKColors.SELECTION_SPECULAR);
//            GetProperty().SetSpecularPower(VTKColors.SELECTION_SPECULAR_POWER);
//        }
//    }
//    
//    private void setUpDeselectionProperty() {
//        DESELECTION_PROPERTY = new vtkProperty(); 
//        DESELECTION_PROPERTY.SetOpacity(DESELECTION_OPACITY);
//        
//        DESELECTION_PROPERTY.SetColor(VTKColors.DESELECTION_COLOR);
//        DESELECTION_PROPERTY.SetAmbient(VTKColors.SELECTION_AMBIENT);
//        DESELECTION_PROPERTY.SetDiffuse(VTKColors.SELECTION_DIFFUSE);
//        DESELECTION_PROPERTY.SetSpecular(VTKColors.SELECTION_SPECULAR);
//        DESELECTION_PROPERTY.SetSpecularPower(VTKColors.SELECTION_SPECULAR_POWER);
//    }
//
//    private void applyDeselectionProperty() {
//        GetProperty().SetOpacity(DESELECTION_OPACITY);
//        
//        GetProperty().SetColor(VTKColors.DESELECTION_COLOR);
//        GetProperty().SetAmbient(VTKColors.SELECTION_AMBIENT);
//        GetProperty().SetDiffuse(VTKColors.SELECTION_DIFFUSE);
//        GetProperty().SetSpecular(VTKColors.SELECTION_SPECULAR);
//        GetProperty().SetSpecularPower(VTKColors.SELECTION_SPECULAR_POWER);
//    }
//
//    protected void newActor(vtkPolyData dataset, boolean visible) {
//        vtkPolyData input = new vtkPolyData();
//        input.ShallowCopy(dataset);
//
//        this.mapper = new vtkPolyDataMapper();
//        SetMapper(mapper);
//        //StaticOn();
//        
//        this.outline = false;
//        this.profile = false;
//        this.scalar = false;
//        
//        setInput(input);
//        
//        input.Delete();
//
//        SetVisibility(visible ? 1 : 0);
//
//        GetProperty().DeepCopy(baseProperty);
//    }
//    
//    protected void newActor(vtkUnstructuredGrid dataset, boolean visible) {
//        vtkUnstructuredGrid input = new vtkUnstructuredGrid();
//        input.ShallowCopy(dataset);
//
//        vtkDataSetSurfaceFilter filter = new vtkDataSetSurfaceFilter();
//        filter.SetInputData(input);
//        filter.PassThroughCellIdsOn();
//        filter.PassThroughPointIdsOn();
//        filter.Update();
//        input.Delete();
//
//        newActor(filter.GetOutput(), visible);
//        filter.Delete();
//    }
//
//    @Override
//    public void setInput(vtkPolyData input) {
////        System.out.println("DefaultActor.setInput() size: " + input.GetActualMemorySize() + " kB");
//        mapper.RemoveAllInputs();
//        mapper.SetInputData(input);
//        
//        int memory = PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_MEMORY, 512);
//        if (input.GetActualMemorySize() > memory) {
//            SetEnableLOD(1);
//        } else {
//            SetEnableLOD(1);
//        }
//        
//    }
//    
//    @Override
//    public void setInput(vtkUnstructuredGrid input) {
//        vtkPolyData filter = VTKUtil.geometryFilter(input);
//        setInput(filter);
//    }
//    
//    @Override
//    public void interactiveOn() {
//        if (!outline && !profile) {
//            SetDisplayTypeToInteractive();
//        } 
//    }
//
//    @Override
//    public void interactiveOff() {
//        if (!outline && !profile) {
//            SetDisplayTypeToFull();
//        }
//    }
//    
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public void rename(String name) {
//        this.name = name;
//    }
//    
//    @Override
//    public vtkMapper getMapper() {
//        return GetMapper();
//    }
//
//    @Override
//    public boolean getVisibility() {
//        return GetVisibility() == 1;
//    }
//
//    @Override
//    public void setVisibility(boolean onoff) {
//        SetVisibility(onoff ? 1 : 0);
//    }
//
//    @Override
//    public double[] getBounds() {
//        return GetBounds();
//    }
//    
//    @Override
//    public vtkActor getActor() {
//        return this;
//    }
//    
//    @Override
//    public void deleteActor() {
////        if (LODFilter != null) {
////            LODMapper.RemoveAllInputs();
////            LODMapper.Delete();
////            LODFilter.RemoveAllInputs();
////            LODFilter.Delete();
////        }
////        vtkDataSet dataset = mapper.GetInputAsDataSet();
////
////        VTKUtil.deleteDataset(dataset);
////
////        mapper.RemoveAllInputs();
////        mapper.Delete();
//        
//        SetMapper(null);
//        Delete();
//    }
//
//    @Override
//    public void transformActor(boolean save, AffineTransform t) {
//        vtkTransform transform = t.toVTK(this.transform);
//        
//        SetUserTransform(transform);
//
//        if (save) {
//            this.transform = transform;
//        }
//    }
//    
//    @Override
//    public vtkTransform getUserTransform() {
//        return (vtkTransform) GetUserTransform();
//    }
//    
//    @Override
//    public void setSolidColor(double[] color, double opacity) {
//        this.scalar = false;
//        setColor(color, opacity);
//
//        scalarVisibilityOff();
//    }
//    
//    @Override
//    public void setScalarColors(vtkLookupTable lut, FieldItem field) {
//        this.scalar = true;
//        setColor(VTKColors.WHITE, BASE_OPACITY);
//        
////        if (mapper != null) {
////            setScalarColor(mapper, lut, field);
////        }
////        
////        if (LODMapper != null) {
////            setScalarColor(LODMapper, lut, field);
////        }
//    }
//    
//    protected void setLineWidth(int width) {
//        GetProperty().SetLineWidth(width);
//
//        baseProperty.SetLineWidth(width);
//        SELECTION_PROPERTY.SetLineWidth(width);
//        DESELECTION_PROPERTY.SetLineWidth(width);
//    }
//    
//    protected void setColor(double[] color, double opacity) {
//        GetProperty().SetColor(color);
//        GetProperty().SetOpacity(opacity);
//        
//        baseProperty.SetColor(color);
//        baseProperty.SetOpacity(opacity);
//        
////        SELECTION_PROPERTY.SetColor(color);
//        
////        DESELECTION_PROPERTY.SetColor(color);
//    }
//
//    private static void setScalarColor(vtkMapper mapper, vtkLookupTable lut, FieldItem field) {
//        mapper.SetLookupTable(lut);
//        mapper.UseLookupTableScalarRangeOn();
//        mapper.ScalarVisibilityOn();
//        mapper.SetColorModeToMapScalars();
//
//        if (field.getDataType().isCell()) {
//            mapper.SetScalarModeToUseCellFieldData();
//        } else if (field.getDataType().isPoint()) {
//            mapper.SetScalarModeToUsePointFieldData();
//        }
//
//        mapper.SetScalarRange(lut.GetRange());
//        mapper.SelectColorArray(field.getName());
//        mapper.Update();
//    }
//    
//    @Override
//    public void setRepresentation(Representation representation) {
//        switch (representation) {
//            case SURFACE:
//                GetProperty().SetRepresentationToSurface();
//                GetProperty().EdgeVisibilityOff();
//                
//                baseProperty.SetRepresentationToSurface();
//                baseProperty.EdgeVisibilityOff();
//                setOutLine(false);
//                setProfile(false);
//                break;
//            case WIREFRAME:
//                GetProperty().SetRepresentationToWireframe();
//                GetProperty().EdgeVisibilityOff();
//                
//                baseProperty.SetRepresentationToWireframe();
//                baseProperty.EdgeVisibilityOff();
//                setOutLine(false);
//                setProfile(false);
//                break;
//            case SURFACE_WITH_EDGES:
//                GetProperty().SetRepresentationToSurface();
//                GetProperty().EdgeVisibilityOn();
//                
//                baseProperty.SetRepresentationToSurface();
//                baseProperty.EdgeVisibilityOn();
//                setOutLine(false);
//                setProfile(false);
//                break;
//            case OUTLINE:
//                GetProperty().SetRepresentationToSurface();
//                GetProperty().EdgeVisibilityOff();
//                
//                baseProperty.SetRepresentationToSurface();
//                baseProperty.EdgeVisibilityOff();
//                setOutLine(true);
//                setProfile(false);
//                break;
//                
//            case PROFILE:
//                GetProperty().SetRepresentationToSurface();
//                GetProperty().EdgeVisibilityOff();
//                
//                baseProperty.SetRepresentationToSurface();
//                baseProperty.EdgeVisibilityOff();
//                setOutLine(false);
//                setProfile(true);
//                break;
//
//            default:
//                break;
//        }
//    }
//    
//    private void setOutLine(boolean outline) {
//        if (outline) {
//            if (!this.outline) {
//                outlineActor();
//            }
//        } else {
//            if (this.outline) {
//                deoutlineActor();
//            }
//        }
//        this.outline = outline;
//    }
//    
//    private void setProfile(boolean profile) {
//        if (profile) {
//            if (!this.profile) {
//                profileActor();
//            }
//        } else {
//            if (this.profile) {
//                deprofileActor();
//            }
//        }
//        this.profile = profile;
//    }
//    
//    private void outlineActor() {
//        SetDisplayTypeToOutline();
//    }
//
//    private void deoutlineActor() {
//        SetDisplayTypeToFull();
//    }
//    
//    private void profileActor() {
//        SetDisplayTypeToProfile();
//    }
//
//    private void deprofileActor() {
//        SetDisplayTypeToFull();
//    }
//    
//    @Override
//    public void restoreFromSelection() {
////        GetProperty().DeepCopy(baseProperty);
//        applyBaseProperty();
//        
//        if (scalar) {
//            scalarVisibilityOn();
//        } else {
//            scalarVisibilityOff();
//        }
//    } 
//    
//    @Override
//    public void selectActor() {
////        GetProperty().DeepCopy(SELECTION_PROPERTY);
//        applySelectionProperty();
//        
//        scalarVisibilityOff();
//    }
//    
//    @Override
//    public void deselectActor() {
////        GetProperty().DeepCopy(DESELECTION_PROPERTY);
////        applyDeselectionProperty();
//        applyBaseProperty();
//        
//        scalarVisibilityOff();
//    }
//
//    private void scalarVisibilityOff() {
//        GetMapper().ScalarVisibilityOff();
////        if (mapper != null) {
////            mapper.ScalarVisibilityOff();
////        }
////        
////        if (LODMapper != null) {
////            LODMapper.ScalarVisibilityOff();
////        }
//    }
//
//    private void scalarVisibilityOn() {
//        GetMapper().ScalarVisibilityOn();
////        if (mapper != null) {
////            mapper.ScalarVisibilityOn();
////        }
////        
////        if (LODMapper != null) {
////            LODMapper.ScalarVisibilityOn();
////        }
//    }
//    
//}

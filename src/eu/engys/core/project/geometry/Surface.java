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


package eu.engys.core.project.geometry;

import static eu.engys.core.project.system.SnappyHexMeshDict.EXPANSION_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FACE_TYPE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FINAL_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.LEVEL_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.NONE_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SURFACE_LAYERS_KEY;
import vtk.vtkPolyData;
import vtk.vtkTransform;
import vtk.vtkTransformFilter;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.surface.Region;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public abstract class Surface implements VisibleItem {

    public static final String MAX_KEY = "max";
    public static final String MIN_KEY = "min";
    public static final String POINT1_KEY = "point1";
    public static final String POINT2_KEY = "point2";
    public static final String RADIUS_KEY = "radius";
    public static final String CENTRE_KEY = "centre";
    public static final String OUTER_RADIUS_KEY = "outerRadius";
    public static final String INNER_RADIUS_KEY = "innerRadius";
    public static final String PLANE_TYPE_KEY = "planeType";
    public static final String NORMAL_VECTOR_KEY = "normalVector";
    public static final String BASE_POINT_KEY = "basePoint";
    public static final String POINT_AND_NORMAL_KEY = "pointAndNormal";
    public static final String POINT_AND_NORMAL_DICT_KEY = "pointAndNormalDict";

    public static final String TRI_SURFACE_MESH_KEY = "triSurfaceMesh";
    public static final String SEARCHABLE_RING_KEY = "searchableRing";
    public static final String SEARCHABLE_PLANE_KEY = "searchablePlane";
    public static final String SEARCHABLE_SPHERE_KEY = "searchableSphere";
    public static final String SEARCHABLE_CYLINDER_KEY = "searchableCylinder";
    public static final String SEARCHABLE_BOX_KEY = "searchableBox";

    private Dictionary geometryDictionary;
    private Dictionary surfaceDictionary;
    private Dictionary volumeDictionary;
    private Dictionary layerDictionary;
    private Dictionary zoneDictionary;

    protected String name;

    private boolean visible = true;
    private AffineTransform transformation;
    private TransfromMode transformMode;

    public static final Dictionary surfaceDefault = new Dictionary("") {
    	{
    		add(LEVEL_KEY, "(0 0)");
    	}
    };
    public static final Dictionary volumeDefault = new Dictionary("") {
    	{
    		add("mode", "none");
    	}
    };
    public static final Dictionary zonesDefault = new Dictionary("") {
    	{
    		add(FACE_TYPE_KEY, NONE_KEY);
    	}
    };
    public static final Dictionary layerDefault = new Dictionary("") {
    	{
    		add(N_SURFACE_LAYERS_KEY, "0");
    		add(EXPANSION_RATIO_KEY, "1.25");
    		add(FINAL_LAYER_THICKNESS_KEY, "0.4");
    	}
    };

    public static final Dictionary stl = new Dictionary("name.stl") {
        {
            add(TYPE, TRI_SURFACE_MESH_KEY);
            add("name", "name");
        }
    };
    public static final Dictionary box = new Dictionary("box") {
        {
            add(TYPE, SEARCHABLE_BOX_KEY);
            add(MIN_KEY, "(0 0 0)");
            add(MAX_KEY, "(2 2 1)");
        }
    };
    public static final Dictionary cylinder = new Dictionary("cylinder") {
        {
            add(TYPE, SEARCHABLE_CYLINDER_KEY);
            add(POINT1_KEY, "(0 0 0)");
            add(POINT2_KEY, "(1 0 0)");
            add(RADIUS_KEY, "1.0");
        }
    };
    public static final Dictionary sphere = new Dictionary("sphere") {
        {
            add(TYPE, SEARCHABLE_SPHERE_KEY);
            add(CENTRE_KEY, "(0 0 0)");
            add(RADIUS_KEY, "1.0");
        }
    };
    public static final Dictionary plane = new Dictionary("plane") {
        {
            add(TYPE, SEARCHABLE_PLANE_KEY);
            add(PLANE_TYPE_KEY, POINT_AND_NORMAL_KEY);
            Dictionary dict = new Dictionary(POINT_AND_NORMAL_DICT_KEY);
//            dict.add(BASE_POINT_KEY, "(0 0 0)");
            dict.add(NORMAL_VECTOR_KEY, "(0 0 1)");
            add(dict);
        }
    };
    public static final Dictionary planeWithCenter = new Dictionary("plane") {
        {
            add(TYPE, SEARCHABLE_PLANE_KEY);
            add(PLANE_TYPE_KEY, POINT_AND_NORMAL_KEY);
            Dictionary dict = new Dictionary(POINT_AND_NORMAL_DICT_KEY);
            dict.add(BASE_POINT_KEY, "(0 0 0)");
            dict.add(NORMAL_VECTOR_KEY, "(0 0 1)");
            add(dict);
        }
    };
    public static final Dictionary ring = new Dictionary("ring") {
        {
            add(TYPE, SEARCHABLE_RING_KEY);
            add(POINT1_KEY, "(0 0 0)");
            add(POINT2_KEY, "(0.05 0 0)");
            add(INNER_RADIUS_KEY, "0.2");
            add(OUTER_RADIUS_KEY, "0.5");
        }
    };

    public Surface(String name) {
        this.name = name;

        this.surfaceDictionary = new Dictionary(name, surfaceDefault);
        this.volumeDictionary = new Dictionary(name, volumeDefault);
        this.layerDictionary = new Dictionary(name, layerDefault);
        this.zoneDictionary = new Dictionary(name, zonesDefault);
        
        this.transformation = new AffineTransform();
//        this.transformMode = TransfromMode.TO_DICTIONARY;
        this.transformMode = TransfromMode.TO_FILE;
    }

    public boolean isAppendRegionName() {
        return geometryDictionary.found("appendRegionName") && geometryDictionary.lookup("appendRegionName").equals("true");
    }

    public String getName() {
        return name;
    }

    public abstract String getPatchName();

    public String getZoneName() {
        return zoneDictionary.lookup("cellZone");
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public abstract Type getType();

    public abstract boolean isSingleton();

    public abstract boolean hasRegions();

    public abstract Region[] getRegions();

    public abstract boolean hasSurfaceRefinement();

    public abstract boolean hasVolumeRefinement();

    public abstract boolean hasLayers();

    public abstract boolean hasZones();

    public abstract Surface cloneSurface();

    protected abstract vtkPolyData getDataSet();
    
    public vtkPolyData getTransformedDataSet() {
        vtkPolyData dataSet = getDataSet();
        if (dataSet != null) {
            if (getTransformation() != null) {
                vtkTransformFilter tFilter = new vtkTransformFilter();
                tFilter.SetTransform(getTransformation().toVTK(new vtkTransform()));
                tFilter.SetInputData(dataSet);
                tFilter.Update();
                
                return (vtkPolyData) tFilter.GetOutput();
            } else {
                return dataSet;
            }
        } else {
            return null;
        }
    }

    public void setGeometryDictionary(Dictionary geometryDictionary) {
        this.geometryDictionary = geometryDictionary;
    }

    public Dictionary getGeometryDictionary() {
        return geometryDictionary;
    }

    public void setSurfaceDictionary(Dictionary surfaceDictionary) {
        this.surfaceDictionary = surfaceDictionary;
    }

    public Dictionary getSurfaceDictionary() {
        return surfaceDictionary;
    }

    public void setVolumeDictionary(Dictionary volumeDictionary) {
        this.volumeDictionary = volumeDictionary;
    }

    public Dictionary getVolumeDictionary() {
        return volumeDictionary;
    }

    public void setLayerDictionary(Dictionary layerDictionary) {
        this.layerDictionary = layerDictionary;
    }

    public Dictionary getLayerDictionary() {
        return layerDictionary;
    }

    public void setZoneDictionary(Dictionary zoneDictionary) {
        this.zoneDictionary = zoneDictionary;
    }

    public Dictionary getZoneDictionary() {
        return zoneDictionary;
    }

    public Dictionary toDictionary() {
        Dictionary d = new Dictionary(getName());
        d.add(new Dictionary("surface", surfaceDictionary));
        d.add(new Dictionary("volume", volumeDictionary));
        d.add(new Dictionary("layer", layerDictionary));
        d.add(new Dictionary("zone", zoneDictionary));

        return d;
    }

    public void fromDictionary(Dictionary d) {
        buildSurfaceDictionary(d.subDict("surface"));
        buildVolumeDictionary(d.subDict("volume"));
        buildLayerDictionary(d.subDict("layer"));
        buildZoneDictionary(d.subDict("zone"));
    }

    @Override
    public String toString() {
        return String.format("[ name: %s, patch_name: %s, type: %s, singleton: %s, visible: %s] ", getName(), getPatchName(), getType(), isSingleton(), isVisible());
    }

    public void rename(String newName) {
        String oldName = getName();
        if (oldName.equals(newName))
            return;

        this.name = newName;
        getSurfaceDictionary().setName(getName());
        getVolumeDictionary().setName(getName());
        getLayerDictionary().setName(getName());
        getZoneDictionary().setName(getName());
    }

    public void buildGeometryDictionary(Dictionary dictionary) {
        Dictionary geometryDict = new Dictionary(dictionary);
        geometryDict.setName(getName());
        getSurfaceDictionary().setName(getName());
        getVolumeDictionary().setName(getName());
        getLayerDictionary().setName(getName());
        getZoneDictionary().setName(getName());
        setGeometryDictionary(geometryDict);
    }

    public void buildSurfaceDictionary(Dictionary dictionary) {
        Dictionary surfaceDict = new Dictionary(dictionary);
        surfaceDict.setName(getName());
        setSurfaceDictionary(surfaceDict);
    }

    public void buildVolumeDictionary(Dictionary dictionary) {
        Dictionary volumeDict = new Dictionary(dictionary);
        volumeDict.setName(getName());
        setVolumeDictionary(volumeDict);
    }

    public void buildLayerDictionary(Dictionary dictionary) {
        Dictionary layerDict = new Dictionary(dictionary);
        layerDict.setName(getName());
        setLayerDictionary(layerDict);
    }

    public void buildZoneDictionary(Dictionary dictionary) {
        Dictionary zoneDict = new Dictionary(dictionary);
        zoneDict.setName(getName());
        setZoneDictionary(zoneDict);
    }

    public boolean willBePatch() {
        return (getType().isSolid() && !((Solid) this).getParent().isSingleton()) || (getType().isStl() && isSingleton());
    }

    protected void cloneSurface(Surface surface) {
        if (this.geometryDictionary != null) {
            surface.geometryDictionary = new Dictionary(this.geometryDictionary);
        }
        surface.surfaceDictionary = new Dictionary(this.surfaceDictionary);
        surface.volumeDictionary = new Dictionary(this.volumeDictionary);
        surface.layerDictionary = new Dictionary(this.layerDictionary);
        surface.zoneDictionary = new Dictionary(this.zoneDictionary);
        
        surface.visible = this.visible;
        surface.transformation = new AffineTransform(this.transformation);
        //System.out.println("Surface.cloneSurface() HASH: "+surface.hashCode()+surfaceDictionary+surface.surfaceDictionary);
    }

    public AffineTransform getTransformation() {
        return transformation;
    }
    public void setTransformation(AffineTransform transformation) {
        this.transformation = transformation;
    }
    
    public TransfromMode getTransformMode() {
        return transformMode;
    }
    public void setTransformMode(TransfromMode transformMode) {
        this.transformMode = transformMode;
    }
}

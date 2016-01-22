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


package eu.engys.core.project.geometry.factory;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.geometry.Surface.SEARCHABLE_BOX_KEY;
import static eu.engys.core.project.geometry.Surface.SEARCHABLE_CYLINDER_KEY;
import static eu.engys.core.project.geometry.Surface.SEARCHABLE_PLANE_KEY;
import static eu.engys.core.project.geometry.Surface.SEARCHABLE_RING_KEY;
import static eu.engys.core.project.geometry.Surface.SEARCHABLE_SPHERE_KEY;
import static eu.engys.core.project.geometry.Surface.TRI_SURFACE_MESH_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FILE_KEY;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkPolyData;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.TransfromMode;
import eu.engys.core.project.geometry.stl.AffineTransform;
import eu.engys.core.project.geometry.stl.STLReader;
import eu.engys.core.project.geometry.stl.STLWriter;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.geometry.surface.Cylinder;
import eu.engys.core.project.geometry.surface.NullSurface;
import eu.engys.core.project.geometry.surface.Plane;
import eu.engys.core.project.geometry.surface.Ring;
import eu.engys.core.project.geometry.surface.Solid;
import eu.engys.core.project.geometry.surface.Sphere;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.util.ColorUtil;
import eu.engys.util.progress.ProgressMonitor;

public class DefaultGeometryFactory implements GeometryFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGeometryFactory.class);
    private static final Map<String, Stl> STLCache = new HashMap<>();

    @Override
    public void deleteSurface(Model model, Surface surface) {
        if(surface instanceof Stl) {
            File file = model.getProject().getConstantFolder().getTriSurface().getFileManager().getFile(surface.getName()+".stl");
            if (file.exists()) {
              FileUtils.deleteQuietly(file);
            }
            file = model.getProject().getConstantFolder().getTriSurface().getFileManager().getFile(surface.getName()+".STL");
            if (file.exists()) {
              FileUtils.deleteQuietly(file);
            }
        }
    }
    
    @Override
    public void writeSurface(Surface surface, Model model, ProgressMonitor monitor) {
        if (surface.getType().isStl()) {
            writeSTL((Stl) surface, model, monitor);
        } else if (surface.getType().isLine()) {
            writeFeatureLine((FeatureLine) surface, model, monitor);
        }
    }

    private void writeFeatureLine(FeatureLine line, Model model, ProgressMonitor monitor) {
        String fileName = line.getName() + ".eMesh";
        File file = model.getProject().getConstantFolder().getTriSurface().getFileManager().getFile(fileName);
        if (!file.exists()|| line.isModified()) {
            new EMESHWriter(file, line).run();
        }
    }

    private void writeSTL(Stl stl, Model model, ProgressMonitor monitor) {
        String fileName = stl.getFileName();
        File file = model.getProject().getConstantFolder().getTriSurface().getFileManager().getFile(fileName);
        AffineTransform transformation = stl.getTransformation();
        
        if (stl.getTransformMode() == TransfromMode.TO_DICTIONARY) {
            if (!file.exists() || stl.isModified()) {
                new STLWriter(file, stl, monitor).run();
            }
        } else { 
            if (!file.exists() || !transformation.isIdentity() || stl.isModified()) {
                new STLWriter(file, stl, monitor).run();
            }
        }
    }
    
    
    
    @Override
    public Surface loadSurface(Dictionary g, Model model, ProgressMonitor monitor) {
        Surface surface;
        if (isSTL(g)) {
            surface = loadSTL(g, model, monitor);
        } else if (isBox(g)) {
            surface = loadBox(g);
        } else if (isCylinder(g)) {
            surface = loadCylinder(g);
        } else if (isSphere(g)) {
            surface = loadSphere(g);
        } else if (isPlane(g)) {
            surface = loadPlane(g);
        } else if (isRing(g)) {
            surface = loadRing(g);
        } else if (isLine(g)) {
            surface = loadLine(g, model, monitor);
        } else {
            if (g.isField(Dictionary.TYPE))
                logger.error("Unknown geometry type: {}.", g.lookup(Dictionary.TYPE));
            else
                logger.error("Bad geometry dictionary format: {}.", g);
            surface = new NullSurface();
        }
        if (monitor != null) {
            monitor.setCurrent(null, monitor.getCurrent() + 1);
        }
        return surface;
    }

    @Override
    public <S extends Surface> S newSurface(Class<S> type, String name) {
        try {
            return type.getDeclaredConstructor(String.class).newInstance(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * LOAD
     */

    @SuppressWarnings("deprecation")
    protected Surface loadBox(Dictionary g) {
        Surface box = new Box(g.getName());
        box.setGeometryDictionary(g);
        return box;
    }

    @SuppressWarnings("deprecation")
    protected Cylinder loadCylinder(Dictionary g) {
        Cylinder cyl = new Cylinder(g.getName());
        cyl.setGeometryDictionary(g);
        return cyl;
    }

    @SuppressWarnings("deprecation")
    protected Plane loadPlane(Dictionary g) {
        Plane plane = new Plane(g.getName());
        plane.setGeometryDictionary(g);
        return plane;
    }

    @SuppressWarnings("deprecation")
    protected Ring loadRing(Dictionary g) {
        Ring ring = new Ring(g.getName());
        ring.setGeometryDictionary(g);
        return ring;
    }

    @SuppressWarnings("deprecation")
    protected Sphere loadSphere(Dictionary g) {
        Sphere sphere = new Sphere(g.getName());
        sphere.setGeometryDictionary(g);
        return sphere;
    }

    @SuppressWarnings("deprecation")
    protected Stl loadSTL(Dictionary g, Model model, ProgressMonitor monitor) {
        Stl stl = new Stl(g.lookup("name"));
        stl.setGeometryDictionary(g);
        
        loadStl(stl, model, monitor);

        stl.setTransformation(AffineTransform.fromGeometryDictionary(g));
        return stl;
    }

    private void loadStl(Stl stl, Model model, ProgressMonitor monitor) {
        String fileName = stl.getGeometryDictionary().getName();
        File file = model.getProject().getConstantFolder().getTriSurface().getFileManager().getFile(fileName);
        stl.setFileName(file);

        if (STLCache.containsKey(fileName)) {
            Stl cached = STLCache.get(fileName);
            Solid[] cachedSolids = cached.getSolids();
            List<Solid> solids = new ArrayList<>();
            for (Solid cachedSolid : cachedSolids) {
                solids.add((Solid)cachedSolid.cloneSurface());
            }
            stl.setSolids(solids);
        } else {
            STLReader reader = new STLReader(file, monitor);
            reader.run();
            List<Solid> solids = reader.getSolids();
            stl.setSolids(solids);
        }
        
        if (!STLCache.containsKey(fileName)) {
            STLCache.put(fileName, stl);
        }
    }

    @Override
    public Stl readSTL(File file, ProgressMonitor monitor) {
        String fileName = file.getName();
        String name = FilenameUtils.removeExtension(fileName);
        Dictionary g = new Dictionary(fileName, Surface.stl);
        g.setName(fileName);
        g.add("name", name);

        Stl stl = new Stl(name);
        stl.setGeometryDictionary(g);
        stl.setFileName(file);
        
        STLReader reader = new STLReader(file, monitor);
        reader.run();
        List<Solid> solids = reader.getSolids();
        stl.setSolids(solids);
        
        return stl;
    }

    private FeatureLine loadLine(Dictionary g, Model model, ProgressMonitor monitor) {
        String fileName = g.lookup("file").replace("\"", "");
        File file = model.getProject().getConstantFolder().getTriSurface().getFileManager().getFile(fileName);
        FeatureLine line = readLine(file);
        line.setColor(ColorUtil.getColor(model.getGeometry().getLines().size()));
        return line;
    }
    
    @Override
    public FeatureLine readLine(File file) {
        FeatureLine featureLine = new FeatureLine(FilenameUtils.removeExtension(file.getName()));
        featureLine.setModified(false);
        vtkPolyData dataSet = new EMESHReader(file).run();
        featureLine.setDataSet(dataSet);
        
        return featureLine;
    }
    
    /*
     * IS
     */

    public static boolean isBox(Dictionary g) {
        return g.isField(TYPE) && SEARCHABLE_BOX_KEY.equals(g.lookup(TYPE));
    }

    public static boolean isCylinder(Dictionary g) {
        return g.isField(TYPE) && SEARCHABLE_CYLINDER_KEY.equals(g.lookup(TYPE));
    }

    public static boolean isSphere(Dictionary g) {
        return g.isField(TYPE) && SEARCHABLE_SPHERE_KEY.equals(g.lookup(TYPE));
    }

    public static boolean isPlane(Dictionary g) {
        return g.isField(TYPE) && SEARCHABLE_PLANE_KEY.equals(g.lookup(TYPE));
    }

    public static boolean isRing(Dictionary g) {
        return g.isField(TYPE) && SEARCHABLE_RING_KEY.equals(g.lookup(TYPE));
    }

    public static boolean isLine(Dictionary g) {
        return g.isField(FILE_KEY) && g.lookup(FILE_KEY).contains(".eMesh");
    }

    public static boolean isSTL(Dictionary g) {
        return g.isField(TYPE) && TRI_SURFACE_MESH_KEY.equals(g.lookup(TYPE));
    }
    
    public static void clearSTLCache() {
        STLCache.clear();
    }
}

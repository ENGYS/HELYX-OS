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


package eu.engys.core.project.constant;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.Model;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.files.DefaultFileManager;
import eu.engys.core.project.files.FileManager;
import eu.engys.core.project.files.Folder;
import eu.engys.core.project.materials.MaterialsWriter;
import eu.engys.core.project.state.State;
import eu.engys.util.progress.ProgressMonitor;

public class ConstantFolder implements Folder {

    public static final String CONSTANT = "constant";

    public static final String POLY_MESH = "polyMesh";
    public static final String TRISURFACE = "triSurface";
    public static final String G = "g";
    public static final String RADIATION_PROPERTIES = "radiationProperties";
    public static final String REGION_PROPERTIES = "regionProperties";
    public static final String LES_PROPERTIES = "LESProperties";
    public static final String RAS_PROPERTIES = "RASProperties";

    //ECOMARINE
    public static final String UFS_KEY = "Ufs";
    public static final String BEACH_KEY = "beach";
    public static final String FREE_SURFACE_PROPERTIES = "freeSurfaceProperties";
    
    private final TriSurfaceFolder triSurface;
    private final PolyMeshFolder polyMesh;

    private Dictionary g;

    private TurbulenceProperties turbulenceProperties;
    private Dictionary RASProperties;
    private Dictionary LESProperties;
    private TransportProperties transportProperties;
    private ThermophysicalProperties thermophysicalProperties;

    private final FileManager fileManager;

    public ConstantFolder(openFOAMProject prj) {
        File constant = new File(prj.getBaseDir(), CONSTANT);
        fileManager = new DefaultFileManager(constant);
        triSurface = new TriSurfaceFolder(constant);
        polyMesh = new PolyMeshFolder(constant);
    }

    public ConstantFolder(File baseDir, ConstantFolder constantFolder) {
        File constant = new File(baseDir, CONSTANT);
        fileManager = new DefaultFileManager(constant);
        triSurface = new TriSurfaceFolder(constant);
        polyMesh = new PolyMeshFolder(constant);

        setG(constantFolder.g);
        setTurbulenceProperties(constantFolder.turbulenceProperties);
        setRASProperties(constantFolder.RASProperties);
        setLESProperties(constantFolder.LESProperties);
        setTransportProperties(constantFolder.transportProperties);
        setThermophysicalProperties(constantFolder.thermophysicalProperties);

        // setPorousZones(constantFolder.porousZones);
        // setMRFZones(constantFolder.MRFZones);
    }

    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    public Dictionary getG() {
        return g;
    }

    public void setG(Dictionary g) {
        this.g = g;
    }

    public TurbulenceProperties getTurbulenceProperties() {
        return turbulenceProperties;
    }

    public void setTurbulenceProperties(Dictionary turbulenceProperties) {
        this.turbulenceProperties = new TurbulenceProperties(turbulenceProperties);
    }

    public Dictionary getRASProperties() {
        return RASProperties;
    }

    public void setRASProperties(Dictionary rASProperties) {
        RASProperties = rASProperties;
    }

    public Dictionary getLESProperties() {
        return LESProperties;
    }

    public void setLESProperties(Dictionary lESProperties) {
        LESProperties = lESProperties;
    }

    public TransportProperties getTransportProperties() {
        return transportProperties;
    }

    public void setTransportProperties(Dictionary transportProperties) {
        this.transportProperties = new TransportProperties(transportProperties);
    }

    public ThermophysicalProperties getThermophysicalProperties() {
        return thermophysicalProperties;
    }

    public void setThermophysicalProperties(Dictionary thermophysicalProperties) {
        this.thermophysicalProperties = new ThermophysicalProperties(thermophysicalProperties);
    }

    public TriSurfaceFolder getTriSurface() {
        return triSurface;
    }

    public PolyMeshFolder getPolyMesh() {
        return polyMesh;
    }

    public List<Dictionary> getAllDictionaries() {
        List<Dictionary> dictionaries = new ArrayList<>();
        dictionaries.add(getG());
        dictionaries.add(getLESProperties());
        dictionaries.add(getRASProperties());
        dictionaries.add(getThermophysicalProperties());
        dictionaries.add(getTransportProperties());
        dictionaries.add(getTurbulenceProperties());
        return dictionaries;
    }

    public void write(Model model, MaterialsWriter materialsWriter, ProgressMonitor monitor) {
        model.getMaterials().saveMaterials(model, materialsWriter);

        File constDir = fileManager.getFile();
        if (!constDir.exists())
            constDir.mkdir();

        DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, turbulenceProperties), monitor);

        State state = model.getState();
        if (state.isLES()) {
            DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, LESProperties), monitor);
            DictionaryUtils.removeDictionary(constDir, DictionaryUtils.header(CONSTANT, RASProperties), monitor);
        } else if (state.isRANS()) {
            DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, RASProperties), monitor);
            DictionaryUtils.removeDictionary(constDir, DictionaryUtils.header(CONSTANT, LESProperties), monitor);
        }

        if (state.isCompressible() && !state.getMultiphaseModel().isMultiphase()) {
            DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, thermophysicalProperties), monitor);
            DictionaryUtils.removeDictionary(constDir, DictionaryUtils.header(CONSTANT, transportProperties), monitor);
        } else if (state.isIncompressible() || (state.isCompressible() && state.getMultiphaseModel().isMultiphase())) {
            DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, transportProperties), monitor);
            DictionaryUtils.removeDictionary(constDir, DictionaryUtils.header(CONSTANT, thermophysicalProperties), monitor);
        }

        if (g == null && Files.exists(constDir.toPath().resolve(G))) {
            DictionaryUtils.removeDictionary(constDir, DictionaryUtils.header(CONSTANT, new Dictionary(G)), monitor);
        } else if (g != null && g.isEmpty()) {
            DictionaryUtils.removeDictionary(constDir, DictionaryUtils.header(CONSTANT, g), monitor);
        } else {
            DictionaryUtils.writeDictionary(constDir, DictionaryUtils.header(CONSTANT, g), monitor);
        }

    }

    public void load(Model model, ProgressMonitor monitor) {
        if (fileManager.getFile().exists() && fileManager.getFile().isDirectory()) {
            setTurbulenceProperties(DictionaryUtils.readDictionary(fileManager.getFile(TurbulenceProperties.TURBULENCE_PROPERTIES), monitor));
            setRASProperties(DictionaryUtils.readDictionary(fileManager.getFile(RAS_PROPERTIES), monitor));
            setLESProperties(DictionaryUtils.readDictionary(fileManager.getFile(LES_PROPERTIES), monitor));
            setThermophysicalProperties(DictionaryUtils.readDictionary(fileManager.getFile(ThermophysicalProperties.THERMOPHYSICAL_PROPERTIES), monitor));
            setTransportProperties(DictionaryUtils.readDictionary(fileManager.getFile(TransportProperties.TRANSPORT_PROPERTIES), monitor));
            setG(DictionaryUtils.readDictionary(fileManager.getFile(G), monitor));
        }
    }
}

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
package eu.engys.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vtk.vtkNativeLibrary;

public class VTKSettings {

    private static final Logger logger = LoggerFactory.getLogger(VTKSettings.class);

    private static boolean librariesAreLoaded = false;

    public static void LoadAllNativeLibraries() {
        librariesAreLoaded = true;
        try {
            if (!_LoadAllNativeLibraries()) {
                for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
                    String libName = lib.GetLibraryName();
                    if (lib.IsLoaded()) {
                        logger.info(libName + " loaded");
                    } else {
                        librariesAreLoaded = false;
                        logger.error(libName + " NOT loaded");
                    }
                }
            } else {
                librariesAreLoaded = true;
                logger.info("ALL VTK libraries loaded");
            }
        } catch (Exception e) {
            librariesAreLoaded = false;
            logger.error("Error loading VTK libraries: " + e.getMessage());
        }

        if (librariesAreLoaded) {
            vtkNativeLibrary.DisableOutputWindow(null);
        } else {
            librariesAreLoaded = false;
        }
    }
    
    private static boolean _LoadAllNativeLibraries() {
        boolean isEveryThingLoaded = true;
        for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
            try {
                if(lib.IsBuilt()) {
                    lib.LoadLibrary();
                }
            } catch (UnsatisfiedLinkError e) {
                isEveryThingLoaded = false;
                e.printStackTrace();
            }
        }
//        try {
//            System.loadLibrary("vtkmyCommonJava");
//        } catch (UnsatisfiedLinkError e) {
//            isEveryThingLoaded = false;
//            e.printStackTrace();
//        }

        return isEveryThingLoaded;
    }
    
    public static boolean librariesAreLoaded() {
        return librariesAreLoaded;
    }
    
//    private static final String COMMON = "vtkCommonJava";
//    private static final String FILTERING = "vtkFilteringJava";
////  private static final String GEOVIS = "vtkGeovisJava";
//    private static final String GRAPHICS = "vtkGraphicsJava";
//    private static final String HYBRID = "vtkHybridJava";
////  private static final String IMAGING = "vtkImagingJava";
////  private static final String INFOVIS = "vtkInfovisJava";
//    private static final String IO = "vtkIOJava";
//    private static final String RENDERING = "vtkRenderingJava";
//    private static final String VIEWS = "vtkViewsJava";
//    private static final String VOLUME_RENDERING = "vtkVolumeRenderingJava";
//    private static final String WIDGETS = "vtkWidgetsJava";
////  private static final String CHARTS = "vtkChartsJava";
//    private static final String PARALLEL = "vtkParallelJava";
//
//    private static boolean librariesAreLoaded = false;
//
//    public static void LoadAllNativeLibraries() {
//        librariesAreLoaded = true;
//
//        loadLibrary(COMMON);
//        loadLibrary(FILTERING);
//        // loadLibrary(GEOVIS);
//        loadLibrary(GRAPHICS);
//        loadLibrary(PARALLEL);
//        loadLibrary(HYBRID);
//        // loadLibrary(IMAGING);
//        // loadLibrary(INFOVIS);
//        loadLibrary(IO);
//        loadLibrary(RENDERING);
//        loadLibrary(VIEWS);
//        loadLibrary(VOLUME_RENDERING);
//        loadLibrary(WIDGETS);
//        // loadLibrary(CHARTS);
//
//        if (librariesAreLoaded) { 
//            disableOutputWindow(null);
//        } else {
//            logger.warn("Make sure the search path is correct: ");
//            logger.warn(System.getProperty("java.library.path"));
//            librariesAreLoaded = false;
//            return;
//        }
//    }
//
//    private static void loadLibrary(String libName) {
//        try {
//            System.loadLibrary(libName);
//            logger.info(libName + " loaded");
//        } catch (UnsatisfiedLinkError e) {
//            logger.warn(libName + " NOT loaded: " + e.getMessage());
//            librariesAreLoaded = false;
//        }
//    }
//
//    public static boolean librariesAreLoaded() {
//        return librariesAreLoaded;
//    }
//
//    private static void disableOutputWindow(File logFile) {
//        if (logFile == null) {
//            logFile = new File("vtkError.txt");
//        }
//        vtkFileOutputWindow outputError = new vtkFileOutputWindow();
//        outputError.SetFileName(logFile.getAbsolutePath());
//        outputError.SetInstance(outputError);
//    }

}

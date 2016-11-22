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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.geometry.Surface;
import eu.engys.util.Util;
import eu.engys.util.ui.TreeUtil;

public class SurfacesSaver {

	private static final Logger logger = LoggerFactory.getLogger(SurfacesSaver.class);
	
	private boolean changeSurface;
	private boolean changeVolume;
	private boolean changeLayer;
	private boolean changeZone;

    private boolean changeGeometry;

	public void saveSurfaces(Surface delegate, Surface... surfaces) {
		if (Util.isVarArgsNotNull(surfaces)) {
			if (TreeUtil.isConsistent(surfaces, delegate.getClass())) {
				_saveSurfaces(delegate, surfaces);
			} else {
				logger.warn("Inconsistent selection");
			}
		}
	}

	private void _saveSurfaces(Surface delegate, Surface... surfaces) {
	    Dictionary surfaceDict = delegate.getSurfaceDictionary();
	    Dictionary volumeDict = delegate.getVolumeDictionary();
	    Dictionary layerDict = delegate.getLayerDictionary();
	    Dictionary zoneDict = delegate.getZoneDictionary();
	    
	    changeGeometry = surfaces.length == 1;
		changeSurface = !surfaces[0].getSurfaceDictionary().equals(surfaceDict);
		changeVolume = !surfaces[0].getVolumeDictionary().equals(volumeDict);
		changeLayer = !surfaces[0].getLayerDictionary().equals(layerDict);
		changeZone = !surfaces[0].getZoneDictionary().equals(zoneDict);
		
        for (Surface surface : surfaces) {
//            if (changeGeometry)  surface.buildGeometryDictionary(geometriesPanel.getBoxDictionary());
//            if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//            if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//            if (changeLayer) surface.buildLayerDictionary(layerDict);
//            if (changeZone) surface.buildZoneDictionary(zoneDict);
            surface.copyFrom(delegate, changeGeometry, changeSurface, changeVolume, changeLayer, changeZone);
            //System.err.println("SurfacesSaver._saveSurfaces() " + surface);
        }
		
//		switch (delegate.getType()) {
//			case BOX:
//				buildBox(surfaces);
//				break;
//			case CYLINDER:
//				buildCylinder(surfaces);
//				break;
//			case SPHERE:
//				buildSphere(surfaces);
//				break;
//			case RING:
//			    buildRing(surfaces);
//			    break;
//			case PLANE:
//			    buildPlane(surfaces);
//			    break;
//			case STL:
//				buildSTL(surfaces);
//				break;
//			case REGION:
//			case SOLID:
//				buildRegion(surfaces);
//				break;
//	
//			default:
//				break;
//		}
	}
	
//	private void buildBox(Surface... surfaces) {
//	    for (Surface surface : surfaces) {
//			if (changeGeometry) surface.buildGeometryDictionary(geometriesPanel.getBoxDictionary());
//	        if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//	        if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//	        if (changeLayer) surface.buildLayerDictionary(layerDict);
//	        if (changeZone) surface.buildZoneDictionary(zoneDict);
//	    }
//	}
//
//	private void buildSTL(Surface... surfaces) {
//		for (Surface surface : surfaces) {
//			if (changeGeometry) 
//				surface.buildGeometryDictionary(geometriesPanel.getSTLDictionary());
//			if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//			if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//			if (changeLayer) surface.buildLayerDictionary(layerDict);
//			if (changeZone) surface.buildZoneDictionary(zoneDict);
//		}
//	}
//
//	private void buildRegion(Surface... surfaces) {
//		for (Surface surface : surfaces) {
//			if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//			if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//			if (changeLayer) surface.buildLayerDictionary(layerDict);
////			if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
//		}
//	}
//
//	private void buildCylinder(Surface... surfaces) {
//		for (Surface surface : surfaces) {
//			if (changeGeometry) surface.buildGeometryDictionary(geometriesPanel.getCylinderDictionary());
//			if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//			if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//			if (changeLayer) surface.buildLayerDictionary(layerDict);
//			if (changeZone) surface.buildZoneDictionary(zoneDict);
//		}
//	}
//
//	private void buildSphere(Surface... surfaces) {
//		for (Surface surface : surfaces) {
//			if (changeGeometry) surface.buildGeometryDictionary(geometriesPanel.getSphereDictionary());
//			if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//			if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//			if (changeLayer) surface.buildLayerDictionary(layerDict);
//			if (changeZone) surface.buildZoneDictionary(zoneDict);
//		}
//	}
//
//	private void buildRing(Surface... surfaces) {
//	    for (Surface surface : surfaces) {
//	        if (changeGeometry) surface.buildGeometryDictionary(geometriesPanel.getRingDictionary());
//	        if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//	        if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//	        if (changeLayer) surface.buildLayerDictionary(layerDict);
//	        if (changeZone) surface.buildZoneDictionary(zoneDict);
//	    }
//	}
//
//	private void buildPlane(Surface... surfaces) {
//	    for (Surface surface : surfaces) {
//	        if (changeGeometry) surface.buildGeometryDictionary(geometriesPanel.getPlaneDictionary());
//	        if (changeSurface) surface.buildSurfaceDictionary(surfaceDict);
//	        if (changeVolume) surface.buildVolumeDictionary(volumeDict);
//	        if (changeLayer) surface.buildLayerDictionary(layerDict);
//	        if (changeZone) surface.buildZoneDictionary(zoneDict);
//	    }
//	}
}

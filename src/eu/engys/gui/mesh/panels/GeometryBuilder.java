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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.Type;
import eu.engys.util.Util;
import eu.engys.util.ui.TreeUtil;

public class GeometryBuilder {

	private static final Logger logger = LoggerFactory.getLogger(GeometryBuilder.class);
	
	private GeometriesPanelBuilder geometriesPanel;
	private DictionaryModel surfaceModel;
	private DictionaryModel volumeModel;
	private DictionaryModel layerModel;
	private DictionaryModel zoneModel;
	private boolean changeSurface;
	private boolean changeVolume;
	private boolean changeLayer;
	private boolean changeZone;
	
	public GeometryBuilder(GeometriesPanelBuilder geometriesPanel, DictionaryModel surfaceModel, DictionaryModel volumeModel, DictionaryModel layerModel, DictionaryModel zoneModel) {
		this.geometriesPanel = geometriesPanel;
		this.surfaceModel = surfaceModel;
		this.volumeModel = volumeModel;
		this.layerModel = layerModel;
		this.zoneModel = zoneModel;
	}

	public void buildSurfaces(Surface... surfaces) {
		if (Util.isVarArgsNotNull(surfaces)) {
			Type type = surfaces[0].getType();
			Class<?> typeClass = surfaces[0].getClass();

			if (TreeUtil.isConsistent(surfaces, typeClass)) {
				_buildSurfaces(type, surfaces);
			} else {
				logger.warn("Inconsistent selection");
			}
		}
	}

	private void _buildSurfaces(Type type, Surface... surfaces) {
		changeSurface = !surfaces[0].getSurfaceDictionary().equals(surfaceModel.getDictionary());
		changeVolume = !surfaces[0].getVolumeDictionary().equals(volumeModel.getDictionary());
		changeLayer = !surfaces[0].getLayerDictionary().equals(layerModel.getDictionary());
		changeZone = !surfaces[0].getZoneDictionary().equals(zoneModel.getDictionary());
		
		switch (type) {
			case BOX:
				buildBox(surfaces);
				break;
			case CYLINDER:
				buildCylinder(surfaces);
				break;
			case SPHERE:
				buildSphere(surfaces);
				break;
			case RING:
			    buildRing(surfaces);
			    break;
			case PLANE:
			    buildPlane(surfaces);
			    break;
			case STL:
				buildSTL(surfaces);
				break;
			case REGION:
			case SOLID:
				buildRegion(surfaces);
				break;
	
			default:
				break;
		}
	}

	private void buildSTL(Surface... surfaces) {
		for (Surface surface : surfaces) {
			if (surfaces.length == 1) 
				surface.buildGeometryDictionary(geometriesPanel.getSTLDictionary());
			if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
			if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
			if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
			if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
		}
	}

	private void buildRegion(Surface... surfaces) {
		for (Surface surface : surfaces) {
			if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
			if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
			if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
//			if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
		}
	}

	private void buildBox(Surface... surfaces) {
		for (Surface surface : surfaces) {
			if (surfaces.length == 1)
				surface.buildGeometryDictionary(geometriesPanel.getBoxDictionary());
			if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
			if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
			if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
			if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
		}
	}

	private void buildCylinder(Surface... surfaces) {
		for (Surface surface : surfaces) {
			if (surfaces.length == 1)
				surface.buildGeometryDictionary(geometriesPanel.getCylinderDictionary());
			if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
			if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
			if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
			if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
		}
	}

	private void buildSphere(Surface... surfaces) {
		for (Surface surface : surfaces) {
			if (surfaces.length == 1)
				surface.buildGeometryDictionary(geometriesPanel.getSphereDictionary());
			if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
			if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
			if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
			if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
		}
	}

	private void buildRing(Surface... surfaces) {
	    for (Surface surface : surfaces) {
	        if (surfaces.length == 1)
	            surface.buildGeometryDictionary(geometriesPanel.getRingDictionary());
	        if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
	        if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
	        if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
	        if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
	    }
	}

	private void buildPlane(Surface... surfaces) {
	    for (Surface surface : surfaces) {
	        if (surfaces.length == 1)
	            surface.buildGeometryDictionary(geometriesPanel.getPlaneDictionary());
	        if (changeSurface) surface.buildSurfaceDictionary(surfaceModel.getDictionary());
	        if (changeVolume) surface.buildVolumeDictionary(volumeModel.getDictionary());
	        if (changeLayer) surface.buildLayerDictionary(layerModel.getDictionary());
	        if (changeZone) surface.buildZoneDictionary(zoneModel.getDictionary());
	    }
	}
}

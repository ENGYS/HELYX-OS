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
package eu.engys.core.project.zero.cellzones;

import eu.engys.core.project.Model;
import eu.engys.core.project.constant.MRFProperties;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.FvOptions;

public interface CellZonesBuilder {

    public void loadMRFDictionary(Model model);
    public void loadMRFDictionary(CellZones cellZones, FvOptions fvOptions, MRFProperties mrfProperties);
    public void saveMRFDictionary(CellZones cellZones, FvOptions fvOptions, MRFProperties mrfProperties);
    public void saveMRFDictionary(Model model);
    
    public void loadPorousDictionary(CellZones cellZones, FvOptions fvOptions);
    public void loadPorousDictionary(Model model);
    public void savePorousDictionary(CellZones cellZones, FvOptions fvOptions);
    public void savePorousDictionary(Model model);
    
    
    public void loadThermalDictionary(Model model);
    public void loadThermalDictionary(CellZones cellZones, FvOptions fvOptions, State state);
    public void saveThermalDictionary(CellZones cellZones, FvOptions fvOptions, State state);
    public void saveThermalDictionary(Model model);

    public void loadHeatExchangerDictionary(Model model);
    public void loadHeatExchangerDictionary(CellZones cellZones, FvOptions fvOptions);
    public void saveHeatExchangerDictionary(CellZones cellZones, FvOptions fvOptions);
    public void saveHeatExchangerDictionary(Model model);
    
}

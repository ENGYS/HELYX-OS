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


package eu.engys.core.project.zero.cellzones;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.FvOptions;
import eu.engys.util.IOUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class CellZonesReader {

    private static Logger logger = LoggerFactory.getLogger(CellZones.class);
    private Set<ApplicationModule> modules;
    private ProgressMonitor monitor;
    private Model model;
    private CellZonesBuilder builder;

    public CellZonesReader(Model model, CellZonesBuilder builder, Set<ApplicationModule> modules, ProgressMonitor monitor) {
        this.model = model;
        this.builder = builder;
        this.modules = modules;
        this.monitor = monitor;
    }

    public CellZones read(File... cellZonesFiles) {
        CellZones cellZones = readCellZoneFiles(cellZonesFiles);
        readCellZoneTypeAndDictionary(cellZones);
        return cellZones;
    }

    public CellZones readCellZoneFiles(File... cellZonesFiles) {
        CellZones cellZones = new CellZones();
        List<CellZone> zones = null;
        if (cellZonesFiles.length == 1) {
            zones = readCellZones(cellZonesFiles[0]);
        } else {
            zones = readParallelCellZones(cellZonesFiles);
        }
        cellZones.addAll(zones);
        return cellZones;
    }

    private void readCellZoneTypeAndDictionary(CellZones cellZones) {
        FvOptions fvOptions = model.getProject().getSystemFolder().getFvOptions();
        builder.loadMRFDictionary(cellZones, fvOptions);
        builder.loadPorousDictionary(cellZones, fvOptions);
        builder.loadThermalDictionary(cellZones, fvOptions, model.getState());
        ModulesUtil.updateCellZonesFromModel(modules, cellZones);
    }

    private List<CellZone> readParallelCellZones(File[] cellZonesFiles) {
        final List<CellZone> zones = Collections.synchronizedList(new ArrayList<CellZone>());
        Runnable[] runnables = new Runnable[cellZonesFiles.length];
        for (int i = 0; i < cellZonesFiles.length; i++) {
            final File cellZoneFile = cellZonesFiles[i];
            runnables[i] = new Runnable() {
                @Override
                public void run() {
                    merge(zones, readCellZones(cellZoneFile));
                }
            };
        }
        ExecUtil.execParallelAndWait(runnables);
        return zones;
    }

    private void merge(List<CellZone> zones, List<CellZone> readZones) {
        for (CellZone zone : readZones) {
            if (!zones.contains(zone)) {
                zones.add(zone);
            }
        }
    }

    private List<CellZone> readCellZones(File cellZones) throws IllegalStateException {
        monitor.setCurrent(null, monitor.getCurrent() + 1, 2);
        logger.info("READ: CellZones {}", cellZones.getAbsolutePath());
        List<CellZone> zones = new ArrayList<CellZone>();

        if (cellZones.exists()) {
            try {
                String cellZonesString = IOUtils.readStringFromFile(cellZones);
                // remove comments
                cellZonesString = cellZonesString.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");

                Pattern pattern = Pattern.compile("(\\d+)\\s*\\(");
                Matcher matcher = pattern.matcher(cellZonesString);

                if (matcher.find()) {

                    if (matcher.groupCount() == 1) {
                        String nZones = matcher.group(1);

                        Pattern patternForType = Pattern.compile("([\\S]+)\\s*\\{\\s*type\\s*(\\w+);");
                        Matcher matcherForType = patternForType.matcher(cellZonesString);
                        int zonesCounter = 0;
                        while (matcherForType.find()) {
                            zonesCounter++;
                            if (matcherForType.groupCount() == 2) {
                                String zoneName = matcherForType.group(1);
                                String zoneType = matcherForType.group(2);
                                
                                CellZone cz = new CellZone(zoneName);
                                cz.setName(zoneName);
                                cz.setVisible(true);

                                zones.add(cz);
                            }
                        }
                        if (Integer.parseInt(nZones) != zonesCounter) {
                            monitor.error(String.format("Number of read patches (%d) is invalid (expected %d).", zonesCounter, nZones), 2);
                        }
                    }
                }
            } catch (Exception e) {
                monitor.warning("Cannot read the file: " + e.getMessage(), 2);
                logger.warn("Cannot read the file", e);
            }
        } else {
            monitor.warning("File does not exist", 2);
            logger.warn("CellZones file does not exist");
        }

        return zones;
    }
}

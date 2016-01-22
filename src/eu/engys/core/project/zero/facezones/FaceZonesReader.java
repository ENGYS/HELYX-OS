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


package eu.engys.core.project.zero.facezones;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.IOUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class FaceZonesReader {

    private static Logger logger = LoggerFactory.getLogger(FaceZones.class);
    private ProgressMonitor monitor;

    public FaceZonesReader(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public FaceZones read(File... faceZonesFiles) {
        FaceZones faceZones = new FaceZones();
        if (faceZonesFiles.length == 1) {
            faceZones.addAll(readFaceZones(faceZonesFiles[0]));
        } else {
            faceZones.addAll(readParallelCellZones(faceZonesFiles));
        }
        
        return faceZones;
    }

    private List<FaceZone> readParallelCellZones(File[] faceZonesFiles) {
        final List<FaceZone> zones = Collections.synchronizedList(new ArrayList<FaceZone>());
        Runnable[] readZonesRunnables = new Runnable[faceZonesFiles.length];
        for (int i = 0; i < faceZonesFiles.length; i++) {
            final File faceZoneFile = faceZonesFiles[i];
            readZonesRunnables[i] = new Runnable() {
                @Override
                public void run() {
                    merge(zones, readFaceZones(faceZoneFile));
                }
            };
        }
        ExecUtil.execParallelAndWait(readZonesRunnables);
        return zones;
    }

    private void merge(Collection<FaceZone> zones, List<FaceZone> readZones) {
        for (FaceZone zone : readZones) {
            if (!zones.contains(zone)) {
                zones.add(zone);
            }
        }
    }

    private List<FaceZone> readFaceZones(File faceZones) throws IllegalStateException {
        monitor.setCurrent(null, monitor.getCurrent() + 1, 2);
        logger.info("READ: FaceZones {}", faceZones.getAbsolutePath());
        List<FaceZone> zones = new ArrayList<FaceZone>();

        if (faceZones.exists()) {

            try {
                String cellZonesString = IOUtils.readStringFromFile(faceZones);

                cellZonesString = cellZonesString.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");// rimuovo
                                                                                             // i
                                                                                             // commenti

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

                                FaceZone cz = new FaceZone(zoneName);
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
            logger.warn("FaceZones file does not exist");
        }

        return zones;
    }
}

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
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.DictionaryWriter;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.FvOptions;
import eu.engys.util.IOUtils;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;

public class CellZonesWriter {

    private static final Logger logger = LoggerFactory.getLogger(CellZones.class);

    private ProgressMonitor monitor;

    private Set<ApplicationModule> modules;

    private CellZonesBuilder builder;

    public CellZonesWriter(CellZonesBuilder builder, Set<ApplicationModule> modules, ProgressMonitor monitor) {
        this.builder = builder;
        this.modules = modules;
        this.monitor = monitor;
    }

    public void writeFvOptions(Model model) {
        FvOptions fvOptions = model.getProject().getSystemFolder().getFvOptions();
        if(fvOptions != null){
            fvOptions.clear();
        }
        builder.saveMRFDictionary(model);
        builder.savePorousDictionary(model);
        builder.saveThermalDictionary(model);
        ModulesUtil.updateModelFromCellZones(modules);
    }

    public void writeCellZoneFiles(Model model, File... cellZonesFiles) {
        final CellZones cellZones = model.getCellZones();
        Runnable[] runnables = new Runnable[cellZonesFiles.length];
        for (int i = 0; i < cellZonesFiles.length; i++) {
            final File cellZoneFile = cellZonesFiles[i];
            runnables[i] = new Runnable() {
                public void run() {
                    writeCellZones(cellZones, cellZoneFile);
                }
            };
        }
        ExecUtil.execSerial(runnables);
    }

    private void writeCellZones(CellZones cellZones, File cellZonesFile) {
        monitor.setCurrent(null, monitor.getCurrent() + 1, 2);
        logger.info("WRITE: CellZones {}", cellZonesFile.getAbsolutePath());

        Map<String, String> zonesOriginalNames = new HashMap<String, String>();
        for (CellZone zone : cellZones) {
            zonesOriginalNames.put(zone.getOriginalName(), zone.getName());
        }
        
        try {
            String cellZonesString = IOUtils.readStringFromFile(cellZonesFile);

            StringBuffer sb = new StringBuffer(cellZonesString.length());

            cellZonesString = cellZonesString.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");

            Pattern pattern = Pattern.compile("(\\d+)\\s*?\\(((?:[\\s\\S])*?\\s*?\\})\\s*?\\)");
            Matcher matcher = pattern.matcher(cellZonesString);

            if (matcher.find()) {
                if (matcher.groupCount() == 2) {
                    String nZones = matcher.group(1);
                    String zonesString = matcher.group(2);

                    FoamFile foamFile = FoamFile.getDictionaryFoamFile("regIOobject", "\"0/polyMesh\"", "cellZones");
                    new DictionaryWriter(foamFile).writeDictionary(sb, "");

                    sb.append(nZones + "(");
                    Pattern patternForType = Pattern.compile("([\\S]+)\\s*?\\{\\s*?type\\s*?(\\w+);");
                    Matcher matcherForType = patternForType.matcher(zonesString);

                    while (matcherForType.find()) {
                        String originalName = matcherForType.group(1);
                        String newName = zonesOriginalNames.get(originalName);
                        String replacement = newName + "\n    {\n        type cellZone;";
                        matcherForType.appendReplacement(sb, replacement);
                    }
                    matcherForType.appendTail(sb);
                    sb.append(")");
                }

                FileWriter outStream = new FileWriter(cellZonesFile);
                outStream.write(sb.toString());
                outStream.close();
            }
        } catch (Exception e) {
            monitor.warning("Error writing cell zones file " + e.getMessage());
            logger.warn("Error writing cell zones file {}", e.getMessage());
        }
    }
}

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
package eu.engys.gui;

import static eu.engys.core.OpenFOAMEnvironment.loadEnvironment;
import static eu.engys.core.OpenFOAMEnvironment.printHeader;
import static eu.engys.core.OpenFOAMEnvironment.printVariables;
import static eu.engys.util.OpenFOAMCommands.CHECK_MESH_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.CHECK_MESH_SERIAL;
import static eu.engys.util.OpenFOAMCommands.POTENTIAL_FOAM_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.POTENTIAL_FOAM_SERIAL;
import static eu.engys.util.OpenFOAMCommands.SET_FIELDS_PARALLEL;
import static eu.engys.util.OpenFOAMCommands.SET_FIELDS_SERIAL;

import java.util.List;

import javax.inject.Inject;

import eu.engys.core.controller.DefaultScriptFactory;
import eu.engys.core.controller.ScriptBuilder;
import eu.engys.core.project.Model;

public class StandardScriptFactory extends DefaultScriptFactory {

    public static final String WRAPPER_PARALLEL_WORKS = "Parallel Works";

    @Inject
    public StandardScriptFactory(Model model) {
        super(model);
    }

    @Override
    protected boolean performBlockMesh() {
        return true;
    }

    @Override
    protected List<String> getSerialCheckMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, CHECK_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(CHECK_MESH_SERIAL());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelCheckMeshScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, CHECK_MESH);
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.append(CHECK_MESH_PARALLEL());
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getSerialInitialiseScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, INITIALISE_FIELDS.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.appendIf(model.getFields().hasCellSetInitialisationField(), SET_FIELDS_SERIAL());
        sb.appendIf(model.getFields().hasPotentialFlowInitialisationField(), POTENTIAL_FOAM_SERIAL(model.getFields().hasInitialiseUBCsField()));
        sb.newLine();
        return sb.getLines();
    }

    @Override
    protected List<String> getParallelInitialiseScript() {
        ScriptBuilder sb = new ScriptBuilder();
        printHeader(sb, INITIALISE_FIELDS.toUpperCase());
        printVariables(sb);
        loadEnvironment(sb);
        sb.newLine();
        sb.appendIf(model.getFields().hasCellSetInitialisationField(), SET_FIELDS_PARALLEL());
        sb.appendIf(model.getFields().hasPotentialFlowInitialisationField(), POTENTIAL_FOAM_PARALLEL(model.getFields().hasInitialiseUBCsField()));
        sb.newLine();
        return sb.getLines();
    }

}

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
package eu.engys.launcher;

import static eu.engys.launcher.StartUpMonitor.info;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.Icon;

import com.google.inject.Injector;
import com.google.inject.Module;

import eu.engys.application.Application;
import eu.engys.application.Batch;
import eu.engys.core.Arguments;
import eu.engys.launcher.modules.Modules;

public abstract class AbstractApplicationLauncher implements ApplicationLauncher {

    private Injector injector;

    @Inject
    public AbstractApplicationLauncher(Injector injector) {
        this.injector = injector;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public abstract void checkLicense();
    
    @Override
    public void launch(Arguments arguments) throws Exception {
        info("Loading application modules");
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        List<Module> modules = new ArrayList<>();
        modules.addAll(Modules.load3DModules(null, arguments.no3D));
        modules.addAll(Modules.loadApplicationModules(url));

        info("Starting Modules");
        Injector appInjector = injector.createChildInjector(modules);

        info("Loading Application");
        Application application = appInjector.getInstance(Application.class);

        info("Layout Application");
        application.start(arguments);
    }

    @Override
    public void batch(Arguments arguments) throws Exception {
        info("Loading modules");
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        Iterable<Module> modules = Modules.loadBatchModules(url);

        info("Starting modules");
        Injector appInjector = injector.createChildInjector(modules);

        info("Loading application");
        Batch batch = appInjector.getInstance(Batch.class);

        info("Launch Batch");
        batch.start(arguments);
    }
}

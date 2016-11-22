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

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import eu.engys.application.Application;
import eu.engys.application.Batch;
import eu.engys.core.Arguments;
import eu.engys.launcher.modules.Modules;
import eu.engys.suite.Suite;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.LoggerUtil;
import eu.engys.util.Util;
import eu.engys.util.plaf.ILookAndFeel;
import eu.engys.util.ui.UiUtil;

public class Launcher {
    
    public static void main(String[] args) throws Exception {
        
        info("Initialize Arguments");
        Arguments arguments = new Arguments();
        arguments.parse(args);
        
        info("Set Application Info");
        ApplicationInfo.init();
        
        printOut(ApplicationInfo.getHeaderInfo());
        
    	info("Initialize Logger");
    	if (arguments.isBatch()) 
    		LoggerUtil.initFlatLogger(arguments.logLevel);
    	else
    		LoggerUtil.initLogger(arguments.logLevel);
    	
    	info("Initialize Locale");
    	LocaleUtil.initLocale();
    	
    	info("Initialize Script");
    	Util.initScriptStyle();

    	info("Loading modules");
    	List<Module> modules = Modules.loadSuiteModules();
    	
    	if (modules.isEmpty()) {
    		if (arguments.isBatch() ) {
    			
    			modules = Modules.loadBatchModules(null);

    			info("Starting modules");
    			Injector injector = Guice.createInjector(modules);
    			
    			info("Go to Batch");
    	        Batch batch = injector.getInstance(Batch.class);
    	        
    	        info("Check License");
                batch.checkLicense();

    	        info("Launch Batch");
    	        batch.start(arguments);
    		} else {
    		    
    		    modules.addAll(Modules.load3DModules(null, arguments.no3D));
    			modules.addAll(Modules.loadApplicationModules(null));
    			
    			info("Starting modules");
    			Injector injector = Guice.createInjector(modules);
    			
    			info("Loading LookAndFeel");
    			ILookAndFeel laf = injector.getInstance(ILookAndFeel.class);
    			laf.init();
    			
    			ApplicationLauncher launcher = injector.getInstance(ApplicationLauncher.class);

    			info("Check License");
    			launcher.checkLicense();

    			info("Loading Application");
    			Application application = injector.getInstance(Application.class);
    			
    			UiUtil.renameUIThread();
    			
    			info("Start Application");
    			application.start(arguments);
    		}
    	} else {
    		info("Starting modules");
    		Injector injector = Guice.createInjector(modules);
    		
    		info("Loading LookAndFeel");
    		ILookAndFeel laf = injector.getInstance(ILookAndFeel.class);
    		laf.init();
    		
    		info("Loading Suite");
    		Suite suite = injector.getInstance(Suite.class);
    		
    		if (arguments.isBatch() ) {
    			info("Go to Batch");
    			suite.batch(arguments);
    		} else {
    			info("Start Suite");
    			UiUtil.installExceptionHandler();
    			UiUtil.renameUIThread();
    			suite.launch(arguments);
    		}
    	}
    }


    private static void printOut(String msg) {
	    System.out.println(msg);
    }

}


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


package eu.engys.launcher;

import static eu.engys.launcher.StartUpMonitor.info;

import java.util.List;

import javax.swing.SwingUtilities;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import eu.engys.application.Application;
import eu.engys.application.Batch;
import eu.engys.core.Arguments;
import eu.engys.core.LoggerUtil;
import eu.engys.launcher.modules.Modules;
import eu.engys.suite.Suite;
import eu.engys.util.ApplicationInfo;
import eu.engys.util.Util;
import eu.engys.util.plaf.ILookAndFeel;
import eu.engys.util.ui.UiUtil;

public class Launcher {
    
    public static void main(String[] args) throws Exception {

        info("Set Application Info");
        ApplicationInfo.init();
        
        printOut(ApplicationInfo.getHeaderInfo());

        info("Initialize Arguments");
    	Arguments.init(args);
        
    	info("Initialize Logger");
    	if (Arguments.isBatch()) 
    		LoggerUtil.initFlatLogger();
    	else
    		LoggerUtil.initLogger();
    	
    	info("Initialize Locale");
    	LocaleUtil.initLocale();
    	
    	Util.initScriptStyle();

    	info("Loading modules");
    	List<Module> modules = Modules.loadSuiteModules();
    	
    	if (modules.isEmpty()) {
    		if (Arguments.isBatch() ) {
    			modules = Modules.loadBatchModules(null);

    			info("Starting modules");
    			Injector injector = Guice.createInjector(modules);
    			
    			info("Go to Batch");
    	        Batch batch = injector.getInstance(Batch.class);

    	        info("Launch Batch");
    	        batch.run();
    		} else {
    			modules = Modules.loadApplicationModules(null);
    			
    			info("Starting modules");
    			Injector injector = Guice.createInjector(modules);
    			
    			info("Loading LookAndFeel");
    			ILookAndFeel laf = injector.getInstance(ILookAndFeel.class);
    			laf.init();
    			
    			info("Check License");
    			ApplicationLauncher launcher = injector.getInstance(ApplicationLauncher.class);
    			launcher.checkLicense();

    			info("Loading Application");
    			Application application = injector.getInstance(Application.class);

    			UiUtil.renameUIThread();
    			
    			info("Layout Application");
    			SwingUtilities.invokeLater(application);
    		}
    	} else {
    		info("Starting modules");
    		Injector injector = Guice.createInjector(modules);
    		
    		info("Loading LookAndFeel");
    		ILookAndFeel laf = injector.getInstance(ILookAndFeel.class);
    		laf.init();
    		
    		info("Loading Suite");
    		Suite suite = injector.getInstance(Suite.class);
    		
    		if (Arguments.isBatch() ) {
    			info("Go to Batch");
    			suite.batch();
    		} else {
    			info("Start Suite");
    			UiUtil.installExceptionHandler();
    			UiUtil.renameUIThread();
    			suite.launch();
    		}
    	}
    }

	private static void printOut(String msg) {
	    System.out.println(msg);
    }

}


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


package eu.engys.launcher.modules;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

public class Modules {

	private static final Logger logger = LoggerFactory.getLogger(Modules.class);
	
	private static ClassLoader classLoader;
	private static String packageName;
	private static Class<?> interfase;

	private static URL url;
	
	public static List<Module> loadSuiteModules() {
		return loadModules("eu.engys.suite.modules", Module.class, null);
	}
	
	public static List<Module> loadApplicationModules(URL url) {
		return loadModules("eu.engys.application.modules", Module.class, url);
	}
	
	public static List<Module> loadBatchModules(URL url) {
		return loadModules("eu.engys.batch.modules", Module.class, url);
	}

	static <M> List<M> loadModules(String pkgName, Class<M> interfaceClass, URL classURL) {
		List<M> modules = new ArrayList<M>();
		
		interfase = interfaceClass;
		packageName = pkgName;
		classLoader = Thread.currentThread().getContextClassLoader();
		url = classURL;
		
		try {
			List<Class<? extends M>> classes = findAllImplementationsInPackage();
    	
			for(Class<? extends M> clazz : classes) {
				try {
					M newInstance = clazz.newInstance();
					logger.info("[Modules] {} loaded", clazz.getName());
					modules.add(newInstance);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return modules;
	}
	
	public static <M> List<Class<? extends M>> findAllImplementationsInPackage() throws IOException, ClassNotFoundException {
		List<Class<? extends M>> implementations = new ArrayList<Class<? extends M>>();
		
		List<Class<? extends M>> classes = getClassesFromPackage(packageName);
		for (Class<? extends M> klass : classes) {
			if (interfase.isAssignableFrom(klass) && !klass.isInterface()){ 
				implementations.add(klass);
			} else {
				logger.info("[Modules] {} is not valid.", klass.getName());
			}
		}
		return implementations;
	}

	@SuppressWarnings("unchecked")
    private static <M> List<Class<? extends M >> getClassesFromPackage(String packageName) throws ClassNotFoundException, IOException {
		List<String> classNames = getClassNamesFromPackage(packageName);

		List<Class<? extends M>> classes = new ArrayList<Class<? extends M >>();
	    for (String className : classNames) {
	    	try {
	    		classes.add((Class<M>) Class.forName(className));
			} catch (ClassNotFoundException e) {
				logger.error("[Modules] Error Loading class {}", className);
			}
	    }
	    return classes;
	}

	
	public static List<String> getClassNamesFromPackage(String packageName) throws IOException {
		assert classLoader != null;
		
		List<String> names = new ArrayList<String>();

		packageName = packageName.replace(".", "/");
		Enumeration<URL> packageURLs = classLoader.getResources(packageName);

		for(URL packageURL : Collections.list(packageURLs)) {
			if (packageURL.getProtocol().equals("jar")) {
				// build jar file name
				String jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
				jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
				if (url == null ){
					names.addAll(extractClassName(packageName, jarFileName));
				} else {
					String parentJarFileName = URLDecoder.decode(url.getFile(), "UTF-8");
					
					if (jarFileName.startsWith(parentJarFileName)){
						names.addAll(extractClassName(packageName, jarFileName));
					} else {
						//logger.error("[Modules] Jar {} is not a child of {}. Not loaded.", jarFileName, parentJarFileName);
					}
				}
			} else {
				if (url == null ) {
					File folder = new File(packageURL.getFile());
					File[] contenuti = folder.listFiles();
					String entryName;
					for (File actual : contenuti) {
						entryName = actual.getName();
						entryName = entryName.substring(0, entryName.lastIndexOf('.'));
						entryName = packageName+"/"+entryName;
						entryName = entryName.replace("/", ".");
						names.add(entryName);
					}
				}
			}
		}
		return names;
	}

	static List<String> extractClassName(String packageName, String jarFileName) throws IOException {
		List<String> entryNames = new ArrayList<String>();
		Enumeration<JarEntry> jarEntries;
		String entryName = "";
		logger.info("[Modules] Looking for Modules in file {}", jarFileName);
		
		JarFile jf = new JarFile(jarFileName);
		jarEntries = jf.entries();
		while (jarEntries.hasMoreElements()) {
			entryName = jarEntries.nextElement().getName();
			if (entryName.startsWith(packageName) && entryName.length() > packageName.length() + 5) {
				entryName = entryName.substring(0, entryName.lastIndexOf('.'));
				entryName = entryName.replace("/", ".");
				logger.info("[Modules] Module {} found.", entryName);
				entryNames.add(entryName);
			}
		}
		jf.close();
		return entryNames;
	}
	
}


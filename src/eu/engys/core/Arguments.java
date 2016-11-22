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
package eu.engys.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.log4j.Level;

import eu.engys.util.ApplicationInfo;
import eu.engys.util.Util;

public class Arguments {

	private static final String LINE = " ********************************";
	private static final String TAB = "    ";

	public boolean verbose = false;
	public boolean no3D = false;
	public boolean load3Dmesh = true;
	public boolean load3Dgeometry = true;
	public Level logLevel = Level.ERROR;

	public File baseDir = null;
	public File studyDir = null;

	public boolean mesh = false;
	public boolean run = false;
	public boolean setup = false;
	public boolean all = false;
	public boolean initialise = false;
	public boolean post = false;

	public int concurrentDesigns = 1;

	public boolean server = false;
	public File[] importFiles = null;

	private static final String OPTION_V = "-v";
	private static final String OPTION_VV = "-V";
	private static final String OPTION_HELP = "-help";
	private static final String OPTION_NO3D = "-no3D";
	private static final String OPTION_CASE = "-case";
	private static final String OPTION_STUDY = "-study";

	public static final String OPTION_MESH = "-mesh";
	public static final String OPTION_RUN = "-run";
	public static final String OPTION_SETUP = "-setup";
	public static final String OPTION_ALL = "-all";
	public static final String OPTION_INITIALISE = "-initialise";
	public static final String OPTION_POST = "-post";
	public static final String OPTION_MEMORY = "-memory=<size>";

	public static final String OPTION_CONCURRENT_DESIGNS = "-concurrent";

	private static final String OPTION_SERVER = "-server"; /* INTERNAL */
	private static final String OPTION_IMPORT = "-import"; /* INTERNAL */
	private static final String OPTION_NOMESH = "-no3Dmesh"; /* INTERNAL */
	private static final String OPTION_NOGEOM = "-no3DGeom"; /* INTERNAL */

	//    private static final String OPTION_CORE = "-core"; /* INTERNAL */
	//    private static final String OPTION_TIMEOUT = "-timeout"; /* INTERNAL */

	public void parse(final String[] argv) {
		for (int i = 0; i < argv.length; i++) {
			final String arg = argv[i];

			if (arg.charAt(0) == '-') {
				switch (arg) {
                case OPTION_V: logLevel = Level.INFO; verbose = true; break;
                case OPTION_VV: logLevel = Level.DEBUG; verbose = true; break;
                case OPTION_NO3D: no3D = true; break;

				case OPTION_CASE:
					if (i == argv.length - 1) {
						fatal("Missing case folder");
						printUsage();
						exit(-1);
					}
					String baseDirPath = argv[++i];
					try {
						/*
						 * Use toRealPath to avoid bad path display when using "." and ".."
						 */
						File baseDir = Paths.get(baseDirPath).toRealPath().toFile();
						if (!baseDir.exists()) {
							warning("Case Folder \"" + baseDir.getAbsolutePath() + "\" Does Not Exist!");
						} else {
							this.baseDir = baseDir;
						}
					} catch (IOException e) {
						warning(e.getMessage());
					}

					break;
				case OPTION_STUDY:
					if (i == argv.length - 1) {
						fatal("Missing study folder");
						printUsage();
						exit(-1);
					}
					String studyDirPath = argv[++i];
					final File studyDir = new File(studyDirPath);
					if (!studyDir.exists()) {
						warning("Case Folder \"" + studyDir.getAbsolutePath() + "\" Does Not Exist!");
					} else {
						this.studyDir = studyDir;
					}
					break;

                case OPTION_MESH: this.mesh = true; break;
                case OPTION_RUN: this.run = true; break;
                case OPTION_SETUP: this.setup = true; break;
                case OPTION_ALL: this.all = true; break;
                case OPTION_INITIALISE: this.initialise = true; break;
                case OPTION_POST: this.post = true; break;
                case OPTION_SERVER: this.server = true; break;

				case OPTION_CONCURRENT_DESIGNS:
					if (i == argv.length - 1) {
						fatal("Missing number of concurrent designs folder");
						printUsage();
						exit(-1);
					}
					String designs = argv[++i];
					try {
						int nDesigns = Integer.parseInt(designs);
						this.concurrentDesigns = nDesigns;
					} catch (NumberFormatException nfe) {
						warning("Invalid number of concurrent designs");
					}
					break;
                case OPTION_NOGEOM: load3Dgeometry = false; break;
                case OPTION_NOMESH: load3Dmesh = false; break;

				case OPTION_IMPORT:
					if (i == argv.length - 1) {
						fatal("Missing import file/folder");
						printUsage();
						exit(-1);
					}
					String importPath = argv[++i];
					final File importFolder = new File(importPath);
					if (!importFolder.exists()) {
						fatal("Import Folder Does Not Exist!");
						exit(-1);
					} else if (importFolder.isFile()) {
						this.importFiles = new File[] { importFolder };
					} else {
						File[] stls = importFolder.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith("stl") || name.endsWith("STL");
							}
						});
						if (stls.length == 0) {
							fatal("No files to import!");
							exit(-1);
						} else {
							this.importFiles = stls;
						}
					}
					break;
				case OPTION_HELP:
					printUsage();
					exit(0);
					break;
				default:
					fatal("Unknown Option " + arg);
					printUsage();
					exit(0);
					break;
				}
			}
		}
		checkCaseOrStudy();
	}

	private void checkCaseOrStudy() {
		if (hasCommand()) {
			if (baseDir == null && studyDir == null) {
				fatal("Missing case (or study) folder");
				printUsage();
				exit(-1);
			}
		}
	}

	private static void fatal(String msg) {
		System.err.println();
		System.err.println(LINE);
		System.err.println(" " + TAB + "FATAL ERROR:");
		System.err.println(" " + TAB + TAB + msg);
		System.err.println(LINE);
	}

	private static void warning(String msg) {
		System.err.println();
		System.err.println(LINE);
		System.err.println(" " + TAB + "WARNING:");
		System.err.println(" " + TAB + TAB + msg);
		System.err.println(TAB + LINE);
	}

	private static void exit(int status) {
		System.exit(status);
	}

	private static void printUsage() {

		System.err.println(" USAGE");
		System.err.println(" -----------------------------------------------------");
		if (Util.isWindows())
			System.err.println("     " + ApplicationInfo.getName() + ".bat [" + OPTION_V + "] [" + OPTION_VV + "] [-case <folder>] [command] ");
		else
			System.err.println("     " + ApplicationInfo.getName() + ".sh [" + OPTION_V + "] [" + OPTION_VV + "] [-case <folder>] [command] ");

		System.err.println();
		System.err.println("     Options:");
		System.err.println("       " + Util.padWithSpaces(OPTION_HELP, 12) + " Print this help screen.");
		System.err.println("       " + Util.padWithSpaces(OPTION_V, 12) + " The verbose output.");
		System.err.println("       " + Util.padWithSpaces(OPTION_VV, 12) + " The very verbose output.");
		System.err.println("       " + Util.padWithSpaces(OPTION_NO3D, 12) + " Does not display 3D window.");
		System.err.println("       " + Util.padWithSpaces(OPTION_CASE, 12) + " Specify the case directory for the application.");
		System.err.println("       " + Util.padWithSpaces(OPTION_STUDY, 12) + " Specify the study directory for the application (case manager only).");
		System.err.println("       " + Util.padWithSpaces(OPTION_CONCURRENT_DESIGNS, 12) + " Specify the number of cases to run in parallel (case manager only).");
		System.err.println();
		System.err.println("       " + Util.padWithSpaces(OPTION_MEMORY, 12) + " Set the maximum memory size. Size is expressed as '1234m' or '1g' ");
		System.err.println();
		System.err.println("     Commands:");
		System.err.println("       " + Util.padWithSpaces(OPTION_MESH, 12) + " Launch mesh creation according to system/snappyHexMeshDict.");
		System.err.println("       " + Util.padWithSpaces(OPTION_SETUP, 12) + " Setup the cfd case according to system/caseSetupDict.");
		System.err.println("       " + Util.padWithSpaces(OPTION_INITIALISE, 12) + " Initialise the fields according to system/caseSetupDict.");
		System.err.println("       " + Util.padWithSpaces(OPTION_RUN, 12) + " Launch the solver.");
		System.err.println("       " + Util.padWithSpaces(OPTION_ALL, 12) + " Launch mesh + setup + run.");
		System.err.println("       " + Util.padWithSpaces(OPTION_POST, 12) + " Extract post-processing results.");
		System.err.println(" -----------------------------------------------------");
	}

	public boolean isBatch() {
		return server || hasCommand();
	}

	private boolean hasCommand() {
		return mesh || setup || run || all || initialise || post;
	}

}

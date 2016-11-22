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

package eu.engys.core.project;

import static eu.engys.core.project.constant.ConstantFolder.CONSTANT;
import static eu.engys.core.project.system.ControlDict.CONTROL_DICT;
import static eu.engys.core.project.system.FvSchemes.FV_SCHEMES;
import static eu.engys.core.project.system.FvSolution.FV_SOLUTION;
import static eu.engys.core.project.system.SystemFolder.SYSTEM;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.project.system.DecomposeParDict;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.core.project.zero.ParallelZeroFileManager;
import eu.engys.core.project.zero.SerialZeroFileManager;
import eu.engys.core.project.zero.ZeroFolderStructure;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.progress.SilentMonitor;
import eu.engys.util.ui.UiUtil;

public class ProjectFolderAnalyzer {
	
	public static final String PARALLEL_CHOICE = "Parallel";
	public static final String SERIAL_CHOICE = "Serial";

	private static final Logger logger = LoggerFactory.getLogger(ProjectFolderAnalyzer.class);

//    parallel   parallel    serial      serial 
//    zero       constant    zero        constant
//    
//    T          T           T           T           -> A
//    T          T           T           F           -> A
//    T          T           F           T           -> P
//    T          T           F           F           -> P
//
//    T          F           T           T           -> A
//    T          F           T           F           -> A
//    T          F           F           T           -> P
//    T          F           F           F           -> P
//    
//    F          T           T           T           -> S
//    F          T           T           F           -> S
//    F          T           F           T           -> A
//    F          T           F           F           -> P
//    
//    F          F           T           T           -> S
//    F          F           T           F           -> S
//    F          F           F           T           -> S
//    F          F           F           F           -> N
    

	public enum WhenInDoubt {READ_PARALLEL, ASK_USER};
	
    private static final String PROCESSOR = "processor";

    private final File baseDir;

    private int processors;

    /* whether boundary file is in 0 or in constant */
    private boolean parallel_constant;
    private boolean parallel_constant_multiregion;
    private boolean parallel_zero_multiregion;
    private boolean parallel_zero;

    private boolean serial_constant;
    private boolean serial_zero;
    private boolean serial_constant_multiregion;
    private boolean serial_zero_multiregion;

    private final ProgressMonitor monitor;

    private WhenInDoubt wid;

    public ProjectFolderAnalyzer(File baseDir, ProgressMonitor monitor) {
        this.baseDir = baseDir;
        this.monitor = monitor;
    }

    public ProjectFolderStructure checkAll(WhenInDoubt wid) {
        this.wid = wid;
        checkSerialOrParallel();
        return populateStructure();
    }

    private ProjectFolderStructure populateStructure() {
        ProjectFolderStructure structure = new ProjectFolderStructure();
        if (parallel_zero) {
            if (serial_zero) {
            	logger.debug("Case is PARALLEL {} proc, mesh is BOTH on parallel_and_serial_zero folder", processors);
                askToUser(structure);
            } else {
            	logger.debug("Case is PARALLEL {} proc, mesh is ONLY on parallel_zero folder", processors);
                structure.setParallel(true);
                structure.setProcessors(processors);
            }
        } else {
            if (parallel_constant) {
                if (serial_zero) {
                	logger.debug("Case is SERIAL, mesh is BOTH on parallel_constant folder and serial_zero folder");
                    structure.setParallel(false);
                    structure.setProcessors(-1);
                } else {
                    if (serial_constant) {
                    	logger.debug("Case is PARALLEL {} proc, mesh is BOTH on parallel_and_serial_constant folder", processors);
                        askToUser(structure);
                    } else {
                    	logger.debug("Case is PARALLEL {} proc, mesh is on parallel_constant folder", processors);
                        structure.setParallel(true);
                        structure.setProcessors(processors);
                    }
                }
            } else {
                if (serial_zero) {
                	logger.debug("Case is SERIAL, mesh is on serial_zero folder");
                    structure.setParallel(false);
                    structure.setProcessors(-1);
                } else {
                    if (serial_constant) {
                    	logger.debug("Case is SERIAL, mesh is both on serial zero and constant folder");
                        structure.setParallel(false);
                        structure.setProcessors(-1);
                    } else {
                    	logger.debug("Looking into decomposeParDict");
                        checkIntoDecomposePar(structure);
                    }
                }
            }
        }
        logger.debug(structure.toString());
        return structure;
    }

    public ProjectFolderAnalyzer checkSerialOrParallel() {
        if (baseDir.exists() && baseDir.isDirectory()) {
            processors = findProcessorsFolders();
            if (processors > 0) {
                checkParallelBoundary();
            }

            checkSerialBoundary();
        }
        return this;
    }

    public int findProcessorsFolders() {
        File[] processorFiles = baseDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && file.getName().startsWith(PROCESSOR);
            }
        });
        int numberOfProcessors = -1;
        for (File file : processorFiles) {
            String processorIndexString = file.getName().replace(PROCESSOR, "");
            int processorIndex = Integer.parseInt(processorIndexString);
            numberOfProcessors = Math.max(numberOfProcessors, processorIndex);
        }
        return numberOfProcessors + 1;
    }

    void checkParallelBoundary() {
        ParallelZeroFileManager fileManager = new ParallelZeroFileManager(baseDir, processors);
        ZeroFolderStructure structure = fileManager.checkFileSystem();

        parallel_zero = structure.isBoundaryFieldInZero();
        parallel_constant = structure.isBoundaryFieldInConstant();
        parallel_zero_multiregion = structure.isBoundaryFieldInZeroMultiRegion();
        parallel_constant_multiregion = structure.isBoundaryFieldInConstantMultiRegion();
    }

    void checkSerialBoundary() {
        SerialZeroFileManager fileManager = new SerialZeroFileManager(baseDir);
        ZeroFolderStructure structure = fileManager.checkFileSystem();

        serial_zero = structure.isBoundaryFieldInZero();
        serial_constant = structure.isBoundaryFieldInConstant();
        serial_zero_multiregion = structure.isBoundaryFieldInZeroMultiRegion();
        serial_constant_multiregion = structure.isBoundaryFieldInConstantMultiRegion();
    }

    private void askToUser(ProjectFolderStructure checkList) {
        if (wid == WhenInDoubt.READ_PARALLEL) {
            System.err.println("Case folder contains a serial AND a parallel case.\nSwitch to parallel!");//Please select which case to load: use '-serial' or '-parallel' option");
            logger.debug("Is Batch, case type is PARALLEL");
            checkList.setParallel(true);
            checkList.setProcessors(processors);
        } else if (wid == WhenInDoubt.ASK_USER) {
            Object[] options = { SERIAL_CHOICE, PARALLEL_CHOICE };
            Window parentComponent = monitor != null ? monitor.getDialog() : UiUtil.getActiveWindow();
            int answer = JOptionPane.showOptionDialog(parentComponent, "Project contains serial AND parallel case.\nPlease select which case to load", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            logger.debug("Asking user");
            if (answer == 0) {
                checkList.setParallel(false);
                checkList.setProcessors(-1);
            } else {
                checkList.setParallel(true);
                checkList.setProcessors(processors);
            }
        }
    }

    void checkIntoDecomposePar(ProjectFolderStructure checkList) {
        File decomposePar = new File(new File(baseDir, SystemFolder.SYSTEM), DecomposeParDict.DECOMPOSE_PAR_DICT);
        if (decomposePar.exists()) {
            Dictionary dPar = DictionaryUtils.readDictionary(decomposePar, new SilentMonitor());
            String nPar = dPar.lookup(DecomposeParDict.NUMBER_OF_SUBDOMAINS_KEY);
            
            try {
                int n = Integer.parseInt(nPar);
                checkList.setParallel(n > 1);
                checkList.setProcessors(n);
            } catch (Exception e) {
            }
        }
    }

    public boolean isParallel_Constant() {
        return parallel_constant;
    }

    public boolean isParallel_Constant_MultiRegion() {
        return parallel_constant_multiregion;
    }

    public boolean isParallel_Zero() {
        return parallel_zero;
    }

    public boolean isParallel_Zero_MultiRegion() {
        return parallel_zero_multiregion;
    }

    public boolean isSerial_Constant() {
        return serial_constant;
    }

    public boolean isSerial_Zero() {
        return serial_zero;
    }

    public boolean isSerial_Constant_MultiRegion() {
        return serial_constant_multiregion;
    }
    
    public boolean isSerial_Zero_MultiRegion() {
        return serial_zero_multiregion;
    }
    
    public static boolean isSuitable(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            File constant = new File(file, CONSTANT);
            File system = new File(file, SYSTEM);
            
            if (constant.exists() && constant.isDirectory() && system.exists() && system.isDirectory()) {
                File controlDict = new File(system, CONTROL_DICT);
                File fvSchemes = new File(system, FV_SCHEMES);
                File fvSolution = new File(system, FV_SOLUTION);
                return fvSchemes.exists() && controlDict.exists() && fvSolution.exists();
            }
            return false;
        }
        return false;
    }
}
